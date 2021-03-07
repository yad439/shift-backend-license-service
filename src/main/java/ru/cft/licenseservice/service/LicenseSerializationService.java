package ru.cft.licenseservice.service;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.cft.licenseservice.dto.LicenseFileDto;

@Service
@RequiredArgsConstructor
public class LicenseSerializationService {
	private final Kryo kryo;

	public byte[] serialize(LicenseFileDto license) {
		try (Output output = new Output(2048)) {
			kryo.writeObject(output, license);
			return output.toBytes();
		}
	}

	public LicenseFileDto deserialize(byte[] file) {
		try (Input input = new Input(file)) {
			return kryo.readObject(input, LicenseFileDto.class);
		}
	}
}
