package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.common.R;
import org.example.entity.AddressBook;
import org.example.entity.OrderDetail;
import org.example.entity.Orders;
import org.example.entity.ShoppingCart;
import org.example.mapper.OrdersMapper;
import org.example.service.AddressBookService;
import org.example.service.OrderDetailService;
import org.example.service.OrdersService;
import org.example.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private AddressBookService addressBookService;
    @Override
    public void submit(Orders orders) {
        //获得用户id
        Long userId = 1l;
        //根据用户id获得购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<ShoppingCart>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        if(shoppingCartList.isEmpty() || shoppingCartList.size() == 0){
           throw new RuntimeException("购物车为空");
        }
        AtomicInteger amount = new AtomicInteger(0);
        for (ShoppingCart shoppingCart : shoppingCartList) {
            amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber ())).intValue());
       }
        //根据地址id获得地址信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        //将地址信息设置到订单中
        orders.setAddress(addressBook.getDetail());
        //存入订单表
        orders.setNumber(String.valueOf(IdWorker.getId()));
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setStatus(1);
        this.saveOrUpdate(orders);
        //存入订单详情表
        for (ShoppingCart shoppingCart : shoppingCartList) {
            //将购物车信息转换为订单详情
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orders.getId());
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setAmount(shoppingCart.getAmount());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetailService.saveOrUpdate(orderDetail);
        }
        //清空购物车
        shoppingCartService.remove(queryWrapper);
    }
}
