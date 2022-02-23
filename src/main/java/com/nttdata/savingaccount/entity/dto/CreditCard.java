package com.nttdata.savingaccount.entity.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditCard {
  
  private String id;
  
  private String cardNumber;
  
  private Customer customer;
  
  private Double limitCredit;
  
  private LocalDate expiration;
  
  private LocalDateTime createAt;

}
