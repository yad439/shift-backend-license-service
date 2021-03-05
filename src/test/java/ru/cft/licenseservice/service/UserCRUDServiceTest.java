package ru.cft.licenseservice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import ru.cft.licenseservice.dto.UserDto;
import ru.cft.licenseservice.entity.User;
import ru.cft.licenseservice.repository.UserRepository;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

class UserCRUDServiceTest {
	private AutoCloseable mocksClosable;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ModelMapper modelMapper;
	@InjectMocks
	private UserCRUDService service;
	private User testEntity;
	private UserDto testDto;

	@BeforeEach
	void setUp() {
		mocksClosable = MockitoAnnotations.openMocks(this);

		testDto = new UserDto();
		testDto.setIsCompany(false);
		testDto.setPrimaryEmail("test@example.com");
		testDto.setOtherEmails(Collections.emptySet());

		testEntity = new User();
		testEntity.setId(1L);
		testEntity.setPrimaryEmail(testDto.getPrimaryEmail());
		testEntity.setOtherEmails(testDto.getOtherEmails());
	}

	@AfterEach
	void tearDown() throws Exception {
		testEntity = null;
		testDto = null;
		mocksClosable.close();
	}

	@Test
	void when_creatingUser_expect_saveInDatabase() {
		Mockito.when(modelMapper.map(testDto, User.class)).thenReturn(testEntity);

		service.createUser(testDto);

		Mockito.verify(userRepository).save(testEntity);
	}

	@Test
	void when_requestsExistingUser_expect_userReturned() {
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(testEntity));
		Mockito.when(modelMapper.map(testEntity, UserDto.class)).thenReturn(testDto);

		var result = service.getUser(1L);

		Assertions.assertSame(testDto, result);
	}

	@Test
	void when_requestedUserNotExists_expect_throw() {
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

		Assertions.assertThrows(NoSuchElementException.class, () -> service.getUser(2L));
	}

	@Test
	void when_updatesExistingUser_expect_saveUpdatedUser() {
		var newDto = new UserDto();
		newDto.setIsCompany(true);
		newDto.setPrimaryEmail("test2@example.com");
		newDto.setOtherEmails(Collections.emptySet());

		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(testEntity));
		Mockito.doAnswer(args -> {
			var dto = (UserDto) args.getArgument(0);
			var entity = (User) args.getArgument(1);
			entity.setPrimaryEmail(dto.getPrimaryEmail());
			entity.setCompany(dto.getIsCompany());
			entity.setOtherEmails(dto.getOtherEmails());
			return null;
		}).when(modelMapper).map(newDto, testEntity);

		service.updateUser(1L, newDto);

		Assertions.assertEquals(newDto.getPrimaryEmail(), testEntity.getPrimaryEmail());
		Assertions.assertEquals(newDto.getIsCompany(), testEntity.isCompany());
		Assertions.assertEquals(newDto.getOtherEmails(), testEntity.getOtherEmails());

		Mockito.verify(modelMapper).map(newDto, testEntity);
		Mockito.verify(userRepository).save(testEntity);
	}

	@Test
	void when_requestedUpdateOfNotExistingUser_expect_throws() {
		var newDto = new UserDto();
		newDto.setIsCompany(true);
		newDto.setPrimaryEmail("test2@example.com");
		newDto.setOtherEmails(Collections.emptySet());

		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

		Assertions.assertThrows(NoSuchElementException.class, () -> service.updateUser(2L, newDto));
	}
}