package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.common.R;
import org.example.entity.Category;
import org.example.entity.Dish;
import org.example.entity.Setmeal;
import org.example.service.CategoryService;
import org.example.service.DishService;
import org.example.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @GetMapping("/list")
    public R<Page> list(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                  @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);//增加排序
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @PostMapping("/save")
    public R<String> save(@RequestBody Category category) {
        categoryService.saveOrUpdate(category);
        return R.success("新增成功");
    }

    @PostMapping("/delete/{id}")
    public R<String> delete(@PathVariable("id") Long id) {
        categoryService.remove(id);
        return R.success("删除成功");
    }

    @PostMapping("/update")
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("更新成功");
    }
}
