package ru.cft.licenseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PublicKey;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseFileDto {

	private long id;
	private long userId;
	private String userPrimaryEmail;
	private Instant startDate;
	private Instant expirationDate;
	private PublicKey key;
	private byte[] signature;
}
