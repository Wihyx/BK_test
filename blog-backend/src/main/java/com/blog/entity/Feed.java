package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * RSS feed source entity mapped to the {@code feed} table.
 * Stores the display name and URL of each subscribed RSS source.
 */
@Data
@TableName("feed")
public class Feed {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String url;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
