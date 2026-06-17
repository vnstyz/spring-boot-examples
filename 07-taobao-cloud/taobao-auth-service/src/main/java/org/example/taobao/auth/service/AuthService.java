package org.example.taobao.auth.service;

import org.example.taobao.auth.entity.UserAccount;
import org.example.taobao.auth.repository.UserAccountRepository;
import org.example.taobao.common.dto.auth.LoginRequest;
import org.example.taobao.common.dto.auth.LoginResponse;
import org.example.taobao.common.dto.auth.TokenValidationResponse;
import org.example.taobao.common.exception.BusinessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 认证核心服务，负责登录和令牌校验。
 */
@Service
public class AuthService {

    private static final String TOKEN_KEY_PREFIX = "auth:token:";

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final StringRedisTemplate stringRedisTemplate;

    public AuthService(UserAccountRepository userAccountRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenService jwtTokenService,
                       StringRedisTemplate stringRedisTemplate) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 执行账号登录：校验密码、签发令牌、写入Redis会话。
     */
    public LoginResponse login(LoginRequest loginRequest) {
        UserAccount userAccount = userAccountRepository.findByUsernameAndEnabledTrue(loginRequest.username())
                .orElseThrow(() -> new BusinessException(40100, "用户名或密码错误"));

        if (!passwordEncoder.matches(loginRequest.password(), userAccount.getPasswordHash())) {
            throw new BusinessException(40100, "用户名或密码错误");
        }

        JwtTokenService.GeneratedToken generatedToken = jwtTokenService.generate(userAccount);
        String redisKey = TOKEN_KEY_PREFIX + generatedToken.jti();
        Duration ttl = Duration.between(java.time.LocalDateTime.now(), generatedToken.expiresAt());
        stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(userAccount.getId()), ttl);

        return new LoginResponse(
                userAccount.getId(),
                userAccount.getNickname(),
                generatedToken.token(),
                generatedToken.expiresAt()
        );
    }

    /**
     * 校验令牌并返回用户身份，兼容各微服务的登录态验证。
     */
    public TokenValidationResponse validateToken(String token) {
        JwtTokenService.ParsedToken parsedToken = jwtTokenService.parse(token);
        String redisKey = TOKEN_KEY_PREFIX + parsedToken.jti();
        String cacheUserId = stringRedisTemplate.opsForValue().get(redisKey);
        if (cacheUserId == null) {
            throw new BusinessException(40102, "登录已失效，请重新登录");
        }

        return new TokenValidationResponse(
                parsedToken.userId(),
                parsedToken.username(),
                parsedToken.nickname(),
                parsedToken.expiresAt()
        );
    }
}
