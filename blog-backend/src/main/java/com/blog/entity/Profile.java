package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * Blog author profile entity mapped to the {@code profile} table.
 * Stores personal info shown on the blog homepage / about page.
 */
@Data
@TableName("profile")
public class Profile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    /** Avatar image URL. */
    private String avatar;
    /** Short bio / introduction. */
    private String bio;
    /** Comma-separated tech stack keywords. */
    private String techStack;
    private String email;
    /** GitHub profile URL. */
    private String github;
}
