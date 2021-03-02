package ru.cft.licenseservice.controller;

import org.springframework.web.bind.annotation.*;

import ru.cft.licenseservice.dto.UserDto;
import ru.cft.licenseservice.service.UserCRUDService;

@RestController
@RequestMapping("/user")
public class UserCRUDController {
	private final UserCRUDService userService;

	public UserCRUDController(final UserCRUDService userService) {
		this.userService = userService;
	}

	@GetMapping("/{id}")
	public UserDto get(@PathVariable("id") long id) {
		return userService.getUser(id);
	}

	@PostMapping
	public void create(@RequestBody UserDto userDto) {
		userService.createUser(userDto);
	}
}
