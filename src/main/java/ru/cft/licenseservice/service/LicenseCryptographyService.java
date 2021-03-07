package ru.cft.licenseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.cft.licenseservice.dto.LicenseDto;
import ru.cft.licenseservice.dto.LicenseFileDto;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;

@Service
@RequiredArgsConstructor
public class LicenseCryptographyService {
	private final KeyPairGenerator keyPairGenerator;
	private final Signature signature;

	public KeyPair generateKeyPair() {
		return keyPairGenerator.generateKeyPair();
	}

	public LicenseFileDto sign(LicenseDto licenseDto) throws InvalidKeyException, SignatureException {
		signature.initSign(licenseDto.getKeyPair().getPrivate());
		ByteBuffer buffer = ByteBuffer.allocate(8 * 4);
		buffer.putLong(licenseDto.getId());
		buffer.putLong(licenseDto.getUserId());
		buffer.putLong(licenseDto.getStartDate().getEpochSecond());
		buffer.putLong(licenseDto.getExpirationDate().getEpochSecond());
		buffer.flip();
		signature.update(buffer);
		signature.update(licenseDto.getUserPrimaryEmail().getBytes(StandardCharsets.UTF_8));
		byte[] signed = signature.sign();
		return LicenseFileDto.builder()
		                     .id(licenseDto.getId())
		                     .userId(licenseDto.getUserId())
		                     .userPrimaryEmail(licenseDto.getUserPrimaryEmail())
		                     .startDate(licenseDto.getStartDate())
		                     .expirationDate(licenseDto.getExpirationDate())
		                     .key(licenseDto.getKeyPair().getPublic())
		                     .signature(signed)
		                     .build();
	}

	public boolean isValidSignature(LicenseFileDto licenseFileDto) throws InvalidKeyException, SignatureException {
		signature.initVerify(licenseFileDto.getKey());
		ByteBuffer buffer = ByteBuffer.allocate(8 * 4);
		buffer.putLong(licenseFileDto.getId());
		buffer.putLong(licenseFileDto.getUserId());
		buffer.putLong(licenseFileDto.getStartDate().getEpochSecond());
		buffer.putLong(licenseFileDto.getExpirationDate().getEpochSecond());
		buffer.flip();
		signature.update(buffer);
		signature.update(licenseFileDto.getUserPrimaryEmail().getBytes(StandardCharsets.UTF_8));
		return signature.verify(licenseFileDto.getSignature());
	}
}
