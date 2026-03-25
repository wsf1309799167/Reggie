package org.example.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.common.R;
import org.example.dto.SetmealDto;
import org.example.entity.Dish;
import org.example.entity.Setmeal;
import org.example.entity.SetmealDish;
import org.example.service.DishService;
import org.example.service.SetmealDishService;
import org.example.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;
    @PostMapping("/save")
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("新增成功");
    }

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,String name){
        Page<Setmeal> page = new Page<>(pageNum, pageSize);
        Page<SetmealDto> dishPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(name)){
            queryWrapper.like(Setmeal::getName,name);
        }
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(page, queryWrapper);
        BeanUtils.copyProperties(page,dishPage);
        List<SetmealDto> dishDtoList = dishPage.getRecords();
        List<Setmeal> setmealList = page.getRecords();
        dishDtoList = setmealList.stream().map(setmeal -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
            List<SetmealDish> setmealDishes = null;
            if (setmeal.getId() != null) {
                setmealDishQueryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
                setmealDishes = setmealDishService.list(setmealDishQueryWrapper);
            }
            setmealDto.setSetmealDishs(setmealDishes);
            return setmealDto;
        }).collect(Collectors.toList());
        dishPage.setRecords(dishDtoList);
        return R.success(dishPage);
    }


    @PostMapping("/delete")
    public R<String> delete(@RequestParam("ids") List<Long> ids){
        // 检查套餐状态，只有停用的套餐可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1); // 查找状态为1（启用）的套餐
        long count = setmealService.count(queryWrapper);

        if (count > 0) {
            return R.error("存在启用状态的套餐，无法删除");
        }

        // 执行删除操作
        setmealService.removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishQueryWrapper);
        return R.success("删除成功");
    }
}
