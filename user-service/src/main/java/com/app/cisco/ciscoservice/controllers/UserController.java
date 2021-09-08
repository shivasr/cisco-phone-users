package com.app.cisco.ciscoservice.controllers;


import com.app.cisco.ciscoservice.dto.PhoneDto;
import com.app.cisco.ciscoservice.dto.UserDto;
import com.app.cisco.ciscoservice.exceptions.PhoneNotFoundException;
import com.app.cisco.ciscoservice.exceptions.UserNotFoundException;
import com.app.cisco.ciscoservice.model.Phone;
import com.app.cisco.ciscoservice.service.PhoneService;
import com.app.cisco.ciscoservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder;

    private final PhoneService phoneService;

    /**
     *  Constrictor
     * @param userService UsersService for fetching the user details
     * @param passwordEncoder encoder for encoding the password
     * @param phoneService phoneService to manage the phone details
     */
    public UserController(UserService userService, BCryptPasswordEncoder passwordEncoder, PhoneService phoneService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.phoneService = phoneService;
    }

    @GetMapping
    public @ResponseBody ResponseEntity<List<UserDto>> listUsers() {

        return ResponseEntity.ok(userService.retrieveAllUsers());
    }

    @PostMapping("/register")
    public @ResponseBody
    ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) throws URISyntaxException {
        LOG.debug(String.format("Got the request to register the user with  username %s and email ID: %s",
                userDto.getUserName(), userDto.getEmailAddress()));
        final String nonEncryptedPassword = userDto.getPassword();

        userDto.setPassword(passwordEncoder.encode(nonEncryptedPassword));
        UserDto savedUser = userService.saveUser(userDto);
        URI location = new URI("/api/v1/users/" + savedUser.getUserId());
        LOG.info(String.format("Successfully registered  username %s and email ID: %s",
                userDto.getUserName(), userDto.getEmailAddress()));
        return ResponseEntity.created(location).body(savedUser);
    }

    @GetMapping("/{userId}")
    @ResponseBody ResponseEntity<UserDto> getUserDetails(@PathVariable("userId") String userId) throws UserNotFoundException, PhoneNotFoundException {

        UserDto existingUser = userService.findUserByUserId(UUID.fromString(userId));

        return ResponseEntity.ok(existingUser);
    }

    @PatchMapping("/{userId}")
    @ResponseBody ResponseEntity<UserDto> patchUserDetails(@PathVariable("userId") String userId,
                                                           @RequestBody UserDto userDto) throws UserNotFoundException, PhoneNotFoundException {

        UserDto existingUser = userService.findUserByUserId(UUID.fromString(userId));


        if(Optional.ofNullable(userDto.getPreferredPhoneNumber()).isPresent() && !userDto.getPreferredPhoneNumber().isBlank()){
            phoneService.doesPhoneNumberBelongsToUser(UUID.fromString(userId), userDto.getPreferredPhoneNumber())
                    .orElseThrow(
                            () -> new PhoneNotFoundException(String.format("The phone number: %s does not belong to the user: %s",
                                    userDto.getPreferredPhoneNumber(), userId)));
        }

        existingUser.setPreferredPhoneNumber(userDto.getPreferredPhoneNumber());

        LOG.info(String.format("Successfully patched the  preferred phone number %s, for the user with username %s and email ID: %s",
                userDto.getPreferredPhoneNumber(), userDto.getUserName(), userDto.getEmailAddress()));
        return ResponseEntity.ok(existingUser);
    }


    @DeleteMapping("/{userId}")
    public @ResponseBody
    ResponseEntity<?> deleteUser(@PathVariable("userId") String userId) throws UserNotFoundException {

        userService.deleteUserById(userId);
        LOG.info(String.format("Successfully deleted the user with the user ID: %s", userId));
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{userId}/phoneNumbers")
    public @ResponseBody
    ResponseEntity<List<PhoneDto>> listAllPhones(@PathVariable("userId") String userId) {


        List<PhoneDto> savedPhones = phoneService.retrieveAllPhonesByUser(UUID.fromString(userId));

        return ResponseEntity.ok(savedPhones);
    }

    @PostMapping("/{userId}/phoneNumbers")
    public @ResponseBody
    ResponseEntity<PhoneDto> addPhone(@PathVariable("userId") String userId, @RequestBody PhoneDto phone) throws PhoneNotFoundException {


        PhoneDto savedPhoneDto = phoneService.savePhone(UUID.fromString(userId), phone);
        LOG.info(String.format("Successfully added phone details: %s, to the user with the user ID: %s",
                phone.toString(), userId));
        return ResponseEntity.ok(savedPhoneDto);
    }

    @DeleteMapping("/{userId}/phoneNumbers/{phoneId}")
    public @ResponseBody
    ResponseEntity<PhoneDto> deletePhone(@PathVariable("userId") String userId, @PathVariable("phoneId") String phoneId) throws PhoneNotFoundException {


        phoneService.deletePhoneById(UUID.fromString(userId), phoneId);
        LOG.info(String.format("Successfully delete phone id: %s, from the user with the user ID: %s",
                phoneId, userId));

        return ResponseEntity.ok().build();
    }


}
