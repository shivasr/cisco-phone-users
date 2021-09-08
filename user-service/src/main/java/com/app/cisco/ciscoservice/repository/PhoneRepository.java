package com.app.cisco.ciscoservice.repository;


import com.app.cisco.ciscoservice.model.Phone;
import com.app.cisco.ciscoservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, UUID> {

    List<Phone> findPhonesByUserId(User user);

    Optional<Phone> findPhoneByUserIdAndPhoneId(User User, UUID phoneId);

    Optional<Phone> findPhoneByUserIdAndPhoneNumber(User user, String phoneNumber);
}
