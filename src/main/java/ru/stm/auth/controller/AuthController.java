package ru.stm.auth.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.stm.auth.repository.RedisRepository;
import ru.stm.auth.repository.impl.RedisSuffixes;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final RedisRepository redisRepository;

    @GetMapping("/token")
    public String getToken() {
        var token = UUID.randomUUID().toString();
        redisRepository.setString(token+ RedisSuffixes.token.getSuffix(), token, 7200);
        return token;
    }
}
