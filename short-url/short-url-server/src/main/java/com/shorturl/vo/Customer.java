package com.shorturl.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Customer {
    private int id;
    private String name;
    private List<Order> orderList = new ArrayList<>();
}
