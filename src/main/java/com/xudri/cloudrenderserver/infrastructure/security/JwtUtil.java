package com.xudri.cloudrenderserver.infrastructure.security;

import com.xudri.cloudrenderserver.common.util.LoggerUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 负责JWT令牌的生成、解析和验证
 *
 * @author maxyun
 * @since 2024-09-18 16:00:00
 */
@Component
@Log4j2
public class JwtUtil {

    @Value("${jwt.secret:cloudRenderServerJwtSecretKey2024ForAuthenticationAndAuthorization}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24小时，单位毫秒
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7天，单位毫秒
    private Long refreshExpiration;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成访问令牌
     *
     * @param username 用户名
     * @param userId 用户ID
     * @param roles 用户角色
     * @return JWT令牌
     */
    public String generateAccessToken(String username, Integer userId, String roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roles", roles);
        return generateToken(claims, username, expiration);
    }

    /**
     * 生成刷新令牌
     *
     * @param username 用户名
     * @param userId 用户ID
     * @return 刷新令牌
     */
    public String generateRefreshToken(String username, Integer userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        return generateToken(claims, username, refreshExpiration);
    }

    /**
     * 生成令牌
     *
     * @param claims 声明
     * @param subject 主题（用户名）
     * @param expiration 过期时间
     * @return JWT令牌
     */
    private String generateToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        try {
            String token = Jwts.builder()
                    .claims(claims)
                    .subject(subject)
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSigningKey())
                    .compact();

            LoggerUtil.logBusiness("JWT令牌", "生成令牌成功 - 用户：" + subject);
            return token;
        } catch (Exception e) {
            LoggerUtil.logError(log, "JWT令牌生成", "生成令牌失败 - 用户：" + subject, e);
            throw new RuntimeException("令牌生成失败", e);
        }
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            LoggerUtil.logError(log, "JWT令牌解析", "获取用户名失败", e);
            return null;
        }
    }

    /**
     * 从令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public Integer getUserIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("userId", Integer.class);
        } catch (Exception e) {
            LoggerUtil.logError(log, "JWT令牌解析", "获取用户ID失败", e);
            return null;
        }
    }

    /**
     * 从令牌中获取用户角色
     *
     * @param token JWT令牌
     * @return 用户角色
     */
    public String getRolesFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("roles", String.class);
        } catch (Exception e) {
            LoggerUtil.logError(log, "JWT令牌解析", "获取用户角色失败", e);
            return null;
        }
    }

    /**
     * 从令牌中获取过期时间
     *
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            LoggerUtil.logError(log, "JWT令牌解析", "获取过期时间失败", e);
            return null;
        }
    }

    /**
     * 验证令牌是否有效
     *
     * @param token JWT令牌
     * @param username 用户名
     * @return 是否有效
     */
    public boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            LoggerUtil.logError(log, "JWT令牌验证", "令牌验证失败 - 用户：" + username, e);
            return false;
        }
    }

    /**
     * 判断令牌是否过期
     *
     * @param token JWT令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            LoggerUtil.logError(log, "JWT令牌验证", "检查令牌过期状态失败", e);
            return true;
        }
    }

    /**
     * 判断是否为刷新令牌
     *
     * @param token JWT令牌
     * @return 是否为刷新令牌
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从令牌中获取声明
     *
     * @param token JWT令牌
     * @return 声明
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}