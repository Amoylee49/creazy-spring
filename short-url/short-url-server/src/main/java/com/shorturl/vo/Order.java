package com.shorturl.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "order")
public class Order {

    private int id;
    private String name;

    private Customer customer;
}
