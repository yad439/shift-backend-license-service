package ru.cft.licenseservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.cft.licenseservice.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {}
