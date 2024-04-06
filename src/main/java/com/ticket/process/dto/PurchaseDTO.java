package com.ticket.process.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDTO {
    private String accountNo;
    private List<SeatDTO> seatDTO;
    private String concertCode;
    private String sessionCode;
}
