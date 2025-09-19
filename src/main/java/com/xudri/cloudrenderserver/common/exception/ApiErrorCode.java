package com.xudri.cloudrenderserver.common.exception;

/**
 * @ClassName ApiErrorCode
 * @Description TODO
 * @Author MaxYun
 * @Date 2023/3/31 9:53
 * @Version 1.0
 */
public enum ApiErrorCode implements IErrorCode {
    FAILED(-1L, "操作失败"),
    SUCCESS(0L, "执行成功"),
    
    // 认证相关错误码
    UNAUTHORIZED(401L, "未授权访问"),
    FORBIDDEN(403L, "禁止访问"),
    INVALID_TOKEN(4001L, "无效的令牌"),
    TOKEN_EXPIRED(4002L, "令牌已过期"),
    INVALID_CREDENTIALS(4003L, "用户名或密码错误"),
    USER_NOT_FOUND(4004L, "用户不存在"),
    USER_DISABLED(4005L, "用户已禁用"),
    USERNAME_EXISTS(4006L, "用户名已存在"),
    EMAIL_EXISTS(4007L, "邮箱已存在"),
    INVALID_REFRESH_TOKEN(4008L, "无效的刷新令牌");

    private final long code;
    private final String msg;

    ApiErrorCode(final long code, final String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ApiErrorCode fromCode(long code) {
        ApiErrorCode[] ecs = values();
        ApiErrorCode[] var3 = ecs;
        int var4 = ecs.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            ApiErrorCode ec = var3[var5];
            if (ec.getCode() == code) {
                return ec;
            }
        }

        return SUCCESS;
    }

    public long getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public String toString() {
        return String.format(" ErrorCode:{code=%s, msg=%s} ", this.code, this.msg);
    }
}
