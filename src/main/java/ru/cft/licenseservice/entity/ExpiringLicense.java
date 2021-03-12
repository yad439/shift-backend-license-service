package ru.cft.licenseservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "expiring_licenses")
@Data
public class ExpiringLicense {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private Instant expirationDate;
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	private License license;
}
