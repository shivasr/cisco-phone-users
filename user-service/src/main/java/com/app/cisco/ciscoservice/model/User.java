package com.app.cisco.ciscoservice.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude="password")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID userId;

    private String userName;

    private String password;

    private String emailAddress;

    private String preferredPhoneNumber;

}
