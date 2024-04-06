package com.ticket.process.controller;

import com.ticket.process.dto.PurchaseDTO;
import com.ticket.process.dto.PurchaseResultDTO;
import com.ticket.process.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/async/purchase", method = RequestMethod.POST)
    public ResponseEntity<String> asyncBuyTicket() {

        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/concurrent/purchase", method = RequestMethod.POST)
    public ResponseEntity<PurchaseResultDTO> concurrentBuyTicket(@RequestBody PurchaseDTO purchaseDTO) {
        return ResponseEntity.ok(ticketService.concurrentPurchase(purchaseDTO));
    }

    @RequestMapping(value = "/synchronous/purchase", method = RequestMethod.POST)
    public ResponseEntity<PurchaseResultDTO> syncBuyTicket(@RequestBody PurchaseDTO purchaseDTO) {
        return ResponseEntity.ok(ticketService.synchronousPurchase(purchaseDTO));
    }
}
