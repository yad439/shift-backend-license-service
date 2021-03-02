package ru.cft.licenseservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String primaryEmail;
	@ElementCollection
	private Set<String> otherEmails;
}