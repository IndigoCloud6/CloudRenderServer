package com.xudri.cloudrenderserver.application.service;

import com.xudri.cloudrenderserver.common.dto.*;
import com.xudri.cloudrenderserver.common.exception.Result;

/**
 * 认证服务接口
 *
 * @author maxyun
 * @since 2024-09-18 16:00:00
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 认证响应
     */
    Result<AuthResponse> login(LoginRequest loginRequest);

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     * @return 认证响应
     */
    Result<AuthResponse> register(RegisterRequest registerRequest);

    /**
     * 刷新令牌
     *
     * @param refreshTokenRequest 刷新令牌请求
     * @return 认证响应
     */
    Result<AuthResponse> refreshToken(RefreshTokenRequest refreshTokenRequest);

    /**
     * 用户登出
     *
     * @param username 用户名
     * @return 操作结果
     */
    Result<String> logout(String username);

    /**
     * 获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    Result<AuthResponse.UserInfoDto> getUserProfile(String username);
}