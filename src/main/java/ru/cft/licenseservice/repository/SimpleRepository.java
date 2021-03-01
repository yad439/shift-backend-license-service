package ru.cft.licenseservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.cft.licenseservice.entity.License;

@Repository
public interface SimpleRepository extends CrudRepository<License, Long> {
}
