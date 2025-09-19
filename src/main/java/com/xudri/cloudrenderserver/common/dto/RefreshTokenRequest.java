package com.xudri.cloudrenderserver.common.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 刷新令牌请求DTO
 *
 * @author maxyun
 * @since 2024-09-18 16:00:00
 */
@Data
public class RefreshTokenRequest {

    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}