package com.nttdata.savingaccount.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nttdata.savingaccount.entity.dto.Customer;
import com.nttdata.savingaccount.entity.dto.Managers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "saving_account")
@AllArgsConstructor
@NoArgsConstructor
public class SavingAccount {
  
  @Id
  private String id;
  
  @NotNull
  private Customer customer;
  
  @NotNull
  private String cardNumber;
  
  @NotNull
  private Integer limitTransactions;
  
  @NotNull
  private Integer freeTransactions;
  
  @NotNull
  private Double commissionTransactions;
  
  @NotNull
  private Double balance;
  
  private Double minAverageVip;
  
  private LocalDateTime createdAt;
  
  private List<Managers> owners;
  
  private List<Managers> signatories;

}
