package ru.cft.licenseservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.security.KeyPair;
import java.time.Instant;

@Entity
@Table(name = "licenses")
@Data
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
    private Instant startDate;
    private Instant expirationDate;
    @Column(length = 2048)
    private KeyPair keyPair;
}
