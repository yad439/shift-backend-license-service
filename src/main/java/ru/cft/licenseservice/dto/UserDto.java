package ru.cft.licenseservice.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
	private Long id;
	private Boolean company;
	private String primaryEmail;
	private Set<String> otherEmails;
}
