package com.ms.springms.repository;

import com.ms.springms.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<UserInfo,Long> {

    Optional<UserInfo> findByUsername(String username);
}
