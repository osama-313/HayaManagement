package com.haya;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haya.entity.Role;
import com.haya.entity.User;
import com.haya.repository.UserRepository;
import com.haya.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Boots the full application against an in-memory database. Each test runs in a transaction that is rolled back,
 * so tests do not leak data into each other. The administrator {@code root} is created once at startup.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTest {

    protected static final String ADMIN_USER = "root";
    protected static final String ADMIN_PASSWORD = "Root@12345";
    protected static final String MEMBER_PASSWORD = "Member@123";

    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected ObjectMapper json;
    @Autowired
    protected UserRepository users;
    @Autowired
    private RoleService roles;
    @Autowired
    private PasswordEncoder passwordEncoder;

    protected User createMember(String userName) {
        return users.save(new User("Test", userName, userName, userName + "@example.com",
                passwordEncoder.encode(MEMBER_PASSWORD), 30, "Tester", roles.require(Role.USER)));
    }

    protected String loginAs(String userName, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("userName", userName, "password", password))))
                .andReturn().getResponse().getContentAsString();
        JsonNode node = json.readTree(body);
        return node.path("token").asText();
    }

    protected String adminToken() throws Exception {
        return loginAs(ADMIN_USER, ADMIN_PASSWORD);
    }

    protected String memberToken(String userName) throws Exception {
        return loginAs(userName, MEMBER_PASSWORD);
    }

    protected MockHttpServletRequestBuilder withJson(MockHttpServletRequestBuilder request, String token, Object body)
            throws Exception {
        return request.header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(body));
    }

    protected Long idOf(String responseBody) throws Exception {
        return json.readTree(responseBody).path("id").asLong();
    }
}
