// the 'worker' who receives the parcels from kafka

package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    @KafkaListener(topics = "${general.kafka-topic}") //whenever the message arrives on this topic, call this method and read the topic from application.yml, i.e. trader updates
    public void listen(Transaction transaction) {

        float amount = transaction.getAmount(); //reading the amount from the received transaction

        //TEMP debug print
        System.out.println("Received transaction amount: " + amount);
    }
}
