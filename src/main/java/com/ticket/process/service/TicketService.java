package com.ticket.process.service;

import com.ticket.process.dto.*;
import com.ticket.process.resttemplate.SystemRestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@Service
@Slf4j
public class TicketService {

    private SystemRestTemplate systemRestTemplate;
    @Autowired
    private ThreadPoolTaskExecutor  concurrentTaskExecutor;

    public TicketService(SystemRestTemplate systemRestTemplate) {
        this.systemRestTemplate = systemRestTemplate;
    }

    public ResponseEntity<ValidateResultDTO> validateAccount(PurchaseDTO purchaseDTO) {
        AccountDTO accountDTO = new AccountDTO();
        BeanUtils.copyProperties(purchaseDTO, accountDTO);
        HttpEntity<AccountDTO> request = new HttpEntity<>(accountDTO);
        return systemRestTemplate.validateAccount(request);
    }

    public ResponseEntity<ValidateResultDTO> validateSeatAvailability(PurchaseDTO purchaseDTO) {
        SeatDTO seatDTO = new SeatDTO();
        BeanUtils.copyProperties(purchaseDTO, seatDTO);
        HttpEntity<SeatDTO> request = new HttpEntity<>(seatDTO);
        return systemRestTemplate.validateSeatAvailability(request);
    }


    public PurchaseResultDTO concurrentPurchase(PurchaseDTO purchaseDTO) {
        PurchaseResultDTO result = new PurchaseResultDTO();
        Callable<ResponseEntity<ValidateResultDTO>> validateAccount = () -> this.validateAccount(purchaseDTO);
        Callable<ResponseEntity<ValidateResultDTO>> validateSeatAvailability = () -> this.validateSeatAvailability(purchaseDTO);
        List<Callable<ResponseEntity<ValidateResultDTO>>> validationTasks = new ArrayList<>();
        validationTasks.add(validateAccount);
        validationTasks.add(validateSeatAvailability);

        // experiment result: concurrent API calls are faster than synchronous calls
        List<Future> futures = new ArrayList<>();
//        for (Callable validationTask: validationTasks) {
//            Future<ResponseEntity<ValidateResultDTO>> future = concurrentTaskExecutor.submit(validationTask);
//            futures.add(future);
//        }
        Future<ResponseEntity<ValidateResultDTO>> validateAccountFuture = concurrentTaskExecutor.submit(validateAccount);
        Future<ResponseEntity<ValidateResultDTO>> validateSeatAvailabilityFuture = concurrentTaskExecutor.submit(validateSeatAvailability);

//        try {
//            for (Future future: futures) {
//                ResponseEntity<ValidateResultDTO> r = (ResponseEntity<ValidateResultDTO>) future.get();
//                log.info(r.getBody().toString());
//                if (!r.getBody().isSuccess()) {
//                    return null;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
        try {
            // validate account and seat availability
            ResponseEntity<ValidateResultDTO> validateAccountResponse = (ResponseEntity<ValidateResultDTO>) validateAccountFuture.get();
            ResponseEntity<ValidateResultDTO> validateSeatAvailabilityResponse = (ResponseEntity<ValidateResultDTO>) validateSeatAvailabilityFuture.get();
            ValidateResultDTO validateAccountResponseBody = validateAccountResponse.getBody();
            ValidateResultDTO validateSeatAvailabiltyResponseBody = validateSeatAvailabilityResponse.getBody();
            if (!validateAccountResponseBody.isSuccess() || !validateSeatAvailabiltyResponseBody.isSuccess()) {
                return null;
            }
            // find concert and get account info
            Callable<ResponseEntity<ConcertDTO>> findConcertByCode = () -> systemRestTemplate.findConcertByCode(purchaseDTO.getConcertCode());
            Callable<ResponseEntity<AccountDTO>> getAccountInfoByAccountNo = () -> systemRestTemplate.getAccountInfoByAccountNo(purchaseDTO.getAccountNo());
            Future<ResponseEntity<ConcertDTO>> findConcertByCodeFuture = concurrentTaskExecutor.submit(findConcertByCode);
            Future<ResponseEntity<AccountDTO>> getAccountInfoByAccountNoFuture = concurrentTaskExecutor.submit(getAccountInfoByAccountNo);
            ResponseEntity<ConcertDTO> findConcertByCodeResponse = findConcertByCodeFuture.get();
            ResponseEntity<AccountDTO> getAccountInfoByAccountNoResponse = getAccountInfoByAccountNoFuture.get();
            if (!findConcertByCodeResponse.hasBody() || !getAccountInfoByAccountNoResponse.hasBody()) {
                return null;
            }
            // generate tickets
            AccountDTO accountDTO = getAccountInfoByAccountNoResponse.getBody();
            TicketDTO ticket = new TicketDTO();
            UUID ticketNo = UUID.randomUUID();
            ticket.setTicktNo(ticketNo.toString());
            ticket.setConcert(findConcertByCodeResponse.getBody());
            ticket.setSeat(purchaseDTO.getSeatDTO());
            ticket.setLastName(accountDTO.getLastName());
            ticket.setFirstName(accountDTO.getFirstName());
            ticket.setAccountNo(purchaseDTO.getAccountNo());
            UUID purchaseRef = UUID.randomUUID();
            ticket.setPurchaseRef(purchaseRef.toString());
            result.setTickets(Collections.singletonList(ticket));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public PurchaseResultDTO synchronousPurchase(PurchaseDTO purchaseDTO) {
        PurchaseResultDTO result = new PurchaseResultDTO();
        ResponseEntity<ValidateResultDTO> r1 = this.validateAccount(purchaseDTO);
        ResponseEntity<ValidateResultDTO> r2 =this.validateSeatAvailability(purchaseDTO);
        if (!r1.getBody().isSuccess() || !r2.getBody().isSuccess()) {
            return null;
        }
        return result;
    }
}
