package com.blog.common;

import lombok.Data;

/**
 * Unified API response wrapper. Encapsulates HTTP status code, message, and
 * payload into a single object returned by all controller endpoints.
 *
 * @param <T> the type of the response payload
 */
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    private Result() {}

    /** Create a success response (code 200) with the given payload. */
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.msg = "success";
        r.data = data;
        return r;
    }

    /** Create a success response without payload. */
    public static <T> Result<T> success() {
        return success(null);
    }

    /** Create an error response with a custom code and message. */
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.code = code;
        r.msg = msg;
        return r;
    }

    /** Shortcut for 401 Unauthorized responses. */
    public static <T> Result<T> unauthorized(String msg) {
        return error(401, msg);
    }

    /** Shortcut for 403 Forbidden responses. */
    public static <T> Result<T> forbidden(String msg) {
        return error(403, msg);
    }
}
