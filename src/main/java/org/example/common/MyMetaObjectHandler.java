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

        // 检查并填充创建时间
        if (metaObject.hasSetter("createTime")) {
            metaObject.setValue("createTime", LocalDateTime.now());
        }

        // 检查并填充更新时间
        if (metaObject.hasSetter("updateTime")) {
            metaObject.setValue("updateTime", LocalDateTime.now());
        }

        // 检查并填充创建用户
        if (metaObject.hasSetter("createUser")) {
            metaObject.setValue("createUser", 1L);
        }

        // 检查并填充更新用户
        if (metaObject.hasSetter("updateUser")) {
            metaObject.setValue("updateUser", 1L);
        }
    }

    /**
     * 更新操作时自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始执行更新操作的自动填充...");

        // 检查并填充更新时间
        if (metaObject.hasSetter("updateTime")) {
            metaObject.setValue("updateTime", LocalDateTime.now());
        }

        // 检查并填充更新用户
        if (metaObject.hasSetter("updateUser")) {
            metaObject.setValue("updateUser", 1L);
        }
    }
}
