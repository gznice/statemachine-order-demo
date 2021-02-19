package com.example.order.service;

import com.example.order.adapter.SimpleEnumStateMachineConfiguration;
import com.example.order.entity.Order;
import org.springframework.statemachine.StateMachine;

import java.util.Date;

public interface OrderService {
    Order create(Date date);

    StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> fulfill(Long orderId);

    StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> pay(Long orderId, String paymentConfirmationNumber);
}
