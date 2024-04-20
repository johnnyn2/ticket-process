package com.ticket.process.resttemplate;

import com.ticket.process.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

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

    @Value("${ticket.system.findConcertByCode}")
    private String findConcertByCodeUrl;

    @Value("${ticket.system.getAccountInfoByAccountNo}")
    private String getAccountInfoByAccountNoUrl;

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

    public ResponseEntity<ConcertDTO> findConcertByCode(String code) {
        String url = systemUrl + findConcertByCodeUrl;
        log.info("url: " + url);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("code", "{code}")
                .encode()
                .toUriString();
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        return restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, ConcertDTO.class, params);
    }

    public ResponseEntity<AccountDTO> getAccountInfoByAccountNo(String accountNo) {
        String url = systemUrl + getAccountInfoByAccountNoUrl;
        log.info("url: " + url);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("accountNo", "{accountNo}")
                .encode()
                .toUriString();
        Map<String, String> params = new HashMap<>();
        params.put("accountNo", accountNo);
        return restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, AccountDTO.class, params);
    }
}
