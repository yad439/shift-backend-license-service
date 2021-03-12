package ru.cft.licenseservice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.cft.licenseservice.dto.LicenseDto;
import ru.cft.licenseservice.dto.LicenseFileDto;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Instant;

class LicenseCryptographyServiceTest {
	private AutoCloseable mocksClosable;
	@InjectMocks
	private LicenseCryptographyService service;
	@Mock
	private KeyPairGenerator keyPairGenerator;
	@Mock
	private Signature signature;
	@Captor
	private ArgumentCaptor<ByteBuffer> byteBufferCaptor;
	@Captor
	private ArgumentCaptor<byte[]> byteCaptor;
	private LicenseDto licenseDto;
	private LicenseFileDto licenseFileDto;
	private KeyPair keyPair;
	@Mock
	private PrivateKey privateKey;
	@Mock
	private PublicKey publicKey;


	@BeforeEach
	void setUp() {
		mocksClosable = MockitoAnnotations.openMocks(this);

		keyPair = new KeyPair(publicKey, privateKey);

		var startDate = Instant.ofEpochSecond(74L);
		var expirationDate = Instant.ofEpochSecond(84L);
		byte[] sig = {(byte) 202, (byte) 254, (byte) 186, (byte) 190};
		licenseFileDto = LicenseFileDto.builder()
		                               .id(1L)
		                               .userId(2L)
		                               .key(publicKey)
		                               .startDate(startDate)
		                               .expirationDate(expirationDate)
		                               .userPrimaryEmail("john.doe@example.com")
		                               .signature(sig)
		                               .build();
		licenseDto = new LicenseDto();
		licenseDto.setId(licenseFileDto.getId());
		licenseDto.setUserId(licenseFileDto.getUserId());
		licenseDto.setStartDate(licenseFileDto.getStartDate());
		licenseDto.setExpirationDate(licenseFileDto.getExpirationDate());
		licenseDto.setUserPrimaryEmail(licenseFileDto.getUserPrimaryEmail());
		licenseDto.setKeyPair(keyPair);
	}

	@AfterEach
	void tearDown() throws Exception {
		mocksClosable.close();
	}

	@Test
	void when_generatingKey_expect_generatedKeyPair() {
		Mockito.when(keyPairGenerator.generateKeyPair()).thenReturn(keyPair);

		var result = service.generateKeyPair();
		Assertions.assertSame(keyPair, result);
	}

	@Test
	void when_signingLicense_expect_signedLicense() throws SignatureException, InvalidKeyException {
		Mockito.doReturn(licenseFileDto.getSignature()).when(signature).sign();

		var result = service.sign(licenseDto);

		Assertions.assertEquals(licenseFileDto, result);
		Mockito.verify(signature).initSign(privateKey);
		Mockito.verify(signature).update(byteBufferCaptor.capture());
		Mockito.verify(signature).update(byteCaptor.capture());

		byte[] content = {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 74, 0, 0, 0, 0, 0, 0, 0, 84};

		Assertions.assertArrayEquals(content, byteBufferCaptor.getValue().array());
		Assertions.assertArrayEquals(licenseFileDto.getUserPrimaryEmail().getBytes(StandardCharsets.UTF_8), byteCaptor.getValue());
	}

	@Test
	void when_verifying_expect_verification() throws SignatureException, InvalidKeyException {
		Mockito.doReturn(true).when(signature).verify(licenseFileDto.getSignature());

		var res = service.isValidSignature(licenseFileDto);

		Assertions.assertTrue(res);
		Mockito.verify(signature).initVerify(publicKey);
		Mockito.verify(signature).update(byteBufferCaptor.capture());
		Mockito.verify(signature).update(byteCaptor.capture());

		byte[] content = {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 74, 0, 0, 0, 0, 0, 0, 0, 84};

		Assertions.assertArrayEquals(content, byteBufferCaptor.getValue().array());
		Assertions.assertArrayEquals(licenseFileDto.getUserPrimaryEmail().getBytes(StandardCharsets.UTF_8), byteCaptor.getValue());
	}
}