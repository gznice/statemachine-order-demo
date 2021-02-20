package com.example.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;

@WithStateMachine(id = "orderStateMachine")
@Slf4j
public class OrderBean {
    @OnTransition(source = "SUBMITTED", target = "PAID")
    public void toStatePaid(Message message) {
        String orderId = message.getHeaders().get("orderId").toString();
        log.info("----------toStatePaid,{}", orderId);
    }
}
