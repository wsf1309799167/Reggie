package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.CustomException;
import org.example.common.R;
import org.example.entity.Category;
import org.example.entity.Dish;
import org.example.entity.Setmeal;
import org.example.mapper.CategoryMapper;
import org.example.service.CategoryService;
import org.example.service.DishService;
import org.example.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,id);
        setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        if(dishService.count(queryWrapper) > 0){
            throw new CustomException("该分类下有菜品，不能删除");
        }
        if(setmealService.count(setmealQueryWrapper) > 0){
            throw new CustomException("该分类下有套餐，不能删除");
        }
        super.removeById(id);
    }
}
