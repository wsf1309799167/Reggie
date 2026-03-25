package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dto.DishDto;
import org.example.entity.Dish;
import org.example.entity.DishFlavor;
import org.example.mapper.DishMapper;
import org.example.service.DishFlavorService;
import org.example.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Transactional
    @Override
    public void dishWithFlavor(DishDto dishDto) {
        // 保存菜品
        this.save(dishDto);
        //获取菜品id
        Long dishId = dishDto.getId();
        // 保存菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().forEach(flavor -> {
            flavor.setDishId(dishId);
        });
        dishFlavorService.saveBatch(flavors);
    }
}