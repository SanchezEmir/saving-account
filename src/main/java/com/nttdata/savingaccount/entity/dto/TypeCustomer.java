package com.nttdata.savingaccount.entity.dto;

import com.nttdata.savingaccount.entity.enums.ETypeCustomer;

import lombok.Data;

@Data
public class TypeCustomer {

  private String id;

  private ETypeCustomer value;

  private SubType subType;

}
