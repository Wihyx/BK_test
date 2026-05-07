package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Article comment entity mapped to the {@code comment} table.
 * Supports nested replies via {@code parentId} (null = top-level comment).
 */
@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    /** Parent comment ID for replies; null for top-level comments. */
    private Long parentId;
    private String nickname;
    private String email;
    private String content;
    /** 1=approved, 0=pending or hidden. */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableLogic
    private Integer deleted;
}
