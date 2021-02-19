package com.example.order.entity;

import com.example.order.adapter.SimpleEnumStateMachineConfiguration;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    private Date dateTime;
    private String state;

    public Order() {
    }

    public Order(Date date, SimpleEnumStateMachineConfiguration.OrderStates os) {
        this.dateTime = date;
        this.state = os.name();
    }

    public SimpleEnumStateMachineConfiguration.OrderStates getOrderState() {
        return SimpleEnumStateMachineConfiguration.OrderStates.valueOf(this.state);
    }

    public void setOrderState(SimpleEnumStateMachineConfiguration.OrderStates state) {
        this.state = state.name();
    }
}
