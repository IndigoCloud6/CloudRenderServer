package com.xudri.cloudrenderserver.interfaces.rest;

import com.xudri.cloudrenderserver.application.service.AuthService;
import com.xudri.cloudrenderserver.common.dto.*;
import com.xudri.cloudrenderserver.common.exception.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供用户认证相关的API接口
 *
 * @author maxyun
 * @since 2024-09-18 16:00:00
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 认证响应
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     * @return 认证响应
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户账户")
    public Result<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    /**
     * 刷新令牌
     *
     * @param refreshTokenRequest 刷新令牌请求
     * @return 认证响应
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public Result<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/profile")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    public Result<AuthResponse.UserInfoDto> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return authService.getUserProfile(username);
    }

    /**
     * 用户登出
     *
     * @return 操作结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户退出登录")
    public Result<String> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return authService.logout(username);
    }
}