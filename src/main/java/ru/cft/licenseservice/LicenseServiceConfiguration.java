package ru.cft.licenseservice;

import com.esotericsoftware.kryo.kryo5.Kryo;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.cft.licenseservice.dto.LicenseFileDto;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.time.Instant;

@Configuration
@EnableAutoConfiguration
public class LicenseServiceConfiguration {
	private static final String KEY_TYPE = "RSA";
	private static final String SIGNATURE_TYPE = "SHA256withRSA";
	private static final int KEY_SIZE = 2048;

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		return modelMapper;
	}

	@Bean
	public Kryo kryo() {
		Kryo kryo = new Kryo();
		kryo.setRegistrationRequired(false);//todo bytes in dto
		kryo.register(Instant.class);
		kryo.register(LicenseFileDto.class, LicenseFileDto.SERIALISATION_ID);
		return kryo;
	}

	@Bean
	public KeyPairGenerator keyPairGenerator() throws NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_TYPE);
		generator.initialize(KEY_SIZE);
		return generator;
	}

	@Bean
	public Signature signature() throws NoSuchAlgorithmException {
		return Signature.getInstance(SIGNATURE_TYPE);
	}
}
