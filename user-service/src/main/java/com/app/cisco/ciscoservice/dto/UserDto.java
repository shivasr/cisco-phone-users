package com.app.cisco.ciscoservice.dto;

import com.app.cisco.ciscoservice.validations.ValidPassword;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude="password")
public class UserDto {
    /**
     * User ID of the user
     */
    private UUID userId;

    /**
     * Name of the user
     */
    @NotNull(message = "User name is required.")
    private String userName;

    /**
     * password of the user
     */
    @ValidPassword
    private String password;

    /**
     * Email address of the user
     */
    @NotNull
    @Email
    private String emailAddress;

    /**
     * Preferred phone number of the user
     */
    private String preferredPhoneNumber;
}
