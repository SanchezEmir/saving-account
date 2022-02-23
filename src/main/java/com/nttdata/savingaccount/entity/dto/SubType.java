package com.nttdata.savingaccount.entity.dto;

import com.nttdata.savingaccount.entity.enums.ESubType;

import lombok.Data;

@Data
public class SubType {

  private String id;

  private ESubType value;

}
