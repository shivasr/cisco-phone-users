package com.app.cisco.ciscoservice.service;


import com.app.cisco.ciscoservice.dto.PhoneDto;
import com.app.cisco.ciscoservice.exceptions.PhoneNotFoundException;
import com.app.cisco.ciscoservice.model.Phone;
import com.app.cisco.ciscoservice.model.User;
import com.app.cisco.ciscoservice.repository.PhoneRepository;
import com.app.cisco.ciscoservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User service to handle users.
 */
@Service
public class PhoneService  {

    final
    PhoneRepository phoneRepository;

    final
    UserRepository userRepository;

    enum PhoneModelEnum {
        IPHONE, ANDROID, DESK_PHONE, SOFT_PHONE
    }

    public PhoneService(PhoneRepository phoneRepository, UserRepository userRepository) {
        this.phoneRepository = phoneRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieve Phones ny user ID
     * @param userId Phone user
     * @return List of Phones
     */
    public List<PhoneDto> retrieveAllPhonesByUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User with user Id %s not found.", userId)));
        return phoneRepository.findPhonesByUserId(user).stream()
                .map(this::mapToPhoneDto)
                .collect(Collectors.toList());
    }

    /**
     * Save phone for a user
     * @param uuid User id of the user
     * @param phoneDto phone information to save
     * @return Saved phone information
     */
    public PhoneDto savePhone(UUID uuid, PhoneDto phoneDto) throws PhoneNotFoundException {


        User user = userRepository.findById(uuid).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with user Id %s not found.", uuid)));
        Phone savedPhone = phoneRepository.save(mapToPhone(user, phoneDto));

        return mapToPhoneDto(savedPhone);

    }

    /**
     * Service method to delete a phone assigned to a user.
     *
     * @param uuid User ID to which the phone is assigned.
     * @param phoneId ID of the phone to be deleted
     * @throws PhoneNotFoundException when  phone with an ID is not available
     */
    public void
    deletePhoneById(UUID uuid, String phoneId) throws PhoneNotFoundException {
        User user = userRepository.findById(uuid).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with user Id %s not found.", uuid)));
        Phone phone = phoneRepository.findPhoneByUserIdAndPhoneId(user, UUID.fromString(phoneId))
                .orElseThrow(() -> new PhoneNotFoundException(String.format("Phone with phone Id: %s not found.", phoneId)));

        phoneRepository.delete(phone);
    }

    /**
     * Check if the phone number belongs to the user
     *
     * @param userId Id of the uyser
     * @return
     */
    public Optional<PhoneDto> doesPhoneNumberBelongsToUser(UUID userId, String phoneNumber) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with user Id %s not found.", userId)));

       Optional<Phone> phone = phoneRepository.findPhoneByUserIdAndPhoneNumber(user, phoneNumber) ;

       PhoneDto phoneDto = mapToPhoneDto(phone.orElse(null));
       return Optional.ofNullable(phoneDto);
    }
    /**
     * Mapper to convert Entity Phone to phone DTO
     *
     * @param phone phone entity containing values
     * @return Phone DTO
     */
    private PhoneDto mapToPhoneDto(Phone phone) {
        if(Optional.ofNullable(phone).isEmpty())
            return null;

        return PhoneDto.builder()
                .phoneId(phone.getPhoneId())
                .phoneName(phone.getPhoneName())
                .phoneNumber(phone.getPhoneNumber())
                .phoneModel(phone.getPhoneModel())
                .build();
    }

    private Phone mapToPhone(User user, PhoneDto phoneDto) throws PhoneNotFoundException {


        final String phoneModel = phoneDto.getPhoneModel();

        PhoneModelEnum phoneModelEnum;

        try {
            phoneModelEnum = PhoneModelEnum.valueOf(phoneModel.toUpperCase(Locale.US));
        } catch (IllegalArgumentException ex) {
            throw new PhoneNotFoundException(String.format("Unknown model input: %s", phoneModel));
        }

        Optional.ofNullable(phoneModelEnum).orElseThrow(
                () -> new PhoneNotFoundException(String.format("Unknown model input: %s", phoneModel)));

        return Phone.builder()
                .phoneId(phoneDto.getPhoneId())
                .phoneName(phoneDto.getPhoneName())
                .phoneNumber(phoneDto.getPhoneNumber())
                .phoneModel(String.valueOf(phoneModelEnum))
                .userId(user)
                .build();
    }


}
