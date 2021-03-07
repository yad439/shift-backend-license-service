package ru.cft.licenseservice.dto;

import lombok.Data;

import java.security.KeyPair;
import java.time.Instant;

@Data
public class LicenseDto {
	private long id;
	private long userId;
	private String userPrimaryEmail;
	private Instant startDate;
	private Instant expirationDate;
	private KeyPair keyPair;
}
