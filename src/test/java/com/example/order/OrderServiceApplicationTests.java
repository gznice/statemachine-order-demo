package com.example.order;

import com.example.order.adapter.SimpleEnumStateMachineConfiguration;
import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.Date;
import java.util.UUID;

//启动类和随机端口
@SpringBootTest(classes = OrderServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class OrderServiceApplicationTests {

	@Autowired
	private OrderService orderService;

	@Autowired
	private StateMachineFactory<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> factory;
//	OrderServiceApplicationTests(StateMachineFactory<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> factory) {
//		this.factory = factory;
//	}

	/**
	 * 测试状态，没有持久化
	 */
	@Test
	void testState() {
		StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> machine = this.factory.getStateMachine("orderStateMachine");
        machine.start();
        log.info("current state: " + machine.getState().getId().name());
        machine.sendEvent(SimpleEnumStateMachineConfiguration.OrderEvents.PAY);
        log.info("current state2: " + machine.getState().getId().name());

        Message<SimpleEnumStateMachineConfiguration.OrderEvents> eventsMessage = MessageBuilder.withPayload(SimpleEnumStateMachineConfiguration.OrderEvents.FULFILL)
                .setHeader("a", "b")
                .build();
        machine.sendEvent(eventsMessage);
        log.info("current state3:" + machine.getState().getId().name());
	}

	/**
	 * 测试订单，有持久化
	 * @throws Exception
	 */
	@Test
	public void testOrder() throws Exception {
		Order order = orderService.create(new Date());
		log.info("----------【statemachine1】订单号（{}），订单事件（{}），订单状态（{}）", order.getId(), "创建订单", order.getOrderState().name());
		order = orderService.getById(order.getId());
		log.info("----------【DB1】订单号（{}），订单时间（{}），订单状态（{}）", order.getId(), order.getDateTime(), order.getState());

		StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> payStateMachine =
				orderService.pay(order.getId(), UUID.randomUUID().toString());
		log.info("----------【statemachine2】订单号（{}），订单事件（{}），订单状态（{}）", payStateMachine.getId(), "订单支付", payStateMachine.getState().getId().name());
		order = orderService.getById(order.getId());
		log.info("----------【DB2】订单号（{}），订单时间（{}），订单状态（{}）", order.getId(), order.getDateTime(), order.getState());

		StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> fulfillStateMachine =
				orderService.fulfill(order.getId());
		log.info("----------【statemachine3】订单号（{}），订单事件（{}），订单状态（{}）", fulfillStateMachine.getId(), "订单签收完成", fulfillStateMachine.getState().getId().name());
		order = orderService.getById(order.getId());
		log.info("----------【DB3】订单号（{}），订单时间（{}），订单状态（{}）", order.getId(), order.getDateTime(), order.getState());

		this.multi();
	}

	private void multi() {
		Order order = orderService.create(new Date());
		log.info("----------【statemachine4】订单号（{}），订单事件（{}），订单状态（{}）", order.getId(), "创建订单", order.getOrderState().name());
		order = orderService.getById(order.getId());
		log.info("----------【DB4】订单号（{}），订单时间（{}），订单状态（{}）", order.getId(), order.getDateTime(), order.getState());

		StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> payStateMachine =
				orderService.pay(order.getId(), UUID.randomUUID().toString());
		log.info("----------【statemachine5】订单号（{}），订单事件（{}），订单状态（{}）", payStateMachine.getId(), "订单支付", payStateMachine.getState().getId().name());
		order = orderService.getById(order.getId());
		log.info("----------【DB5】订单号（{}），订单时间（{}），订单状态（{}）", order.getId(), order.getDateTime(), order.getState());

		StateMachine<SimpleEnumStateMachineConfiguration.OrderStates, SimpleEnumStateMachineConfiguration.OrderEvents> fulfillStateMachine =
				orderService.fulfill(order.getId());
		log.info("----------【statemachine6】订单号（{}），订单事件（{}），订单状态（{}）", fulfillStateMachine.getId(), "订单签收完成", fulfillStateMachine.getState().getId().name());
		order = orderService.getById(order.getId());
		log.info("----------【DB6】订单号（{}），订单时间（{}），订单状态（{}）", order.getId(), order.getDateTime(), order.getState());
	}
}
