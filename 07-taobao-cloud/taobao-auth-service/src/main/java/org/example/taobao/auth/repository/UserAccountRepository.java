package org.example.taobao.auth.repository;

import org.example.taobao.auth.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户账号数据访问层。
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    /**
     * 按用户名查询启用中的账号。
     */
    Optional<UserAccount> findByUsernameAndEnabledTrue(String username);
}
