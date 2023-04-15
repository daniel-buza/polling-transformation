package com.example;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class BusinessService {

    @SneakyThrows(InterruptedException.class)
    public String process() {
        final String result = UUID.randomUUID().toString();

        log.info("Process started, going to sleep for a while before returning {}...", result);
        Thread.sleep(15_000);
        log.info("Process going to return result {} now", result);

        return result;
    }
}
