package com.app.cisco.ciscoservice.repository;


import com.app.cisco.ciscoservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findUserByUserName(String userName);
}
