package com.example.demo.lottery.dao.repository;

import com.example.demo.lottery.dao.model.IdempotentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * @author long_w
 */
public interface IdempotentRecordRepository extends JpaRepository<IdempotentRecord, Long> {

    @Query("SELECT r FROM IdempotentRecord r WHERE r.idempotencyKey = :idempotencyKey")
    Optional<IdempotentRecord> findByIdempotencyKey(@Param("idempotencyKey") String idempotencyKey);

}