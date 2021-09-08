package com.app.cisco.ciscoservice.service;


import com.app.cisco.ciscoservice.dto.UserDto;
import com.app.cisco.ciscoservice.exceptions.UserNotFoundException;
import com.app.cisco.ciscoservice.model.User;
import com.app.cisco.ciscoservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User service to handle users.
 */
@Service
public class UserService implements UserDetailsService {

    final
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Method to list all existing users.
     *
     * @return a list of users
     */
    public List<UserDto> retrieveAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Method to find a user by user Id
     * @param userId
     * @return
     */
    public UserDto findUserByUserId(UUID userId) throws UserNotFoundException {
        User user  =  userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with user Id: %s not found.", userId)));
        return mapToUserDto(user);
    }

    /**
     * Method to save a user
     * @param userDto
     * @return
     */
    public UserDto saveUser(UserDto userDto) {
        User savedUser = userRepository.save(mapToUser(userDto));

        return mapToUserDto(savedUser);

    }

    public void
    deleteUserById(String userId) throws UserNotFoundException {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException(String.format("User with user Id: %s not found.", userId)));

        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUserName(username);
        Optional.ofNullable(user).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User with the username: %s not found.", username)));
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
                true, true, true, true, new ArrayList<>());
    }

    /**
     * Private method to map User entity to User DTO
     * @param user
     * @return
     */
    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .password(user.getPassword())
                .emailAddress(user.getEmailAddress())
                .preferredPhoneNumber(Optional.ofNullable(user.getPreferredPhoneNumber()).orElse(null))
                .build();
    }

    /**
     * Private method to map user DTO to user Entity
     * @param user
     * @return
     */
    private User mapToUser(UserDto user) {
        return User.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .password(user.getPassword())
                .emailAddress(user.getEmailAddress())
                .preferredPhoneNumber(Optional.ofNullable(user.getPreferredPhoneNumber()).orElse(null))
                .build();
    }


}
