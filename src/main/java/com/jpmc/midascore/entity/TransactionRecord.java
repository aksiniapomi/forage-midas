package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity //make a db table for this class
public class TransactionRecord {

    @Id //unique ID for each row
    @GeneratedValue(strategy = GenerationType.IDENTITY) //this db will auto-generate the ID
    private Long id;

    @ManyToOne //many receipts can point to the same sender person
    @JoinColumn(name = "sender_id") //in the receipts table, store the sender's ID column called sender_id
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserRecord recipient;

    @Column(nullable = false)
    private float amount; //how much money moved

    private float incentive;

    public TransactionRecord() {}

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount, float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
    }

    public Long getId() { return id; }
    public UserRecord getSender() { return sender; }
    public UserRecord getRecipient() { return recipient; }
    public float getAmount() { return amount; }
    public float getIncentive() { return incentive; }
}
