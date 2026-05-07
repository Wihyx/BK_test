package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * Cached RSS feed article entity mapped to the {@code feed_article} table.
 * Each row stores metadata for a single article fetched from a feed source.
 */
@Data
@TableName("feed_article")
public class FeedArticle {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long feedId;
    private String title;
    private String link;
    private String summary;
    private String image;
    private String pubDate;
    /** Denormalized feed name for display purposes. */
    private String feedName;
}
