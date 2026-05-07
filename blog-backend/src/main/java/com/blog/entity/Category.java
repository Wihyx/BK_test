package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Article category entity mapped to the {@code category} table.
 * Uses {@code @TableLogic} for soft deletes and
 * {@code @TableField(fill = FieldFill.INSERT)} for auto-timestamping.
 */
@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    /** Display sort order (lower = first). */
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableLogic
    private Integer deleted;
}
