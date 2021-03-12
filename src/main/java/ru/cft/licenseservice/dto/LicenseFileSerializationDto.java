package ru.cft.licenseservice.dto;

import lombok.Data;

@Data
public class LicenseFileSerializationDto {
	private long id;
	private long userId;
	private String userPrimaryEmail;
	private long startDate;
	private long expirationDate;
	private byte[] key;
	private byte[] signature;
}
