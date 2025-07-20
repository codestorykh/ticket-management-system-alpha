package com.codestorykh.user.service.impl;

import com.codestorykh.common.constant.ApiConstant;
import com.codestorykh.common.dto.EmptyObject;
import com.codestorykh.common.exception.CustomMessageException;
import com.codestorykh.user.entity.CustomUserDetail;
import com.codestorykh.user.entity.User;
import com.codestorykh.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.customUserDetail(username);
    }

    public CustomUserDetail customUserDetail(String username) {
        Optional<User> user = userRepository.findFirstByUsernameAndStatus(username, ApiConstant.ACTIVE.getKey());
        if(user.isEmpty()){
            log.warn("Username {} unauthorized", username);
            throw new CustomMessageException(
                    "Unauthorized",
                    String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                    new EmptyObject(),
                    HttpStatus.UNAUTHORIZED);
        }
        user.ifPresent(
                u -> {
                    if(!u.getStatus().equals(ApiConstant.ACTIVE.getKey())){
                        log.warn("Username {} blocked", username);
                        throw new CustomMessageException(
                                "Blocked",
                                String.valueOf(HttpStatus.FORBIDDEN.value()),
                                new EmptyObject(),
                                HttpStatus.UNAUTHORIZED);
                    }
                    if (u.getLoginAttempt() > u.getMaxAttempt()) {
                        log.warn("Username {} attempt more than 3", username);
                        throw new CustomMessageException(
                                "Unauthorized",
                                String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                                new EmptyObject(),
                                HttpStatus.UNAUTHORIZED);
                    }
                }
        );

        return new CustomUserDetail(
                user.get().getUsername(),
                user.get().getPassword(),
                user.get().getRoles()
                        .stream().map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList()));
    }

    public void saveUserAttemptAuthentication(String username) {
         userRepository.findFirstByUsernameAndStatus(username, ApiConstant.ACTIVE.getKey()).ifPresent(
                user -> {
                    int attempt = user.getLoginAttempt() + 1;
                    user.setLoginAttempt(attempt);
                    user.setUpdatedAt(LocalDateTime.now());
                    if(user.getLoginAttempt() > user.getMaxAttempt()){
                        log.warn("User {} update status to blocked", username);
                        user.setStatus(ApiConstant.BLK.getKey());
                    }
                    userRepository.save(user);
                }
        );
    }

    public void updateAttempt(String username) {
        userRepository.findFirstByUsernameAndStatus(username, ApiConstant.ACTIVE.getKey()).ifPresent(
                user -> {
                    user.setLoginAttempt(0);
                    user.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(user);
                }
        );
    }

}