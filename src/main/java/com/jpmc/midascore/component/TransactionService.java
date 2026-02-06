package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;

    public TransactionService(UserRepository userRepository,
                              TransactionRecordRepository transactionRecordRepository,
                              RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void process(Transaction tx) {
        Optional<UserRecord> senderOpt = userRepository.findById(tx.getSenderId());
        Optional<UserRecord> recipientOpt = userRepository.findById(tx.getRecipientId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) return;

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        float amount = tx.getAmount();

        if (sender.getBalance() < amount) return;

        // Call incentive API AFTER transaction is valid (safe fallback to 0)
        float incentive = 0f;
        try {
            Incentive incentiveObj = restTemplate.postForObject(
                    "http://localhost:8080/incentive",
                    tx,
                    Incentive.class
            );
            if (incentiveObj != null) {
                incentive = incentiveObj.getAmount();
            }
        } catch (Exception e) {
            // Incentive service unavailable / wrong port / refused connection
            // -> treat incentive as 0 so processing still completes

            System.out.println("Incentive call failed: " + e.getMessage());
        }
        // update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentive);

        // record transaction (with incentive)
        TransactionRecord record = new TransactionRecord(sender, recipient, amount, incentive);
        transactionRecordRepository.save(record);

        userRepository.save(sender);
        userRepository.save(recipient);
    }
}
