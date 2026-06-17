package org.example.taobao.auth.init;

import org.example.taobao.auth.entity.UserAccount;
import org.example.taobao.auth.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 初始化演示账号，便于本地联调登录流程。
 */
@Component
public class AuthDataInitializer implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthDataInitializer(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 启动时检查并创建默认账号：demo / 123456。
     */
    @Override
    public void run(String... args) {
        userAccountRepository.findByUsernameAndEnabledTrue("demo").ifPresentOrElse(
                account -> {
                    // 已存在演示账号时不重复创建。
                },
                () -> {
                    UserAccount userAccount = new UserAccount();
                    userAccount.setUsername("demo");
                    userAccount.setNickname("淘宝演示用户");
                    userAccount.setEnabled(true);
                    userAccount.setPasswordHash(passwordEncoder.encode("123456"));
                    userAccountRepository.save(userAccount);
                }
        );
    }
}
