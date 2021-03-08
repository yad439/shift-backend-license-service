package ru.cft.licenseservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cft.licenseservice.dto.UserDto;
import ru.cft.licenseservice.service.UserCRUDService;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/user")
public class UserCRUDController {
	private final UserCRUDService userService;

	public UserCRUDController(final UserCRUDService userService) {
		this.userService = userService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> get(@PathVariable("id") long id) {
		try {
			return ResponseEntity.ok(userService.getUser(id));
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public long create(@RequestBody UserDto userDto) {
		return userService.createUser(userDto);
	}

	@PostMapping("/{id}")
	public ResponseEntity<Void> update(@PathVariable("id") long id, @RequestBody UserDto userDto) {
		try {
			userService.updateUser(id, userDto);
			return ResponseEntity.ok().build();
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
