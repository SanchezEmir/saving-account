package com.nttdata.savingaccount.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.savingaccount.entity.SavingAccount;
import com.nttdata.savingaccount.entity.enums.ETypeCustomer;
import com.nttdata.savingaccount.service.ISavingAccountService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/savingAccount")
public class SavingAccountController {
  
  @Autowired
  ISavingAccountService service;
  
  @GetMapping("/list")
  public Flux<SavingAccount> list(){
      log.info("listar saving account");
      return service.findAll();
  }

  @GetMapping("/find/{id}")
  public Mono<SavingAccount> findById(@PathVariable String id){
      log.info("buscando saving account");
      return service.findById(id);
  }

  @GetMapping("/findAccountByCustomerId/{id}")
  public Flux<SavingAccount> findAccountByCustomerId(@PathVariable String id){
      log.info("buscando saving account");
      return service.findCustomerById(id);
  }

  @PostMapping("/create")
  public Mono<ResponseEntity<SavingAccount>> create(@Valid @RequestBody SavingAccount savingAccount){
      log.info("buscando cliente por DocumentNumber");
      return service.findCustomerNumber(savingAccount.getCustomer().getDocumentNumber())
              .filter(customer -> customer.getTypeCustomer().getValue().equals(ETypeCustomer.PERSONAL) && savingAccount.getBalance() >= 0)
              .flatMap(customer -> {
                  log.info("cliente encontrado");
                  return service.findCustomerAccountBankDocumentNumber(savingAccount.getCustomer().getDocumentNumber()) // COUNT CUENTAS AHORRO
                          .filter(count -> count < 1)
                          .flatMap(count -> {
                              switch (customer.getTypeCustomer().getSubType().getValue()) {
                                  case VIP:   return service.findCreditCardByCustomer(customer.getId())
                                          .count()
                                          .filter(cnt -> cnt > 0
                                                  & savingAccount.getMinAverageVip() != null & savingAccount.getMinAverageVip() > 0.0
                                                  & savingAccount.getBalance() != null & savingAccount.getBalance() >= 0.0
                                                  & savingAccount.getBalance() >= calculateAveregaMin(savingAccount.getMinAverageVip()))
                                          .flatMap(cnt -> {
                                              savingAccount.setCustomer(customer);
                                              savingAccount.setCreatedAt(LocalDateTime.now());
                                              log.info("asignando cliente VIP");
                                              return service.create(savingAccount);
                                          });

                                  case NORMAL: savingAccount.setCustomer(customer);
                                      savingAccount.setCreatedAt(LocalDateTime.now());
                                      savingAccount.setBalance(savingAccount.getBalance() != null ? savingAccount.getBalance() : 0.0);
                                      log.info("asignando cliente NORMAL");
                                      return service.create(savingAccount);
                                  default: return Mono.empty();
                              }
                          })
                          .map(savedSavingAccount -> new ResponseEntity<>(savedSavingAccount , HttpStatus.CREATED));
              })
              .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @PutMapping("/update")
  public Mono<ResponseEntity<SavingAccount>> update(@RequestBody SavingAccount savingAccount) {

      return service.findById(savingAccount.getId())
              .filter(saDB -> savingAccount.getBalance() >= 0)
              .flatMap(saDB -> service.findCustomer(savingAccount.getCustomer().getId())
                      .filter(customer -> customer.getTypeCustomer().getValue().equals(ETypeCustomer.PERSONAL))
                      .flatMap(customer -> {
                                  log.info("entro en proceso");
                                  switch (customer.getTypeCustomer().getSubType().getValue()) {
                                      case VIP:   return service.findCreditCardByCustomer(customer.getId())
                                              .count()
                                              .filter(cnt -> cnt > 0
                                                      & savingAccount.getMinAverageVip() != null & savingAccount.getMinAverageVip() > 0.0
                                                      & savingAccount.getBalance() != null & savingAccount.getBalance() >= 0.0
                                                      & savingAccount.getBalance() >= calculateAveregaMin(savingAccount.getMinAverageVip()))
                                              .flatMap(cnt -> {
                                                  savingAccount.setCustomer(customer);
                                                  savingAccount.setCreatedAt(LocalDateTime.now());
                                                  return service.create(savingAccount);
                                              });

                                      case NORMAL: savingAccount.setCustomer(customer);
                                          savingAccount.setCreatedAt(LocalDateTime.now());
                                          savingAccount.setBalance(savingAccount.getBalance() != null ? savingAccount.getBalance() : 0.0);
                                          return service.create(savingAccount);
                                      default: return Mono.empty();
                                  }
                              }
                      )
              )
              .map(savedSavingAccount -> new ResponseEntity<>(savedSavingAccount , HttpStatus.CREATED))
              .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @DeleteMapping("/delete/{id}")
  public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
      return service.delete(id)
              .filter(deleteSavingAccount -> deleteSavingAccount)
              .map(deleteCustomer -> new ResponseEntity<>("Account Deleted", HttpStatus.ACCEPTED))
              .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  public Double calculateAveregaMin(Double minAverageVip){
      Integer daysRemaining = LocalDate.now().lengthOfMonth() - LocalDate.now().getDayOfMonth();
      return minAverageVip*LocalDate.now().getDayOfMonth()/daysRemaining;
  }

  @GetMapping("/findByAccountNumber/{number}")
  public Mono<SavingAccount> findByAccountNumber(@PathVariable String number){
      return service.findByCardNumber(number);
  }

  @PutMapping("/updateTransference")
  public Mono<ResponseEntity<SavingAccount>> updateForTransference(@Valid @RequestBody SavingAccount savingAccount) {
      return service.create(savingAccount)
              .filter(customer -> savingAccount.getBalance() >= 0)
              .map(ft -> new ResponseEntity<>(ft, HttpStatus.CREATED));
  }

}
