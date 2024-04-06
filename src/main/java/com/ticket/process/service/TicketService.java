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
import java.util.List;
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

        List<Future> futures = new ArrayList<>();
        for (Callable validationTask: validationTasks) {
            Future<ResponseEntity<ValidateResultDTO>> future = concurrentTaskExecutor.submit(validationTask);
            futures.add(future);
        }

        try {
            for (Future future: futures) {
                ResponseEntity<ValidateResultDTO> r = (ResponseEntity<ValidateResultDTO>) future.get();
                log.info(r.getBody().toString());
                if (!r.getBody().isSuccess()) {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return result;
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
