package com.example.order.service.impl;

import com.example.order.adapter.SimpleEnumStateMachineConfiguration;
import com.example.order.dao.OrderRepository;
import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.access.StateMachineAccess;
import org.springframework.statemachine.access.StateMachineFunction;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptor;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    public static final String ORDER_ID_HEADER = "orderId";

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StateMachineFactory<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> factory;

    @Override
    public Order create(Date date) {
        return orderRepository.save(new Order(date, SimpleEnumStateMachineConfiguration.OrderStates.SUBMITTED));
    }

    /**
     * 支付
     * @param orderId 订单主键
     * @return
     */
    @Override
    public StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> fulfill(Long orderId) {
        StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> sm = this.build(orderId);

        Message<SimpleEnumStateMachineConfiguration.OrderEvents> fulfillmentMessage = MessageBuilder.withPayload(SimpleEnumStateMachineConfiguration.OrderEvents.FULFILL)
                .setHeader(ORDER_ID_HEADER, orderId)
                .build();

        sm.sendEvent(fulfillmentMessage);
        return sm;
    }

    /**
     * 支付
     * @param orderId 订单主键
     * @param paymentConfirmationNumber 订单号
     * @return
     */
    @Override
    public StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> pay(Long orderId, String paymentConfirmationNumber) {
        StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> sm = this.build(orderId);

        Message<SimpleEnumStateMachineConfiguration.OrderEvents> payMessage = MessageBuilder.withPayload(SimpleEnumStateMachineConfiguration.OrderEvents.PAY)
                .setHeader(ORDER_ID_HEADER, orderId)
                .setHeader("paymentConfirmationNumber", paymentConfirmationNumber)
                .build();

        sm.sendEvent(payMessage);
        return sm;
    }

    private StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> build(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        String orderIdKey = Long.toString(order.getId());
        StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> sm = this.factory.getStateMachine(orderIdKey);

        //恢复状态机后恢复状态
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(new StateMachineFunction<StateMachineAccess<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents>>() {
                    @Override
                    public void apply(StateMachineAccess<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> sma) {

                        //拦截器
                        sma.addStateMachineInterceptor(new StateMachineInterceptorAdapter<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents>() {
                            /**
                             * 状态改变之前
                             * @param state
                             * @param message
                             * @param transition
                             * @param stateMachine
                             * @param stateMachine1
                             */
                            @Override
                            public void preStateChange(State<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> state, Message<SimpleEnumStateMachineConfiguration.OrderEvents> message, Transition<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> transition, StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> stateMachine, StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> stateMachine1) {
                                Optional.ofNullable(message).ifPresent(msg -> {
                                    Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault("orderId", -1L)))
                                            .ifPresent(orderId -> {
                                                Order order = orderRepository.findById(orderId).get();
                                                order.setOrderState(state.getId());
                                                orderRepository.save(order);
                                            });
                                });
                            }
                        });

                        //恢复状态
                        sma.resetStateMachine(new DefaultStateMachineContext<>(
                                order.getOrderState(), null, null, null
                        ));
                    }
                });
        sm.start();

        return sm;
    }
}
