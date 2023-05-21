package com.mindhub.homebanking;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.List;


import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest

@AutoConfigureTestDatabase(replace = NONE)

public class RepositoriesTest {

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Test
    public void existLoans(){
        List<Loan> loans = loanRepository.findAll();
        assertThat(loans,is(not(empty())));
    }

    @Test
    public void existPersonalLoan(){
        List<Loan> loans = loanRepository.findAll();
        assertThat(loans, hasItem(hasProperty("name", is("Personal Loan"))));
    }

    @Test
    public  void cardFoundByNumber(){
        Card card= cardRepository.findByNumber("2234-6745-5520-7888");
        assertThat(card,notNullValue());
    }

    @Test
    public void noCardFoundByCvvNotExisting(){
        Card card= cardRepository.findByCvv(111);
        assertThat(card,nullValue());
    }

    @Test
    public  void accountNumberStartsWithVIN(){
        List<Account> accounts=accountRepository.findAll();
        assertThat(accounts, hasItem(hasProperty("number", startsWith("VIN"))));
    }

    @Test
    public void balanceGreaterThanOrEqualToZero(){
        List<Account> accounts=accountRepository.findAll();
        assertThat(accounts, hasItem(hasProperty("balance", greaterThanOrEqualTo(0.0))));
    }

    @Test
    public void mailContainsAnAtSign(){
        List<Client> clients= clientRepository.findAll();
        assertThat(clients, hasItem(hasProperty("email", containsString("@"))));
    }

  @Test
    public void transactionsHasPropertyDescription(){
      List<Transaction> transactions= transactionRepository.findAll();
      assertThat(transactions, hasItem(hasProperty("description")));
  }


}