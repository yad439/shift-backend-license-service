package ru.cft.licenseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.cft.licenseservice.entity.ExpiringLicense;
import ru.cft.licenseservice.entity.License;
import ru.cft.licenseservice.repository.ExpiringLicenseRepository;
import ru.cft.licenseservice.repository.LicenseRepository;

import java.time.Instant;
import java.time.Period;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final ExpiringLicenseRepository expiringRepository;
    private final LicenseRepository licenseRepository;

    @Scheduled(fixedRateString = "${service.expiring-update.delay-in-milliseconds}")
    public void updateExpired() {
        System.out.println("test");
        Instant now = Instant.now();
        Instant expirationWarningDate = now.plus(Period.ofDays(7));
        expiringRepository.deleteByExpirationDateBeforeOrExpirationDateAfter(now, expirationWarningDate);
        Iterable<License> expiringLicenses = licenseRepository.findAllByExpirationDateAfterAndExpirationDateBefore(now, expirationWarningDate);
        for (License license : expiringLicenses) {
            ExpiringLicense expiringEntry = new ExpiringLicense();
            expiringEntry.setExpirationDate(license.getExpirationDate());
            expiringEntry.setLicense(license);
            license.setExpiringEntry(expiringEntry);
            licenseRepository.save(license);
        }
    }
}
