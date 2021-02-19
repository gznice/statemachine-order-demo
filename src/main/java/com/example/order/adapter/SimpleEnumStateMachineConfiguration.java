package com.example.order.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

/**
 * 订单状态机demo
 *
 * @Configuration 配置引擎本身
 * @EnableStateMachineFactory 状态机实例
 */
@Slf4j
@Configuration
@EnableStateMachineFactory
public class SimpleEnumStateMachineConfiguration extends StateMachineConfigurerAdapter<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> {

    public enum OrderStates {
        SUBMITTED,
        PAID,
        FULFILLED,
        CANCELLED
    }
    public enum OrderEvents {
        FULFILL,
        PAY,
        CANCEL
    }

    /**
     * 更改引擎配置点
     * @param config
     * @throws Exception
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStates, OrderEvents> config) throws Exception {

        //监听适配器，监听状态变化
        StateMachineListenerAdapter<OrderStates, OrderEvents> adapter = new StateMachineListenerAdapter<OrderStates, OrderEvents>() {
            @Override
            public void stateChanged(State<OrderStates, OrderEvents> from, State<OrderStates, OrderEvents> to) {
                log.info(String.format("stateChanged(from: %s to: %s)", from + "", to + ""));
            }
        };

        //引擎配置
        config.withConfiguration()
                .autoStartup(false)//关闭自动启动
                .listener(adapter)//监听器
                ;
    }

    /**
     * 根据业务流程配置状态和事件
     * @param transitions
     * @throws Exception
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStates, OrderEvents> transitions) throws Exception {
        //withExternal 外部过渡权
        //withLocal 本地过渡权
        transitions
                .withExternal().source(OrderStates.SUBMITTED).target(OrderStates.PAID).event(OrderEvents.PAY)
                .and()
                .withExternal().source(OrderStates.PAID).target(OrderStates.FULFILLED).event(OrderEvents.FULFILL)
                .and()
                .withExternal().source(OrderStates.SUBMITTED).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL)
                .and()
                .withExternal().source(OrderStates.PAID).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL)
                .and()
                .withExternal().source(OrderStates.FULFILLED).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL)
                .and()
        ;
    }

    /**
     * 定义状态
     * @param states
     * @throws Exception
     */
    @Override
    public void configure(StateMachineStateConfigurer<OrderStates, OrderEvents> states) throws Exception {
        states
            .withStates()
            .initial(OrderStates.SUBMITTED) //最初
            .state(OrderStates.PAID)
            .end(OrderStates.FULFILLED)
            .end(OrderStates.CANCELLED);
    }
}
