package com.ticket.process.resttemplate;

import com.ticket.process.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class SystemRestTemplate {
    @Value("${ticket.system.url}")
    private String systemUrl;

    @Value("${ticket.system.validatePurchase}")
    private String validatePurchaseUrl;

    @Value("${ticket.system.validateAccount}")
    private String validateAccountUrl;

    @Value("${ticket.system.validateSeat}")
    private String validateSeatUrl;

    @Value("${ticket.system.purchase}")
    private String purchaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity asyncValidatePurchaseTicketRequest(HttpEntity request) {
        log.info("url: " + systemUrl + validatePurchaseUrl);
        return restTemplate.exchange(systemUrl + validatePurchaseUrl, HttpMethod.POST, request, PurchaseResultDTO.class);
    }

    public ResponseEntity<ValidateResultDTO> validateAccount(HttpEntity<AccountDTO> request) {
        log.info("url: " + systemUrl + validateAccountUrl);
        return restTemplate.exchange(systemUrl + validateAccountUrl, HttpMethod.POST, request, ValidateResultDTO.class);
    }

    public ResponseEntity<ValidateResultDTO> validateSeatAvailability(HttpEntity<SeatDTO> request) {
        log.info("url: " + systemUrl + validateSeatUrl);
        return restTemplate.exchange(systemUrl + validateSeatUrl, HttpMethod.POST, request, ValidateResultDTO.class);
    }

    public ResponseEntity<PurchaseResultDTO> purchaseTicket(HttpEntity<PurchaseDTO> request) {
        log.info("url: " + systemUrl + purchaseUrl);
        return restTemplate.exchange(systemUrl + purchaseUrl, HttpMethod.POST, request, PurchaseResultDTO.class);
    }
}
