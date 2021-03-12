package ru.cft.licenseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cft.licenseservice.dto.LicenseFileDto;
import ru.cft.licenseservice.exception.ActionNotPermittedException;
import ru.cft.licenseservice.exception.InvalidFileException;
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
		} catch (ActionNotPermittedException e) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
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

	@PostMapping("/list")
	ResponseEntity<?> list(@RequestBody String body) {
		long userId = Long.parseLong(body);
		try {
			return ResponseEntity.ok(licenseService.listLicenses(userId));
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/check")
	public ResponseEntity<String> check(@RequestBody byte[] file) {
		LicenseFileDto licenseFileDto;
		try {
			licenseFileDto = serializationService.deserialize(file);
		} catch (InvalidFileException e) {
			return new ResponseEntity<>(LicenseService.LicenseStatus.LICENSE_INVALID.toString(), HttpStatus.valueOf(418));
		}
		LicenseService.LicenseStatus status = licenseService.checkLicense(licenseFileDto);
		if (status == LicenseService.LicenseStatus.LICENSE_VALID) {
			return ResponseEntity.ok(LicenseService.LicenseStatus.LICENSE_VALID.toString());
		} else {
			return new ResponseEntity<>(status.toString(), HttpStatus.valueOf(418));
		}
	}
}
