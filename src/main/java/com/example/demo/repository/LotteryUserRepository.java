package com.example.demo.repository;

import com.example.demo.model.LotteryUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author long_w
 */
public interface LotteryUserRepository extends JpaRepository<LotteryUser, Long> {

    @Query("SELECT r FROM LotteryUser r WHERE r.username = :username")
    Optional<LotteryUser> findByUsername(String username);

}
