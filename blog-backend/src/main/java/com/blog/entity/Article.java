package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Article entity mapped to the {@code article} table.
 *
 * <p>Key MyBatis-Plus annotations:</p>
 * <ul>
 *   <li>{@code @TableName} — explicitly binds the entity to the table named "article".</li>
 *   <li>{@code @TableId(type = IdType.AUTO)} — auto-increment primary key.</li>
 *   <li>{@code @TableField(exist = false)} — marks commentCount as a transient field
 *       that is NOT persisted to the database (computed at query time).</li>
 *   <li>{@code @TableLogic} — marks {@code deleted} as a logical-delete flag;
 *       MyBatis-Plus automatically appends {@code deleted=0} to all queries.</li>
 *   <li>{@code FieldFill.INSERT / UPDATE} — auto-fills the timestamp fields on
 *       insert or update via a MetaObjectHandler.</li>
 * </ul>
 */
@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private Long categoryId;
    /** 1=published, 0=draft or hidden. */
    private Integer status;
    private Long viewCount;
    private Long likeCount;
    /** 1=pinned to top, 0=normal order. */
    private Integer isTop;
    private Integer isCommentEnabled;
    private Integer cardType;

    /** Computed field — not stored in DB, populated on read. */
    @TableField(exist = false)
    private Long commentCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
