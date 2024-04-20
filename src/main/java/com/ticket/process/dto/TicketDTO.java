package com.ticket.process.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private String ticktNo;
    private String purchaseRef;
    private String accountNo;
    private String firstName;
    private String lastName;
    private ConcertDTO concert;
    private List<SeatDTO> seat;
}
