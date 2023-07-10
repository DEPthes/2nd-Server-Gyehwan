package com.websocket.domain.chat.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Objects;


/**
 * 로컬 환경일경우 내장 레디스가 실행됩니다.
 */
@Profile("local")
@Configuration
public class EmbeddedRedisConfig {

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

//    @PostConstruct
//    public void redisServer() throws Exception {
//
//        if (isArmMac()) {
//            redisServer = new RedisServer(Objects.requireNonNull(getRedisFileForArcMac()),
//                    redisPort);
//        }
//        if (!isArmMac()) {
//            redisServer = new RedisServer(redisPort);
//        }
//
//        redisServer.start();
//    }
//
//
//    @PreDestroy
//    public void stopRedis() {
//        if (redisServer != null) {
//            redisServer.stop();
//        }
//    }

    @PostConstruct
    public void startRedis() throws Exception {
        int port = isRedisRunning() ? findAvailablePort() : redisPort;
        if (isArmArchitecture()) {
            redisServer = new RedisServer(Objects.requireNonNull(getRedisServerExecutable()), port);
        } else {
            redisServer = RedisServer.builder()
                    .port(port)
                    .setting("maxmemory 128M")
                    .build();
        }
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        redisServer.stop();
    }
    public int findAvailablePort() throws Exception {
        for (int port = 10000; port <= 65535; port++) {
            Process process = executeGrepProcessCommand(port);
            if (!isRunning(process)) {
                return port;
            }
        }

        throw new Exception();
    }

    private boolean isRedisRunning() throws Exception {
        return isRunning(executeGrepProcessCommand(redisPort));
    }

    private Process executeGrepProcessCommand(int redisPort) throws IOException {
        String command = String.format("netstat -nat | grep LISTEN|grep %d", redisPort);
        String[] shell = {"/bin/sh", "-c", command};

        return Runtime.getRuntime().exec(shell);
    }

    private boolean isRunning(Process process) throws Exception {
        String line;
        StringBuilder pidInfo = new StringBuilder();
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in)); //선언

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }
        } catch (Exception e) {
            throw new Exception();
        }
        return StringUtils.hasText(pidInfo.toString());
    }

    private File getRedisServerExecutable() throws Exception {
        try {
            return new ClassPathResource("binary/redis/redis-server-6.0.10-mac-arm64").getFile();
        } catch (Exception e) {
            throw new Exception();
        }
    }

    private boolean isArmArchitecture() {
        return System.getProperty("os.arch").contains("aarch64");
    }

}