package ru.cft.licenseservice.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.cft.licenseservice.dto.LicenseDto;
import ru.cft.licenseservice.dto.LicenseFileDto;
import ru.cft.licenseservice.entity.License;
import ru.cft.licenseservice.entity.User;
import ru.cft.licenseservice.exception.ActionNotPermittedException;
import ru.cft.licenseservice.repository.LicenseRepository;
import ru.cft.licenseservice.repository.UserRepository;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.SignatureException;
import java.time.Instant;
import java.time.Period;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LicenseService {
	private static final Period DEFAULT_LICENSE_LENGTH = Period.ofDays(30);

	private final LicenseCryptographyService cryptographyService;
	private final LicenseRepository licenseRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	public LicenseFileDto generateLicense(long userId) {
		User user = userRepository.findById(userId).orElseThrow();
		if (!user.isCompany() && !user.getLicenses().isEmpty()) throw new ActionNotPermittedException();

		KeyPair keyPair = cryptographyService.generateKeyPair();
		Instant startDate = Instant.now();
		Instant expirationDate = startDate.plus(DEFAULT_LICENSE_LENGTH);

		License license = new License();
		license.setUser(user);
		license.setKeyPair(keyPair);
		license.setStartDate(startDate);
		license.setExpirationDate(expirationDate);
		licenseRepository.save(license);

		LicenseDto dto = modelMapper.map(license, LicenseDto.class);
		try {
			return cryptographyService.sign(dto);
		} catch (InvalidKeyException | SignatureException e) {
			throw new RuntimeException(e);
		}
	}

	public LicenseFileDto getLicense(long licenseId, long userId) {
		License license = licenseRepository.findById(licenseId).orElseThrow();
		if (license.getUser().getId() != userId) throw new NoSuchElementException();

		LicenseDto dto = modelMapper.map(license, LicenseDto.class);
		try {
			return cryptographyService.sign(dto);
		} catch (InvalidKeyException | SignatureException e) {
			throw new RuntimeException(e);
		}
	}

	public LicenseStatus checkLicense(LicenseFileDto licenseFile) {
		Optional<License> licenseOptional = licenseRepository.findById(licenseFile.getId());
		if (licenseOptional.isEmpty()) return LicenseStatus.LICENsE_NOT_EXISTS;

		License license = licenseOptional.get();
		if (!license.getKeyPair().getPublic().equals(licenseFile.getKey())) return LicenseStatus.LICENSE_INVALID;

		try {
			if (!cryptographyService.isValidSignature(licenseFile)) return LicenseStatus.LICENSE_INVALID;
		} catch (InvalidKeyException | SignatureException e) {
			throw new RuntimeException(e);
		}

		if (Instant.now().isAfter(licenseFile.getExpirationDate())) return LicenseStatus.LICENSE_EXPIRED;

		return LicenseStatus.LICENSE_VALID;
	}

	public enum LicenseStatus {LICENSE_VALID, LICENsE_NOT_EXISTS, LICENSE_INVALID, LICENSE_EXPIRED}
}
