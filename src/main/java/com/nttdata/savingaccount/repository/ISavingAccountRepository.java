package com.nttdata.savingaccount.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.nttdata.savingaccount.entity.SavingAccount;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ISavingAccountRepository extends ReactiveMongoRepository<SavingAccount, String> {
  
  Flux<SavingAccount> findByCustomerId(String id);
  
  Flux<SavingAccount> findByCustomerDocumentNumber(String documentNumber);
  
  Mono<SavingAccount> findByCardNumber(String number);

}
