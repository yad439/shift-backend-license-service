package ru.cft.licenseservice.dto;

import lombok.Builder;
import lombok.Value;

import java.security.PublicKey;
import java.time.Instant;

@Value
@Builder
public class LicenseFileDto {
	public static final int SERIALISATION_ID = 16;

	long id;
	long userId;
	String userPrimaryEmail;
	Instant startDate;
	Instant expirationDate;
	PublicKey key;
	byte[] signature;
}
