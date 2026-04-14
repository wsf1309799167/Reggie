package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.common.R;
import org.example.entity.ShoppingCart;
import org.example.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    /**
     * 添加购物车
     */
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
    /**
     * 查询购物车列表
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> shoppingCartList(){
        long currentId = 1;
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
//        查询购物车列表
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }
    /**
     * 清空购物车
     */
    @PostMapping("/clean")
    public R<String> cleanShoppingCart(){
        long currentId = 1;
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, 1L);
        shoppingCartService.remove(queryWrapper);
        return R.success("删除成功");
    }
    /**
     * 删除购物车项
     */
    @PostMapping("/delete")
    public R<String> deleteShoppingCart(@RequestParam Long id) {
        // 查询 userid 为 1 且 id 等于传入 id 的购物车项
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, 1L);
        queryWrapper.eq(ShoppingCart::getId, id);
        ShoppingCart cartShop = shoppingCartService.getOne(queryWrapper);
        
        // 检查购物车项是否存在
        if (cartShop == null) {
            return R.error("购物车项不存在");
        }
        
        // 处理数量减一或删除
        if (cartShop.getNumber() > 1) {
            cartShop.setNumber(cartShop.getNumber() - 1);
            shoppingCartService.updateById(cartShop);
        } else {
            shoppingCartService.removeById(id);
        }
        
        return R.success("删除成功");
    }
    }