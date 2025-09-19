package com.xudri.cloudrenderserver.application.service.impl;

import com.xudri.cloudrenderserver.application.service.AuthService;
import com.xudri.cloudrenderserver.common.dto.AuthResponse;
import com.xudri.cloudrenderserver.common.dto.LoginRequest;
import com.xudri.cloudrenderserver.common.dto.RefreshTokenRequest;
import com.xudri.cloudrenderserver.common.dto.RegisterRequest;
import com.xudri.cloudrenderserver.common.exception.Result;
import com.xudri.cloudrenderserver.common.util.LoggerUtil;
import com.xudri.cloudrenderserver.domain.entity.Role;
import com.xudri.cloudrenderserver.domain.entity.User;
import com.xudri.cloudrenderserver.domain.entity.UserRole;
import com.xudri.cloudrenderserver.infrastructure.mapper.RoleMapper;
import com.xudri.cloudrenderserver.infrastructure.mapper.UserMapper;
import com.xudri.cloudrenderserver.infrastructure.mapper.UserRoleMapper;
import com.xudri.cloudrenderserver.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 *
 * @author maxyun
 * @since 2024-09-18 16:00:00
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Result<AuthResponse> login(LoginRequest loginRequest) {
        try {
            LoggerUtil.logBusiness("用户登录", "尝试登录 - 用户：" + loginRequest.getUsername());

            // 进行身份认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 获取用户信息
            User user = userMapper.findByUsername(loginRequest.getUsername());
            if (user == null) {
                LoggerUtil.logBusiness("用户登录", "用户不存在 - " + loginRequest.getUsername());
                return Result.error("用户不存在");
            }

            // 获取用户角色
            List<Role> roles = roleMapper.findRolesByUserId(user.getId());
            String roleNames = roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(","));

            // 生成令牌
            String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getId(), roleNames);
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getId());

            // 构建响应
            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(86400000L) // 24小时
                    .userInfo(AuthResponse.UserInfoDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .roles(roles.stream().map(Role::getName).collect(Collectors.toList()))
                            .status(user.getStatus())
                            .build())
                    .build();

            LoggerUtil.logBusiness("用户登录", "登录成功 - 用户：" + loginRequest.getUsername());
            return Result.success(authResponse);

        } catch (BadCredentialsException e) {
            LoggerUtil.logBusiness("用户登录", "登录失败 - 用户名或密码错误：" + loginRequest.getUsername());
            return Result.error("用户名或密码错误");
        } catch (AuthenticationException e) {
            LoggerUtil.logError(log, "用户登录", "认证失败 - " + loginRequest.getUsername(), e);
            return Result.error("认证失败：" + e.getMessage());
        } catch (Exception e) {
            LoggerUtil.logError(log, "用户登录", "登录过程中发生异常 - " + loginRequest.getUsername(), e);
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<AuthResponse> register(RegisterRequest registerRequest) {
        try {
            LoggerUtil.logBusiness("用户注册", "尝试注册 - 用户：" + registerRequest.getUsername());

            // 检查用户名是否已存在
            User existingUser = userMapper.findByUsername(registerRequest.getUsername());
            if (existingUser != null) {
                LoggerUtil.logBusiness("用户注册", "用户名已存在 - " + registerRequest.getUsername());
                return Result.error("用户名已存在");
            }

            // 检查邮箱是否已存在
            if (StringUtils.hasText(registerRequest.getEmail())) {
                User existingEmailUser = userMapper.findByEmail(registerRequest.getEmail());
                if (existingEmailUser != null) {
                    LoggerUtil.logBusiness("用户注册", "邮箱已存在 - " + registerRequest.getEmail());
                    return Result.error("邮箱已存在");
                }
            }

            // 创建新用户
            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            newUser.setEmail(registerRequest.getEmail());
            newUser.setStatus(1); // 默认激活状态
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());

            // 保存用户
            userMapper.insert(newUser);

            // 为新用户分配默认角色（USER）
            Role userRole = roleMapper.findByName("USER");
            if (userRole != null) {
                UserRole userRoleRelation = new UserRole();
                userRoleRelation.setUserId(newUser.getId());
                userRoleRelation.setRoleId(userRole.getId());
                userRoleMapper.insert(userRoleRelation);
            }

            // 获取用户角色
            List<Role> roles = roleMapper.findRolesByUserId(newUser.getId());
            String roleNames = roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(","));

            // 生成令牌
            String accessToken = jwtUtil.generateAccessToken(newUser.getUsername(), newUser.getId(), roleNames);
            String refreshToken = jwtUtil.generateRefreshToken(newUser.getUsername(), newUser.getId());

            // 构建响应
            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(86400000L) // 24小时
                    .userInfo(AuthResponse.UserInfoDto.builder()
                            .id(newUser.getId())
                            .username(newUser.getUsername())
                            .email(newUser.getEmail())
                            .roles(roles.stream().map(Role::getName).collect(Collectors.toList()))
                            .status(newUser.getStatus())
                            .build())
                    .build();

            LoggerUtil.logBusiness("用户注册", "注册成功 - 用户：" + registerRequest.getUsername());
            return Result.success(authResponse);

        } catch (Exception e) {
            LoggerUtil.logError(log, "用户注册", "注册过程中发生异常 - " + registerRequest.getUsername(), e);
            return Result.error("注册失败：" + e.getMessage());
        }
    }

    @Override
    public Result<AuthResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            String refreshToken = refreshTokenRequest.getRefreshToken();
            
            // 验证刷新令牌
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                LoggerUtil.logBusiness("令牌刷新", "无效的刷新令牌");
                return Result.error("无效的刷新令牌");
            }

            String username = jwtUtil.getUsernameFromToken(refreshToken);
            if (!StringUtils.hasText(username)) {
                LoggerUtil.logBusiness("令牌刷新", "无法从刷新令牌中获取用户名");
                return Result.error("无效的刷新令牌");
            }

            if (jwtUtil.isTokenExpired(refreshToken)) {
                LoggerUtil.logBusiness("令牌刷新", "刷新令牌已过期 - 用户：" + username);
                return Result.error("刷新令牌已过期");
            }

            // 获取用户信息
            User user = userMapper.findByUsername(username);
            if (user == null || user.getStatus() != 1) {
                LoggerUtil.logBusiness("令牌刷新", "用户不存在或已禁用 - " + username);
                return Result.error("用户不存在或已禁用");
            }

            // 获取用户角色
            List<Role> roles = roleMapper.findRolesByUserId(user.getId());
            String roleNames = roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(","));

            // 生成新的访问令牌
            String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getId(), roleNames);

            // 构建响应
            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken) // 保持原有刷新令牌
                    .tokenType("Bearer")
                    .expiresIn(86400000L) // 24小时
                    .userInfo(AuthResponse.UserInfoDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .roles(roles.stream().map(Role::getName).collect(Collectors.toList()))
                            .status(user.getStatus())
                            .build())
                    .build();

            LoggerUtil.logBusiness("令牌刷新", "令牌刷新成功 - 用户：" + username);
            return Result.success(authResponse);

        } catch (Exception e) {
            LoggerUtil.logError(log, "令牌刷新", "刷新令牌过程中发生异常", e);
            return Result.error("刷新令牌失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> logout(String username) {
        try {
            LoggerUtil.logBusiness("用户登出", "用户登出 - " + username);
            // 在实际应用中，可以在这里将令牌加入黑名单
            // 这里简单记录日志即可
            return Result.success("登出成功");
        } catch (Exception e) {
            LoggerUtil.logError(log, "用户登出", "登出过程中发生异常 - " + username, e);
            return Result.error("登出失败：" + e.getMessage());
        }
    }

    @Override
    public Result<AuthResponse.UserInfoDto> getUserProfile(String username) {
        try {
            User user = userMapper.findByUsername(username);
            if (user == null) {
                LoggerUtil.logBusiness("获取用户信息", "用户不存在 - " + username);
                return Result.error("用户不存在");
            }

            List<Role> roles = roleMapper.findRolesByUserId(user.getId());
            AuthResponse.UserInfoDto userInfo = AuthResponse.UserInfoDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .roles(roles.stream().map(Role::getName).collect(Collectors.toList()))
                    .status(user.getStatus())
                    .build();

            return Result.success(userInfo);

        } catch (Exception e) {
            LoggerUtil.logError(log, "获取用户信息", "获取用户信息失败 - " + username, e);
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }
}