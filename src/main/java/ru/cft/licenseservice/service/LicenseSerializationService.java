package ru.cft.licenseservice.service;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.KryoException;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.cft.licenseservice.dto.LicenseFileDto;
import ru.cft.licenseservice.dto.LicenseFileSerializationDto;
import ru.cft.licenseservice.exception.InvalidFileException;

@Service
@RequiredArgsConstructor
public class LicenseSerializationService {
	private final Kryo kryo;
	private final ModelMapper modelMapper;

	public byte[] serialize(LicenseFileDto license) {
		LicenseFileSerializationDto prepared = modelMapper.map(license, LicenseFileSerializationDto.class);
		try (Output output = new Output(2048)) {
			kryo.writeObject(output, prepared);
			return output.toBytes();
		}
	}

	public LicenseFileDto deserialize(byte[] file) {
		try (Input input = new Input(file)) {
			LicenseFileSerializationDto serialised = kryo.readObject(input, LicenseFileSerializationDto.class);
			return modelMapper.map(serialised, LicenseFileDto.class);
		} catch (KryoException e) {
			throw new InvalidFileException(e);
		}
	}
}
