package ru.cft.licenseservice.service;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class ScheduledTasks {

    @Scheduled(fixedRate = 86400)
    public void reportCurrentTime() {

    }
}
