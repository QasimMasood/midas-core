package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/**
 * Represents a validated financial transaction persisted to the database
 * Maintains a many-to-one relationship with sender and recipient UserRecord entities
 */
@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue
    private long id;

    //The user sending funds - multiple transactions can reference the same sender
    @ManyToOne
    private UserRecord sender;

    // The user receiving funds - multiple transactions can reference the same recipient
    @ManyToOne
    private UserRecord recipient;

    private float amount;

    // Requirement for JPA
    protected TransactionRecord() {}

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }
}
