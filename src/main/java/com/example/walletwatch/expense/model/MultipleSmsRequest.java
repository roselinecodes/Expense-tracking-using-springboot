package com.example.walletwatch.expense.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleSmsRequest {


    @JsonProperty("TransactionDetails")
    List<SmsRequest> TransactionDetails;
    Long userId;
}
