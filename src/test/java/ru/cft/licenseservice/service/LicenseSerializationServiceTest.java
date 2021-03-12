package ru.cft.licenseservice.service;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.stubbing.Answer;
import org.modelmapper.ModelMapper;
import ru.cft.licenseservice.dto.LicenseFileDto;
import ru.cft.licenseservice.dto.LicenseFileSerializationDto;

import java.security.PublicKey;
import java.time.Instant;

class LicenseSerializationServiceTest {
	@InjectMocks
	private LicenseSerializationService service;
	@Mock
	private Kryo kryo;
	@Mock
	private ModelMapper mapper;
	private LicenseFileDto licenseFileDto;
	private LicenseFileSerializationDto serializationDto;
	private byte[] serialized;
	private AutoCloseable mocksClosable;

	@BeforeEach
	void setUp() {
		mocksClosable = MockitoAnnotations.openMocks(this);

		var key = Mockito.mock(PublicKey.class);
		var startDate = Instant.ofEpochSecond(1974L);
		var expirationDate = Instant.ofEpochSecond(1984L);
		byte[] sig = {(byte) 202, (byte) 254, (byte) 186, (byte) 190};
		licenseFileDto = LicenseFileDto.builder()
		                               .id(1L)
		                               .userId(2L)
		                               .key(key)
		                               .startDate(startDate)
		                               .expirationDate(expirationDate)
		                               .userPrimaryEmail("john.doe@example.com")
		                               .signature(sig)
		                               .build();
		serializationDto = new LicenseFileSerializationDto();
		serializationDto.setId(licenseFileDto.getId());
		serializationDto.setUserId(licenseFileDto.getUserId());
		serializationDto.setUserPrimaryEmail(licenseFileDto.getUserPrimaryEmail());
		serializationDto.setSignature(licenseFileDto.getSignature());
		serializationDto.setStartDate(licenseFileDto.getStartDate().getEpochSecond());
		serializationDto.setExpirationDate(licenseFileDto.getExpirationDate().getEpochSecond());
		serializationDto.setKey(new byte[]{(byte) 71, (byte) 8, (byte) 28, (byte) 182, (byte) 85});
		serialized = new byte[]{(byte) 0x44, (byte) 0x69, (byte) 0x67, (byte) 0x69, (byte) 0x74, (byte) 0x61, (byte) 0x6c, (byte) 0x20, (byte) 0x53, (byte) 0x68, (byte) 0x61, (byte) 0x64, (byte) 0x6f, (byte) 0x77};
	}

	@AfterEach
	void tearDown() throws Exception {
		mocksClosable.close();
	}

	@Test
	void when_serializing_expect_serializedDataReturned() {
		Mockito.when(mapper.map(licenseFileDto, LicenseFileSerializationDto.class)).thenReturn(serializationDto);
		Mockito.doAnswer((Answer<Void>) invocation -> {
			Output output = invocation.getArgument(0);
			output.write(serialized);
			return null;
		}).when(kryo).writeObject(ArgumentMatchers.any(Output.class), ArgumentMatchers.same(serializationDto));

		var result = service.serialize(licenseFileDto);

		Assertions.assertArrayEquals(serialized, result);
	}

	@Test
	void when_deserializing_expect_deserializedClassReturned() {
		final byte[][] captured = new byte[1][];
		Mockito.when(kryo.readObject(ArgumentMatchers.any(Input.class), ArgumentMatchers.same(LicenseFileSerializationDto.class)))
		       .thenAnswer((Answer<LicenseFileSerializationDto>) invocation -> {
			       captured[0] = invocation.getArgument(0, Input.class).readAllBytes();
			       return serializationDto;
		       });
		Mockito.when(mapper.map(serializationDto, LicenseFileDto.class)).thenReturn(licenseFileDto);

		var result = service.deserialize(serialized);
		Assertions.assertSame(licenseFileDto, result);
		Assertions.assertArrayEquals(serialized, captured[0]);
	}
}