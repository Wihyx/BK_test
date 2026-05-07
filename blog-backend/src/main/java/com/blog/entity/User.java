package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Admin user entity mapped to the {@code user} table.
 * The table name is escaped with backticks because "user" is a MySQL
 * reserved word. Uses {@code @TableLogic} for soft deletes.
 */
@Data
@TableName("`user`")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
    /** Role identifier, e.g. "admin" or "editor". */
    private String role;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
