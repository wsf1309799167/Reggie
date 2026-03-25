package org.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.common.R;
import org.example.dto.DishDto;
import org.example.entity.Category;
import org.example.entity.Dish;
import org.example.entity.DishFlavor;
import org.example.mapper.DishFlavorMapper;
import org.example.service.CategoryService;
import org.example.service.DishFlavorService;
import org.example.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @PostMapping("/add")
    public R<String> addDish(@RequestBody DishDto dishDto){
        dishService.dishWithFlavor(dishDto);
        return R.success("新增成功");
    }

    @PostMapping("/delete/{dishId}")
    public R<String> deleteDish(@PathVariable Long dishId){
        if (dishId == null){
            return R.error("菜品id不能为空");
        }
        dishService.removeById(dishId);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(queryWrapper);
        return R.success("删除成功");
    }
    @PostMapping("/deleteList")
    public R<String> deleteDishList(@RequestBody DishDto dishDto){
        List<Long> dishIds = dishDto.getDishIds();
        dishService.removeByIds(dishIds);
        return R.success("批量菜品删除成功");
    }
     @PostMapping("/updateStatus")
     public R<String> updateDishStatus(@RequestBody DishDto dishDto){
        List<Long> dishIds = dishDto.getDishIds();
        Integer status = dishDto.getStatus();
        if (dishIds == null || dishIds.isEmpty()){
            return R.error("菜品id不能为空");
        }
        if (status == null){
            return R.error("状态不能为空");
        }
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, dishIds);
        Dish dish = new Dish();
        dish.setStatus(status);
        dishService.update(dish,queryWrapper);
         return  R.success("菜品状态更新成功");
     }
    @GetMapping("/page")
    public R<Page> page(@RequestParam(defaultValue = "1") int pageNum,@RequestParam(defaultValue = "10") int pageSize,String name){
        Page<Dish> pageInfo = new Page<>(pageNum,pageSize);
        Page<DishDto> dishDtoPageInfo = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,dishDtoPageInfo);
        List<Dish> dishList = pageInfo.getRecords();
        List<DishDto> dishDtoList = dishList.stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            Long categoryId = dish.getCategoryId();
            if(categoryId!=null){
                Category category = categoryService.getById(categoryId);
                if(category != null){
                    dishDto.setCategoryName(category.getName());
                }
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPageInfo.setRecords(dishDtoList);
        return R.success(dishDtoPageInfo);
    }
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        Dish dish = dishService.getById(id);
        if(dish == null){
            return R.error("菜品不存在");
        }
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        Long categoryId = dish.getCategoryId();
        if(categoryId!=null){
            Category category = categoryService.getById(categoryId);
            if(category != null){
                dishDto.setCategoryName(category.getName());
            }
        }
        List<DishFlavor> flavors = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));
        dishDto.setFlavors(flavors);
        return R.success(dishDto);
    }
    @GetMapping("/list")
    public R<List<Dish>> list(@RequestParam(value = "categoryId",required = false) Long categoryId){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,categoryId);
        List<Dish> dishList = dishService.list(queryWrapper);
        return R.success(dishList);
    }
    @PostMapping("/update")
    public R<String> updateDish(@RequestBody DishDto dishDto){
        dishService.saveOrUpdate(dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().forEach(flavor -> {
            flavor.setDishId(dishDto.getId());
        });
        dishFlavorService.saveOrUpdateBatch(flavors);
        return R.success("更新成功");
    }
}