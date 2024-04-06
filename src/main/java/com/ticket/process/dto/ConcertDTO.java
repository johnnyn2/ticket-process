package com.ticket.process.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConcertDTO {
    private String title;
    private String startTime;
    private String endTime;
    private String country;
    private String district;
    private String location;
}
