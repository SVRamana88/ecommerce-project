package com.svr.ecommerce.controllers;

import com.svr.ecommerce.entities.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    @RequestMapping("/hello")
    public Message sayHello() {
        return new Message("Rest API Response");
    }
}
