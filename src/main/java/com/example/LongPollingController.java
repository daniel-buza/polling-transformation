package com.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("longPolling")
public class LongPollingController {
    private static final HashMap<Integer, Future<String>> SUBMITTED_TASKS = new HashMap<>();

    private final AsyncTaskExecutor asyncTaskExecutor;
    private final BusinessService businessService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskStatus getTheAnswer() {
        log.info("New job request arrived");
        final Future<String> processFutureResult = asyncTaskExecutor.submit(businessService::process);
        final int id = processFutureResult.hashCode();
        SUBMITTED_TASKS.put(id, processFutureResult);

        log.info("Job is submitted with id {}", id);
        return new TaskStatus(id, "Submitted", null);
    }



    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskStatus getCurrentStatus(@PathVariable int id) {
        log.info("Going to fetch the status of task {}", id);
        try {
            final Future<String> futureToCheck = SUBMITTED_TASKS.get(id);
            final String result = futureToCheck.get(5, TimeUnit.SECONDS);
            return new TaskStatus(id, "Finished", result);
        } catch (final TimeoutException timeoutException) {
            log.info("Task {} is not yet finished", id);
            return new TaskStatus(id, "Running", null);
        } catch (final Exception exception) {
            log.warn("Task {} failed", id, exception);
            return new TaskStatus(id, "Failed", null);
        }
    }

    private record TaskStatus(int id, String status, String result) {}

}