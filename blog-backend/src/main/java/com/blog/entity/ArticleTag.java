package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * Join-table entity mapping articles to tags. Mapped to the {@code article_tag}
 * table. Each row links one article to one tag via their foreign-key IDs.
 */
@Data
@TableName("article_tag")
public class ArticleTag {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private Long tagId;
}
