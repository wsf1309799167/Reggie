package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.common.R;
import org.example.entity.ShoppingCart;
import org.example.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(@RequestBody ShoppingCart shoppingCart) {
//        先设置id
        long currentId = 1;
        shoppingCart.setUserId(currentId);
//        查询是否存在相同菜品或套餐的购物车
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        if(shoppingCart.getDishId() != null){
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        if(cart == null){
            if(shoppingCart.getNumber() ==null) {
                shoppingCart.setNumber(1);
            }
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        } else {
            if(shoppingCart.getNumber() ==null) {
                cart.setNumber(cart.getNumber()+ 1);
            } else {
                cart.setNumber(cart.getNumber() + shoppingCart.getNumber());
            }
            shoppingCartService.saveOrUpdate(cart);
        }

        return R.success(cart);
    }
   }
