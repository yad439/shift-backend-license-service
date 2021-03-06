package ru.cft.licenseservice.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.cft.licenseservice.dto.UserDto;
import ru.cft.licenseservice.entity.User;
import ru.cft.licenseservice.repository.UserRepository;

@Service
public class UserCRUDService {
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	public UserCRUDService(UserRepository userRepository, ModelMapper modelMapper) {
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
	}

	public long createUser(UserDto userDto) {
		User entity = modelMapper.map(userDto, User.class);
		User createdUser = userRepository.save(entity);
		return createdUser.getId();
	}

	public UserDto getUser(long id) {
		User entity = userRepository.findById(id).orElseThrow();
		return modelMapper.map(entity, UserDto.class);
	}

	public void updateUser(long id,UserDto userDto){
		User entity = userRepository.findById(id).orElseThrow();
		modelMapper.map(userDto,entity);
		userRepository.save(entity);
	}
}
