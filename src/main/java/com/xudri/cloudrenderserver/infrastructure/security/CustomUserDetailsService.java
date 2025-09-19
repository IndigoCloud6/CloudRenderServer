package com.xudri.cloudrenderserver.infrastructure.security;

import com.xudri.cloudrenderserver.domain.entity.Role;
import com.xudri.cloudrenderserver.domain.entity.User;
import com.xudri.cloudrenderserver.infrastructure.mapper.RoleMapper;
import com.xudri.cloudrenderserver.infrastructure.mapper.UserMapper;
import com.xudri.cloudrenderserver.common.util.LoggerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义用户详情服务
 * 实现Spring Security的UserDetailsService接口
 * 负责根据用户名加载用户信息和权限
 *
 * @author maxyun
 * @since 2024-09-18 16:00:00
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // 查询用户信息
            User user = userMapper.findByUsername(username);
            if (user == null) {
                LoggerUtil.logBusiness("用户认证", "用户不存在 - " + username);
                throw new UsernameNotFoundException("用户不存在: " + username);
            }

            // 检查用户状态
            if (user.getStatus() == null || user.getStatus() != 1) {
                LoggerUtil.logBusiness("用户认证", "用户已禁用 - " + username);
                throw new UsernameNotFoundException("用户已禁用: " + username);
            }

            // 查询用户角色
            List<Role> roles = roleMapper.findRolesByUserId(user.getId());
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                    .collect(Collectors.toList());

            LoggerUtil.logBusiness("用户认证", "加载用户成功 - " + username + ", 角色: " + 
                    roles.stream().map(Role::getName).collect(Collectors.joining(",")));

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LoggerUtil.logError(log, "用户认证", "加载用户失败 - " + username, e);
            throw new UsernameNotFoundException("加载用户失败: " + username, e);
        }
    }
}