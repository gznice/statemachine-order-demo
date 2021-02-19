package com.example.order;

import com.example.order.adapter.SimpleEnumStateMachineConfiguration;
import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class OrderRunner implements ApplicationRunner {

    @Autowired
    private OrderService orderService;

    private final StateMachineFactory<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> factory;
    OrderRunner(StateMachineFactory<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> factory) {
        this.factory = factory;
    }


    /**
     * 测试状态，没有持久化
     * @param args
     * @throws Exception
     */
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> machine = this.factory.getStateMachine("10000");
//        machine.start();
//        log.info("current state: " + machine.getState().getId().name());
//        machine.sendEvent(SimpleEnumStateMachineConfiguration.OrderEvents.PAY);
//        log.info("current state2: " + machine.getState().getId().name());
//
//        Message<SimpleEnumStateMachineConfiguration.OrderEvents> eventsMessage = MessageBuilder.withPayload(SimpleEnumStateMachineConfiguration.OrderEvents.FULFILL)
//                .setHeader("a", "b")
//                .build();
//        machine.sendEvent(eventsMessage);
//        log.info("current state3:" + machine.getState().getId().name());
//
//    }


    /**
     * 测试订单，有持久化
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Order order = orderService.create(new Date());
        log.info("after calling create():" + order.getOrderState().name());

        StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> payStateMachine =
                orderService.pay(order.getId(), UUID.randomUUID().toString());
        log.info("after calling pay():" + payStateMachine.getState().getId().name());

        StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> fulfillStateMachine =
                orderService.fulfill(order.getId());
        log.info("after calling fulfill():" + fulfillStateMachine.getState().getId().name());


    }
}
