package ru.cft.licenseservice.service;

import org.springframework.stereotype.Service;
import ru.cft.licenseservice.entity.License;
import ru.cft.licenseservice.repository.SimpleRepository;

@Service
public class SimpleService {

    private final SimpleRepository simpleRepository;

    public SimpleService(SimpleRepository simpleRepository) {
        this.simpleRepository = simpleRepository;
    }

    public License createLicense() {
        License license = new License();
        simpleRepository.save(license);
        return license;
    }
}
