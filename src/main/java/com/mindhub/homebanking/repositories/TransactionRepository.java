package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    @Query("SELECT T FROM Transaction T where T.account=:account and T.data>=:initialDate and T.data <= :finalDate")
    List<Transaction> findByDate(LocalDateTime initialDate, LocalDateTime finalDate, Account account);
}
