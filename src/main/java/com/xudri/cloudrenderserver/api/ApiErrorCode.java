package com.xudri.cloudrenderserver.api;

/**
 * @ClassName ApiErrorCode
 * @Description TODO
 * @Author MaxYun
 * @Date 2023/3/31 9:53
 * @Version 1.0
 */
public enum ApiErrorCode implements IErrorCode {
    FAILED(-1L, "操作失败"),
    SUCCESS(0L, "执行成功");

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
