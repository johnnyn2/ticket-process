package com.ticket.process.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateResultDTO {
    private boolean success;
    private String accountNo;
    private String lastName;
    private String firstName;
}
