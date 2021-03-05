package ru.cft.licenseservice.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import ru.cft.licenseservice.dto.UserDto;
import ru.cft.licenseservice.service.UserCRUDService;

import java.util.Collections;
import java.util.NoSuchElementException;

class UserCRUDControllerTest {
	private AutoCloseable mocksClosable;
	@Mock
	private UserCRUDService service;
	@InjectMocks
	private UserCRUDController controller;
	private UserDto testDto;

	@BeforeEach
	void setUp() {
		mocksClosable = MockitoAnnotations.openMocks(this);

		testDto = new UserDto();
		testDto.setIsCompany(false);
		testDto.setPrimaryEmail("test@example.com");
		testDto.setOtherEmails(Collections.emptySet());
	}

	@AfterEach
	void tearDown() throws Exception {
		testDto = null;
		mocksClosable.close();
	}

	@Test
	void when_existingUserIsRequested_expect_userReturned() {
		Mockito.when(service.getUser(1L)).thenReturn(testDto);

		var result = controller.get(1L);

		Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
		Assertions.assertSame(testDto, result.getBody());
	}

	@Test
	void when_nonExistingUserIsRequested_expect_returnNotFound() {
		Mockito.when(service.getUser(2L)).thenThrow(NoSuchElementException.class);

		var result = controller.get(2L);

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
	}

	@Test
	void when_userCreationRequested_expect_userCreationCall() {
		controller.create(testDto);

		Mockito.verify(service).createUser(testDto);
	}

	@Test
	void when_existingUserUpdateRequested_expect_successfulUpdate() {
		var result = controller.update(1L, testDto);

		Mockito.verify(service).updateUser(1L, testDto);
		Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void when_nonExistingUserUpdateRequested_expect_notFoundReturned() {
		Mockito.doThrow(NoSuchElementException.class).when(service).updateUser(2L, testDto);

		var result = controller.update(2L, testDto);

		Assertions.assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
	}
}