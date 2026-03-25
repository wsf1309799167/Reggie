package org.example.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作时自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始执行插入操作的自动填充...");
        // 填充创建时间和更新时间
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        // 填充创建用户和更新用户
        // TODO: 从Session中获取当前登录用户的ID
        // 这里暂时使用固定值，后续需要从Session中获取
        metaObject.setValue("createUser", 1L);
        metaObject.setValue("updateUser", 1L);
    }

    /**
     * 更新操作时自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始执行更新操作的自动填充...");
        // 填充更新时间
        metaObject.setValue("updateTime", LocalDateTime.now());
        // 填充更新用户
        // TODO: 从Session中获取当前登录用户的ID
        // 这里暂时使用固定值，后续需要从Session中获取
        metaObject.setValue("updateUser", 1L);
    }
}