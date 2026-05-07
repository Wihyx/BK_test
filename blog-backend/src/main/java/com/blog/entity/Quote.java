package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Quote/saying entity mapped to the {@code quote} table.
 * The table name is escaped with backticks because "quote" is a MySQL
 * reserved word. Uses {@code @TableLogic} for soft deletes.
 */
@Data
@TableName("`quote`")
public class Quote {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableLogic
    private Integer deleted;
}
