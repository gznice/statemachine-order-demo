package com.example.order.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.statemachine.event.OnStateMachineError;
@Slf4j
public class ErrorApplicationEventListener implements ApplicationListener<OnStateMachineError> {
    @Override
    public void onApplicationEvent(OnStateMachineError onStateMachineError) {
        // do something with error
        log.error("statemachine {} 出现异常 {}", onStateMachineError.getStateMachine(), onStateMachineError.getException());
    }
}
