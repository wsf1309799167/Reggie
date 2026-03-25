package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.SetmealDto;
import org.example.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);
}