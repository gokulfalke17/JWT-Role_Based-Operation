package com.example.controller;

import com.example.entity.Product;
import com.example.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    // ✅ DO NOT AUTOWIRE IN FUNCTIONAL TESTS
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static String adminToken;
    private static String userToken;

    @Test
    @Order(1)
    void registerUsers() throws Exception {

        User admin = new User(null, "admin", "1234", "ADMIN");
        User user = new User(null, "user", "1234", "USER");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(admin)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }


    @Test
    @Order(2)
    void loginAndGetTokens() throws Exception {

        String adminLogin = """
            { "username":"admin", "password":"1234" }
        """;

        String userLogin = """
            { "username":"user", "password":"1234" }
        """;

        adminToken = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminLogin))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        userToken = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userLogin))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotNull(adminToken);
        assertNotNull(userToken);
    }


    @Test
    @Order(3)
    void adminCanAddProduct() throws Exception {

        Product product = new Product(null, "TV", 40000);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk());
    }


    @Test
    @Order(4)
    void userCanGetProducts() throws Exception {

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }


    @Test
    @Order(5)
    void userCannotDeleteProduct() throws Exception {

        mockMvc.perform(delete("/api/products/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }


    @Test
    @Order(6)
    void adminCanDeleteProduct() throws Exception {

        mockMvc.perform(delete("/api/products/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
