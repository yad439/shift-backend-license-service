package ru.cft.licenseservice.dto;

import lombok.Value;

import java.security.KeyPair;
import java.time.Instant;

@Value
public class LicenseDto {
	long id;
	long userId;
	String userPrimaryEmail;
	Instant startDate;
	Instant expirationDate;
	KeyPair keyPair;
}
