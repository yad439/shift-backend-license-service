package ru.cft.licenseservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.cft.licenseservice.entity.ExpiringLicense;

import java.time.Instant;

@Repository
public interface ExpiringLicenseRepository extends CrudRepository<ExpiringLicense, Long> {
	void deleteByExpirationDateBeforeOrExpirationDateAfter(Instant min, Instant max);

}
