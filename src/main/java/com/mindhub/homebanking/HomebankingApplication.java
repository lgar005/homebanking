package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static com.fasterxml.jackson.core.io.NumberInput.parseLong;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
		return (args) -> {
			// save a couple of client
			Client client1=new Client("Melba", "Morel"," melba@mindhub.com");
			Client client2=new Client("Manuel", "Casas"," manuelC@mindhub.com");
			clientRepository.save(client1);
			clientRepository.save(client2);
			//date 1
			LocalDateTime dateCreatedMelva=LocalDateTime.now().plusDays(1);
			DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
			String date1= fmt.format(dateCreatedMelva);
			LocalDateTime date1f=LocalDateTime.parse(date1,fmt);
			//date 2
			LocalDateTime dateCreationNow=LocalDateTime.now();
			String date2= fmt.format(dateCreationNow);
			LocalDateTime date2f=LocalDateTime.parse(date2,fmt);

			Account account1=new Account("VIN001", date2f,50000);
			Account account2=new Account("VIN002",date1f,75000);
			Account account3=new Account("VIN003", date2f,100000);
			Account account4=new Account("VIN004",date1f,70000);
			client1.addAccount(account1);
			client1.addAccount(account2);
			client2.addAccount(account3);
			client2.addAccount(account4);

			Transaction transaction1= new Transaction(TransactionType.CREDIT,7700, "credit transaction", date2f);
			Transaction transaction2= new Transaction(TransactionType.CREDIT,90, "credit transaction", date1f);
			Transaction transaction3= new Transaction(TransactionType.CREDIT,1000, "credit transaction",date2f);
			Transaction transaction4= new Transaction(TransactionType.CREDIT,900, "credit transaction", date1f);
			Transaction transaction5= new Transaction(TransactionType.DEBIT,700, "debit transaction", date2f);
			Transaction transaction6= new Transaction(TransactionType.DEBIT,300, "debit transaction", date1f);
			Transaction transaction7= new Transaction(TransactionType.DEBIT,770, "debit transaction", date2f);
			Transaction transaction8= new Transaction(TransactionType.DEBIT,880, "debit transaction", date1f);
			Transaction transaction9= new Transaction(TransactionType.DEBIT,100, "debit transaction", date2f);
			Transaction transaction10= new Transaction(TransactionType.CREDIT,8800, "credit transaction", date1f);
			account1.addTransaction(transaction1);
			account2.addTransaction(transaction2);
			account3.addTransaction(transaction3);
			account4.addTransaction(transaction4);
			account1.addTransaction(transaction5);
			account2.addTransaction(transaction6);
			account3.addTransaction(transaction7);
			account4.addTransaction(transaction8);
			account1.addTransaction(transaction9);
			account2.addTransaction(transaction10);
			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);
			accountRepository.save(account4);

			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);
			transactionRepository.save(transaction5);
			transactionRepository.save(transaction6);
			transactionRepository.save(transaction7);
			transactionRepository.save(transaction8);
			transactionRepository.save(transaction9);
			transactionRepository.save(transaction10);

			account1.setBalance(account1.getBalance()+transaction1.getAmount()-transaction5.getAmount()-transaction9.getAmount());
			account2.setBalance(account2.getBalance()+transaction2.getAmount()-transaction6.getAmount()+transaction10.getAmount());
			account3.setBalance(account3.getBalance()+transaction3.getAmount()-transaction7.getAmount());
			account4.setBalance(account4.getBalance()+transaction4.getAmount()-transaction8.getAmount());

			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);
			accountRepository.save(account4);
		};
	}



}
