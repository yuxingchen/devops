package com.devops.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.devops.util.UserContextUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自动填充字段
 *
 * @author yux
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 使用当前JVM的时区创建Date对象
        this.strictInsertFill(metaObject, "createdTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updatedTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "createdBy", Long.class, UserContextUtil.getCurrentUserId());
        this.strictInsertFill(metaObject, "updatedBy", Long.class, UserContextUtil.getCurrentUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新操作的填充逻辑
        this.strictUpdateFill(metaObject, "updatedTime", Date.class, new Date());
        this.strictUpdateFill(metaObject, "updatedBy", Long.class, UserContextUtil.getCurrentUserId());
    }
}