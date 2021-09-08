package com.app.cisco.ciscoservice.dto;

import com.app.cisco.ciscoservice.model.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneDto {

    private UUID phoneId;

    private String phoneName;

    private String phoneModel;

    private String phoneNumber;
}
