package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Listens for incoming Kafka transactions, validates them against the database
 * Persists valid transactions while updating sender and recipient balances
 */
@Component
public class TransactionListener {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;

    // Spring automatically injects the repository implementation at runtime
    public TransactionListener(UserRepository userRepository, TransactionRecordRepository transactionRecordRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
    }
    // Listens to the Kafka topic defined in application.yml and deserializes incoming messages into Transaction objects
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core")
    public void listen(Transaction transaction) {
        // Look up sender and recipient in the database by their IDs
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // Discard transaction if either user doesn't exist or sender has insufficient funds
        if (sender == null || recipient == null || sender.getBalance() < transaction.getAmount()) {
            return;
        }

        // Call Incentive API to get bonus amount for this transaction
        Incentive incentive = restTemplate.postForObject("http://localhost:8080/incentive", transaction, Incentive.class);
        float incentiveAmount = incentive != null ? incentive.getAmount() : 0f;

        // Deduct amount from sender and credit recipient respectively
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + incentiveAmount);

        // Persist updated balances
        userRepository.save(sender);
        userRepository.save(recipient);

        // Record the valid transaction in the database
        transactionRecordRepository.save(new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount));
        }
    }
