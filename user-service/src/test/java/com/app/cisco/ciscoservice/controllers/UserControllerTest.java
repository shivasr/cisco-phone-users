package com.app.cisco.ciscoservice.controllers;


import com.app.cisco.ciscoservice.dto.UserDto;
import com.app.cisco.ciscoservice.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnListOfUsersAnd200OK() throws Exception {
        this.mockMvc.perform(get("/users"))
                .andDo(print());
    }

    @Test
    public void shouldCreateUserAndReturnUpdateUserAnd201Created() throws Exception {
        Gson gson = new Gson();

        final String userName = "shivasr";
        final String password = "Welcome!23";
        final String emailAddress = "shivasr@gmail.com";
        UserDto userDto = createUserDto(userName, password, emailAddress);

        final String locationHeader = "Location";
        MvcResult result = this.mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists(locationHeader))
                .andExpect(jsonPath("$.userName").value(userName))
                .andExpect(jsonPath("$.emailAddress").value(emailAddress))
                .andReturn();

        UserDto userDtoSaved = new Gson().fromJson(result.getResponse().getContentAsString(), UserDto.class);




    }

    @Test
    public void shouldNotCreateInvalidUserAndReturn400BadRequest_SmallerPassword() throws Exception {
        Gson gson = new Gson();

        final String userName = "shivasr1";
        final String password = "sa";
        final String emailAddress = "shivasr@gmail.com";
        UserDto userDto = createUserDto(userName, password, emailAddress);

        this.mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldNotCreateInvalidUserAndReturn400BadRequest_NoUpperCase() throws Exception {
        Gson gson = new Gson();

        final String userName = "shivasr2";
        final String password = "welcome";
        final String emailAddress = "shivasr@gmail.com";
        UserDto userDto = createUserDto(userName, password, emailAddress);

        this.mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldNotCreateInvalidUserAndReturn400BadReques_NoDigitt() throws Exception {
        Gson gson = new Gson();

        final String userName = "shivasr3";
        final String password = "Welcome";
        final String emailAddress = "shivasr@gmail.com";
        UserDto userDto = createUserDto(userName, password, emailAddress);

        this.mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldDeleteUserAndReturn200OK() throws Exception {
        Gson gson = new Gson();
        final String userName = "shivasr4";
        final String password = "Welcome!23";
        final String emailAddress = "shivasr@gmail.com";
        UserDto userDto = createUserDto(userName, password, emailAddress);

        final String locationHeader = "Location";

        String authHeader = generateRequestHeader(userName, password);


        MvcResult result = this.mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists(locationHeader))
                .andExpect(jsonPath("$.userName").value(userName))
                .andExpect(jsonPath("$.emailAddress").value(emailAddress))
                .andReturn();

        UserDto userDtoSaved = new Gson().fromJson(result.getResponse().getContentAsString(), UserDto.class);

        this.mockMvc.perform(delete("/users/" + userDtoSaved.getUserId())
                        .header("Authorization", authHeader))
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/users/" + userDtoSaved.getUserId())
                        .header("Authorization", authHeader))
                .andExpect(status().isUnauthorized());



    }

    private String generateRequestHeader(String userName, String password) {
        String auth = userName + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        return authHeader;
    }

    private UserDto createUserDto(String userName, String password, String emailAddress) {
        return UserDto.builder()
                .userName(userName)
                .password(password)
                .emailAddress(emailAddress)
                .build();
    }
}