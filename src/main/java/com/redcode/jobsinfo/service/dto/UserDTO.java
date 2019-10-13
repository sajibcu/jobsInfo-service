package com.redcode.jobsinfo.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.redcode.jobsinfo.common.enums.Gender;
import com.redcode.jobsinfo.database.entity.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {

    private Long id;

    @NotNull
    @Size(max = 25)
    private String firstName;

    @NotNull
    @Size(max = 25)
    private String lastName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private Gender gender;

    @Size(max = 20)
    private String mobile;

    @Size(max = 20)
    private String telephone;

    @Email
    @Size(min = 5, max = 50)
    private String email;

    private Long userType;


    @Size(max = 20)
    private String nid;

    private String password;

    private String address;

    @Size(max = 100)
    private String city;
    @Size(max = 100)
    private String state;
    @Size(max = 20)
    private String postCode;
    @Size(max = 100)
    private String country;

    private boolean activated;

    private boolean locked;

    private String recaptchaResponse;

    public UserDTO(User user) {
        this(user.getId(), user.getFirstName(), user.getLastName(), user.getDob(), user.getGender(),
                user.getMobile(), user.getTelephone(), user.getEmail(), (user.getUserType() != null) ? user.getUserType().getId() : 0, user.getNid(), null,
                user.getAddress(), user.getCity(), user.getState(), user.getPostCode(), user.getCountry(),
                user.isActivated(), user.isLocked(), "");
    }

}
