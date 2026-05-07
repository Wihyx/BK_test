package com.blog.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充处理器
 * 在新增和更新数据时，自动填入创建时间和更新时间，不用手动设置
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    // 插入数据时自动填充 createdTime 字段为当前时间
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, LocalDateTime.now());
    }

    // 更新数据时自动填充 updatedTime 字段为当前时间
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
    }
}
