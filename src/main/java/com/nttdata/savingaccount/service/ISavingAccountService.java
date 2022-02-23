package com.nttdata.savingaccount.service;

import com.nttdata.savingaccount.entity.SavingAccount;
import com.nttdata.savingaccount.entity.dto.CreditCard;
import com.nttdata.savingaccount.entity.dto.Customer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ISavingAccountService {
  
  Mono<SavingAccount> findById(String id);
  
  Flux<SavingAccount> findAll();  
  
  Mono<SavingAccount> create(SavingAccount savingAccount);
  
  Mono<SavingAccount> update(SavingAccount savingAccount);
  
  Mono<Boolean> delete(String id);
  
  Mono<Customer>findCustomerNumber(String number);
  
  Mono<Customer>findCustomer(String id);

  Mono<Long>findCustomerAccountBank(String id);
  
  Mono<Long>findCustomerAccountBankDocumentNumber(String number);
  
  Flux<CreditCard> findCreditCardByCustomer(String id);
  
  Mono<SavingAccount> findByCardNumber(String number);
  
  Flux<SavingAccount> findCustomerById(String id);

}
