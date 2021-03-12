package ru.cft.licenseservice.repository;

import org.springframework.data.repository.CrudRepository;
import ru.cft.licenseservice.entity.License;

import java.time.Instant;

public interface LicenseRepository extends CrudRepository<License, Long> {
	Iterable<License> findAllByExpirationDateAfterAndExpirationDateBefore(Instant min, Instant max);
}
