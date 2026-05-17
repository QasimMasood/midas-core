package com.jpmc.midascore.repository;

import com.jpmc.midascore.TransactionRecord;
import org.springframework.data.repository.CrudRepository;

// Provides database save operations for validated TransactionRecord entities
public interface TransactionRecordRepository extends CrudRepository<TransactionRecord, Long> {
}
