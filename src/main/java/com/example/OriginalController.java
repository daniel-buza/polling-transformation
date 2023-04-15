package com.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("original")
@RequiredArgsConstructor
public class OriginalController {

    private final BusinessService businessService;

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getTheAnswer() {
        log.info("Original controller invoked");
        final String result = businessService.process();

        log.info("Original controller returning now");
        return result;
    }
}
