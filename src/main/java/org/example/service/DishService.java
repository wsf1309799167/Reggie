package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.DishDto;
import org.example.entity.Dish;

public interface DishService extends IService<Dish> {

    public void dishWithFlavor(DishDto dishDto);
}
