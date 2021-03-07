package ru.cft.licenseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cft.licenseservice.dto.LicenseFileDto;
import ru.cft.licenseservice.service.LicenseSerializationService;
import ru.cft.licenseservice.service.LicenseService;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
class LicenseController {
	private static final String DEFAULT_FILENAME = "license.lic";

	private final LicenseService licenseService;
	private final LicenseSerializationService serializationService;

	@PostMapping("/new")
	public ResponseEntity<?> create(@RequestBody String body) {
		try {
			long userId = Long.parseLong(body);
			LicenseFileDto licenseFileDto = licenseService.generateLicense(userId);
			byte[] file = serializationService.serialize(licenseFileDto);
			ContentDisposition disposition = ContentDisposition.attachment().filename(DEFAULT_FILENAME).build();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDisposition(disposition);
			return new ResponseEntity<>(file, headers, HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/{licenseId}")
	public ResponseEntity<?> get(@PathVariable(name = "licenseId") long licenseId, @RequestBody String body) {
		try {
			long userId = Long.parseLong(body);
			LicenseFileDto licenseFileDto = licenseService.getLicense(licenseId, userId);
			byte[] file = serializationService.serialize(licenseFileDto);
			ContentDisposition disposition = ContentDisposition.attachment().filename(DEFAULT_FILENAME).build();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDisposition(disposition);
			return new ResponseEntity<>(file, headers, HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/check")
	public ResponseEntity<LicenseService.LicenseStatus> check(@RequestBody byte[] file) {
		LicenseFileDto licenseFileDto = serializationService.deserialize(file);
		LicenseService.LicenseStatus status = licenseService.checkLicense(licenseFileDto);
		if (status == LicenseService.LicenseStatus.LICENSE_VALID) {
			return ResponseEntity.ok(LicenseService.LicenseStatus.LICENSE_VALID);
		} else {
			return new ResponseEntity<>(status, HttpStatus.valueOf(418));
		}
	}
}
