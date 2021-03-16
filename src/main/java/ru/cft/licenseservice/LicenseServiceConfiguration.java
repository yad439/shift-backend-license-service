package ru.cft.licenseservice;

import com.esotericsoftware.kryo.kryo5.Kryo;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.cft.licenseservice.dto.LicenseFileSerializationDto;
import ru.cft.licenseservice.exception.InvalidFileException;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
public class LicenseServiceConfiguration {
	private static final int LICENSE_FILE_SERIALIZATION_ID = 16;
	private static final String KEY_TYPE = "RSA";
	private static final String SIGNATURE_TYPE = "SHA256withRSA";
	private static final int KEY_SIZE = 2048;

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		modelMapper.addConverter(new Converter<Instant, Long>() {//lambda doesn't work for unknown reason
			@Override
			public Long convert(final MappingContext<Instant, Long> context) {
				return context.getSource().getEpochSecond();
			}
		});
		modelMapper.addConverter(new Converter<Long, Instant>() {
			@Override
			public Instant convert(final MappingContext<Long, Instant> context) {
				return Instant.ofEpochSecond(context.getSource());
			}
		});
		modelMapper.addConverter(new Converter<PublicKey, byte[]>() {
			@Override
			public byte[] convert(final MappingContext<PublicKey, byte[]> context) {
				return context.getSource().getEncoded();
			}
		});
		modelMapper.addConverter(new Converter<byte[], PublicKey>() {
			@Override
			public PublicKey convert(final MappingContext<byte[], PublicKey> context) {
				try {
					KeyFactory keyFactory = KeyFactory.getInstance(KEY_TYPE);
					KeySpec keySpecX509 = new X509EncodedKeySpec(context.getSource());
					return keyFactory.generatePublic(keySpecX509);
				} catch (NoSuchAlgorithmException e) {
					throw new RuntimeException(e);
				} catch (InvalidKeySpecException e) {
					throw new InvalidFileException(e);
				}
			}
		});
		return modelMapper;
	}

	@Bean
	public Kryo kryo() {
		Kryo kryo = new Kryo();
		kryo.register(byte[].class);
		kryo.register(LicenseFileSerializationDto.class, LICENSE_FILE_SERIALIZATION_ID);
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
