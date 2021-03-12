package ru.cft.licenseservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.cft.licenseservice.dto.LicenseFileDto;
import ru.cft.licenseservice.exception.ActionNotPermittedException;
import ru.cft.licenseservice.exception.InvalidFileException;
import ru.cft.licenseservice.repository.LicenseRepository;
import ru.cft.licenseservice.repository.SimpleRepository;
import ru.cft.licenseservice.repository.UserRepository;
import ru.cft.licenseservice.service.LicenseSerializationService;
import ru.cft.licenseservice.service.LicenseService;

import java.security.PublicKey;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@WebMvcTest(LicenseController.class)
class LicenseControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private LicenseService licenseService;
	@MockBean
	private LicenseSerializationService serializationService;
	@MockBean
	private SimpleRepository simpleRepository;
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private LicenseRepository licenseRepository;
	private LicenseFileDto license;
	private byte[] serialized;

	@BeforeEach
	private void setUp() {
		var key = Mockito.mock(PublicKey.class);
		var startDate = Instant.ofEpochSecond(1974L);
		var expirationDate = Instant.ofEpochSecond(1984L);
		byte[] sig = {(byte) 202, (byte) 254, (byte) 186, (byte) 190};
		license = LicenseFileDto.builder()
		                        .id(1L)
		                        .userId(2L)
		                        .key(key)
		                        .startDate(startDate)
		                        .expirationDate(expirationDate)
		                        .userPrimaryEmail("john.doe@example.com")
		                        .signature(sig)
		                        .build();
		serialized = new byte[]{0x44, 0x69, 0x67, 0x69, 0x74, 0x61, 0x6c, 0x20, 0x53, 0x68, 0x61, 0x64, 0x6f, 0x77};
	}

	@Test
	void when_licenseCreationRequested_expect_newLicenseReturned() throws Exception {
		Mockito.when(licenseService.generateLicense(2L)).thenReturn(license);
		Mockito.when(serializationService.serialize(license)).thenReturn(serialized);

		mockMvc.perform(MockMvcRequestBuilders.post("/license/new").content("2"))
		       .andExpect(MockMvcResultMatchers.status().isOk())
		       .andExpect(MockMvcResultMatchers.content().bytes(serialized));
	}

	@Test
	void when_licenseForNonExistingUserRequested_expect_notFoundReturned() throws Exception {
		Mockito.when(licenseService.generateLicense(42L)).thenThrow(NoSuchElementException.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/license/new").content("42"))
		       .andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	void when_licenseRequestedButNotPermitted_expect_forbiddenReturned() throws Exception {
		Mockito.when(licenseService.generateLicense(3L)).thenThrow(ActionNotPermittedException.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/license/new").content("3"))
		       .andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	void when_existingLicenseRequested_expect_licenseReturned() throws Exception {
		Mockito.when(licenseService.getLicense(1L, 2L)).thenReturn(license);
		Mockito.when(serializationService.serialize(license)).thenReturn(serialized);

		mockMvc.perform(MockMvcRequestBuilders.post("/license/{id}", 1).content("2"))
		       .andExpect(MockMvcResultMatchers.status().isOk())
		       .andExpect(MockMvcResultMatchers.content().bytes(serialized));
	}

	@Test
	void when_nonExistingLicenseRequested_expect_notFoundReturned() throws Exception {
		Mockito.when(licenseService.getLicense(21L, 42L)).thenThrow(NoSuchElementException.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/license/{id}", 21).content("42"))
		       .andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	void when_licenseListRequested_expect_idArrayReturned() throws Exception {
		var list = List.of(14L, 15L, 9L, 26L, 5L, 4L);
		Mockito.when(licenseService.listLicenses(2L)).thenReturn(list);

		mockMvc.perform(MockMvcRequestBuilders.post("/license/list").content("2"))
		       .andExpect(MockMvcResultMatchers.status().isOk())
		       .andExpect(MockMvcResultMatchers.content().json("[14,15,9,26,5,4]"));
	}

	@Test
	void when_licenseListOfNonExistingUserRequested_expect_notFoundReturned() throws Exception {
		Mockito.when(licenseService.listLicenses(42L)).thenThrow(NoSuchElementException.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/license/list").content("42"))
		       .andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	void when_licenseIsCorrect_expect_licenseValidReturned() throws Exception {
		Mockito.when(serializationService.deserialize(serialized)).thenReturn(license);
		Mockito.when(licenseService.checkLicense(license)).thenReturn(LicenseService.LicenseStatus.LICENSE_VALID);

		mockMvc.perform(MockMvcRequestBuilders.post("/license/check").content(serialized))
		       .andExpect(MockMvcResultMatchers.status().isOk())
		       .andExpect(MockMvcResultMatchers.content().string("LICENSE_VALID"));
	}

	@ParameterizedTest
	@EnumSource(value = LicenseService.LicenseStatus.class, names = {"LICENSE_VALID"}, mode = EnumSource.Mode.EXCLUDE)
	void when_licenseInIncorrect_expect_statusReturned(LicenseService.LicenseStatus status) throws Exception {
		Mockito.when(serializationService.deserialize(serialized)).thenReturn(license);
		Mockito.when(licenseService.checkLicense(license)).thenReturn(status);

		mockMvc.perform(MockMvcRequestBuilders.post("/license/check").content(serialized))
		       .andExpect(MockMvcResultMatchers.status().is(418))
		       .andExpect(MockMvcResultMatchers.content().string(status.toString()));
	}

	@Test
	void when_cannotParseLicenseFile_expect_licenseIncorrectReturned() throws Exception {
		Mockito.when(serializationService.deserialize(serialized)).thenThrow(InvalidFileException.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/license/check").content(serialized))
		       .andExpect(MockMvcResultMatchers.status().is(418))
		       .andExpect(MockMvcResultMatchers.content().string("LICENSE_INVALID"));
	}
}