package com.app.cisco.ciscoservice.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Phone {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID phoneId;

    private String phoneName;

    private String phoneModel;

    private String phoneNumber;

    @ManyToOne
    private User userId;

}
