package com.example;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("redirectLongPolling")
public class RedirectLongPollingController {
    private static final HashMap<Integer, Future<String>> SUBMITTED_TASKS = new HashMap<>();

    private final AsyncTaskExecutor asyncTaskExecutor;
    private final BusinessService businessService;

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody RedirectView getTheAnswer() {
        log.info("New job request arrived");
        final Future<String> processFutureResult = asyncTaskExecutor.submit(businessService::process);
        final int id = processFutureResult.hashCode();
        SUBMITTED_TASKS.put(id, processFutureResult);

        log.info("Job is submitted with id {}", id);
        return new RedirectView("/redirectLongPolling/" + id);
    }

    @GetMapping(value = "/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody Object getCurrentStatus(@PathVariable int id) {
        log.info("Going to fetch the status of task {}", id);
        try {
            final Future<String> futureToCheck = SUBMITTED_TASKS.get(id);
            final String result = futureToCheck.get(5, TimeUnit.SECONDS);
            return result;
        } catch (final TimeoutException timeoutException) {
            log.info("Task {} is not yet finished", id);
            return new RedirectView("" + id);
        } catch (final Exception exception) {
            log.warn("Task {} failed", id, exception);
            return "Task failed";
        }
    }
}