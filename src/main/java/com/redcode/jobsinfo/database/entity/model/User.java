package com.redcode.jobsinfo.database.entity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.redcode.jobsinfo.common.enums.Gender;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
    private Long id;

    @Size(max = 25)
    @Column(name = "first_name", length = 25)
    private String firstName;

    @Size(max = 25)
    @Column(name = "last_name", length = 25)
    private String lastName;

    @Column(name = "dob")
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    @Size(max = 25)
    @Column(name = "mobile", length = 25)
    private String mobile;

    @Size(max = 25)
    @Column(name = "telephone", length = 25)
    private String telephone;

    @Email
    @Size(max = 50)
    @Column(length = 50, nullable = false, updatable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "user_type")
    private Role userType;

    @Size(max = 25)
    @Column(name = "nid", length = 25)
    private String nid;

    @Size(max = 25)
    @Column(name = "passport", length = 25)
    private String passport;

    @Size(max = 60)
    @Column(length = 60)
    @JsonIgnore
    private String password;

    @Column(name = "address")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "post_code", length = 20)
    private String postCode;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "activated")
    private boolean activated;

    @Column(name = "locked")
    private boolean locked;

    @Column(name = "credentials_expired")
    private boolean credentialsNonExpired;

    @Column(name = "account_expired")
    private boolean accountNonExpired;

}
