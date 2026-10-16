package com.haya;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationApiTest extends IntegrationTest {

    @Test
    void loginReturnsTokenAndProfileWithoutCredentials() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("userName", ADMIN_USER, "password", ADMIN_PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresAt").isNotEmpty())
                .andExpect(jsonPath("$.user.userName").value(ADMIN_USER))
                .andExpect(jsonPath("$.user.role").value("ADMIN"))
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("userName", ADMIN_USER, "password", "nope"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unknownUserGetsTheSameResponseAsAWrongPassword() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("userName", "ghost", "password", "whatever"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Invalid username or password"));
    }

    @Test
    void blankCredentialsFailValidation() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("userName", "", "password", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.userName").exists());
    }

    @Test
    void protectedEndpointsRequireAToken() throws Exception {
        mvc.perform(get("/api/tasks")).andExpect(status().isUnauthorized());
    }

    @Test
    void malformedTokenIsTreatedAsAnonymous() throws Exception {
        mvc.perform(get("/api/tasks").header("Authorization", "Bearer not.a.jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meReturnsTheSignedInUser() throws Exception {
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(ADMIN_USER));
    }

    @Test
    void logoutRevokesTheToken() throws Exception {
        String token = adminToken();

        mvc.perform(post("/api/auth/logout").header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void twoLoginsInTheSameSecondYieldIndependentTokens() throws Exception {
        String first = adminToken();
        String second = adminToken();

        mvc.perform(post("/api/auth/logout").header("Authorization", "Bearer " + first))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + second))
                .andExpect(status().isOk());
    }
}
