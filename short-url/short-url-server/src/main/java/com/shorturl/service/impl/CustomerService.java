package com.shorturl.service.impl;

import com.shorturl.mapper.CustomerMapper;
import com.shorturl.vo.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerService {
    @Autowired
    private CustomerMapper mapper;

    public void findCustomer(){
        Customer customer = mapper.findCustomer(1);
      log.info("customer:===={}",customer);
    }
}
