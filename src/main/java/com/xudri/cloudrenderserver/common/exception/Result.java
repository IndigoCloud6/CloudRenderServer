package com.xudri.cloudrenderserver.common.exception;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * 统一API响应结果封装
 *
 * @param <T> 数据泛型类型
 * @author MaxYun
 * @date 2023/3/31 9:49
 */
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private long code;
    private T data;
    private String msg;

    public Result() {
    }

    public Result(IErrorCode errorCode) {
        IErrorCode code = Optional.ofNullable(errorCode).orElse(ApiErrorCode.FAILED);
        this.code = code.getCode();
        this.msg = code.getMsg();
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> ok(T data) {
        ApiErrorCode resultCode = (data instanceof Boolean && Boolean.FALSE.equals(data))
                ? ApiErrorCode.FAILED
                : ApiErrorCode.SUCCESS;

        return restResult(data, resultCode);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> failed(String msg) {
        return restResult(null, ApiErrorCode.FAILED.getCode(), msg);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> failed(IErrorCode errorCode) {
        return restResult(null, errorCode);
    }

    /**
     * 创建响应结果
     */
    public static <T> Result<T> restResult(T data, IErrorCode errorCode) {
        return restResult(data, errorCode.getCode(), errorCode.getMsg());
    }

    private static <T> Result<T> restResult(T data, long code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setData(data);
        result.setMsg(msg);
        return result;
    }

    /**
     * 判断请求是否成功
     */
    public boolean ok() {
        return ApiErrorCode.SUCCESS.getCode() == this.code;
    }

    /**
     * 获取服务数据，如果失败则抛出异常
     */
    public T serviceData() {
        if (!this.ok()) {
            throw new ApiException(this.msg);
        }
        return this.data;
    }

    // Getter和Setter方法
    public long getCode() {
        return this.code;
    }

    public Result<T> setCode(long code) {
        this.code = code;
        return this;
    }

    public T getData() {
        return this.data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public String getMsg() {
        return this.msg;
    }

    public Result<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    // equals和hashCode方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Result)) return false;

        Result<?> other = (Result<?>) o;
        return code == other.code &&
                Objects.equals(data, other.data) &&
                Objects.equals(msg, other.msg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, data, msg);
    }

    @Override
    public String toString() {
        return "Result(code=" + this.code +
                ", data=" + this.data +
                ", msg=" + this.msg + ")";
    }
}