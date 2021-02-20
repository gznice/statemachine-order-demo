package com.example.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;

@WithStateMachine(id = "orderStateMachine")
@Slf4j
public class OrderBean {

    /**
     * 订单从提交到支付事件的监听
     * @param message
     */
    @OnTransition(source = "SUBMITTED", target = "PAID")
    public void toStatePaid(Message message) {
        String orderId = message.getHeaders().get("orderId").toString();
        log.info("----------toStatePaid,{}", orderId);
        log.info("下面请开始写业务逻辑：。。。无需判断状态和事件，无需关心订单状态");
    }
}
