package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dto.SetmealDto;
import org.example.entity.Setmeal;
import org.example.entity.SetmealDish;
import org.example.mapper.SetmealMapper;
import org.example.service.SetmealDishService;
import org.example.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishs();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
        });
        setmealDishService.saveBatch(setmealDishes);
    }
}


