package ru.cft.licenseservice.repository;

import org.springframework.data.repository.CrudRepository;
import ru.cft.licenseservice.entity.License;

public interface LicenseRepository extends CrudRepository<License, Long> {}
