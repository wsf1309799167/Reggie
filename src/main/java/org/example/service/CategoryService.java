package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.Category;

public interface CategoryService extends IService<Category> {
    /**
     * 删除分类
     * @param id 分类id
     */
    void remove(Long id);
}