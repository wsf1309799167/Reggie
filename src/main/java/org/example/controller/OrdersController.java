package org.example.controller;


import lombok.extern.slf4j.Slf4j;
import org.example.common.R;
import org.example.entity.Orders;
import org.example.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @PostMapping("/submit")
   public R<String> submitOrder(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return R.success("success");
    }
}
