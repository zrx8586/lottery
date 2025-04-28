package com.example.demo.lottery.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应类
 * @param <T> 返回数据的类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    private boolean success; // 操作是否成功
    private String message;  // 返回的消息
    private T data;          // 返回的数据
    private int code;        // 状态码或错误码

    // 静态方法用于快速创建响应
    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<>(true, message, data, 200);
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, "操作成功", data, 200);
    }

    public static <T> BaseResponse<T> failure(String message, int code) {
        return new BaseResponse<>(false, message, null, code);
    }

    public static <T> BaseResponse<T> failure(String message) {
        return new BaseResponse<>(false, message, null, 500);
    }
}