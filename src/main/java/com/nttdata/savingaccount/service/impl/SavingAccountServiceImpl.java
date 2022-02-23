package com.nttdata.savingaccount.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.nttdata.savingaccount.entity.SavingAccount;
import com.nttdata.savingaccount.entity.dto.CreditCard;
import com.nttdata.savingaccount.entity.dto.Customer;
import com.nttdata.savingaccount.repository.ISavingAccountRepository;
import com.nttdata.savingaccount.service.ISavingAccountService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SavingAccountServiceImpl implements ISavingAccountService {

  private final WebClient webClient;
  private final ReactiveCircuitBreaker reactiveCircuitBreaker;

  @Value("${config.base.apigateway}")
  private String url;

  public SavingAccountServiceImpl(
      ReactiveResilience4JCircuitBreakerFactory circuitBreakerFactory) {
    this.webClient = WebClient.builder().baseUrl(this.url).build();
    this.reactiveCircuitBreaker = circuitBreakerFactory
        .create("customerSaving");
  }
  
  @Autowired
  ISavingAccountRepository repo;

  @Override
  public Mono<SavingAccount> findById(String id) {
    return repo.findById(id);
  }

  @Override
  public Flux<SavingAccount> findAll() {
    return repo.findAll();
  }

  @Override
  public Mono<SavingAccount> create(SavingAccount savingAccount) {
    return repo.save(savingAccount);
  }

  @Override
  public Mono<SavingAccount> update(SavingAccount savingAccount) {
    return repo.save(savingAccount);
  }

  @Override
  public Mono<Boolean> delete(String id) {
    return repo.findById(id)
        .flatMap(sa -> repo.delete(sa)
        .then(Mono.just(Boolean.TRUE)))
        .defaultIfEmpty(Boolean.FALSE);
  }

  @Override
  public Mono<Customer> findCustomerNumber(String number) {
    return reactiveCircuitBreaker.run(webClient.get()
                                 .uri(this.url + "/customer/documentNumber/{number}", number)
                                 .accept(MediaType.APPLICATION_JSON)
                                 .retrieve()
                                 .bodyToMono(Customer.class),
        throwable -> {
          return this.getDefaultCustomer();
        });
  }

  @Override
  public Mono<Customer> findCustomer(String id) {
    return reactiveCircuitBreaker.run(webClient.get()
                                .uri(this.url + "/customer/find/{id}",id)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(Customer.class),
        throwable -> {
            return this.getDefaultCustomer();
        });
  }

  @Override
  public Mono<Long> findCustomerAccountBank(String id) {
    return repo.findByCustomerId(id).count();
  }

  @Override
  public Mono<Long> findCustomerAccountBankDocumentNumber(String number) {
    return repo.findByCustomerDocumentNumber(number).count();
  }

  @Override
  public Flux<CreditCard> findCreditCardByCustomer(String id) {
    return reactiveCircuitBreaker.run(webClient.get()
                                .uri(this.url + "/creditcard/creditcard/find/{id}",id)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToFlux(CreditCard.class),
        throwable -> {
            return this.getDefaultCreditCard();
        });
  }

  @Override
  public Mono<SavingAccount> findByCardNumber(String number) {
    return repo.findByCardNumber(number);
  }

  @Override
  public Flux<SavingAccount> findCustomerById(String id) {
    return repo.findByCustomerId(id);
  }
  
  public Mono<Customer> getDefaultCustomer() {
    Mono<Customer> customer = Mono.just(new Customer());
    return customer;
  }
  
  public Flux<CreditCard> getDefaultCreditCard() {
    Flux<CreditCard> creditCard = Flux.just(new CreditCard());
    return creditCard;
  }

}
