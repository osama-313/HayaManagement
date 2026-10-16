package com.haya;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserApiTest extends IntegrationTest {

    private Map<String, Object> newUser(String userName) {
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "Jamie");
        body.put("lastName", "Rivera");
        body.put("userName", userName);
        body.put("email", userName + "@example.com");
        body.put("password", "Sup3rSecret!");
        body.put("age", 28);
        body.put("proficiency", "QA Engineer");
        body.put("role", "USER");
        return body;
    }

    @Test
    void adminCreatesAUserWhoCanThenSignIn() throws Exception {
        mvc.perform(withJson(post("/api/users"), adminToken(), newUser("jamie")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());

        mvc.perform(withJson(get("/api/auth/me"), loginAs("jamie", "Sup3rSecret!"), Map.of()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("jamie"));
    }

    @Test
    void listingUsersNeverExposesPasswordHashes() throws Exception {
        createMember("lister");

        mvc.perform(get("/api/users").header("Authorization", "Bearer " + memberToken("lister")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].password").doesNotExist());
    }

    @Test
    void membersCannotCreateUsers() throws Exception {
        createMember("plain");

        mvc.perform(withJson(post("/api/users"), memberToken("plain"), newUser("sneaky")))
                .andExpect(status().isForbidden());
    }

    @Test
    void duplicateUsernameIsAConflict() throws Exception {
        String token = adminToken();
        mvc.perform(withJson(post("/api/users"), token, newUser("dupe"))).andExpect(status().isCreated());

        Map<String, Object> second = newUser("dupe");
        second.put("email", "other@example.com");
        mvc.perform(withJson(post("/api/users"), token, second)).andExpect(status().isConflict());
    }

    @Test
    void invalidInputIsReportedPerField() throws Exception {
        Map<String, Object> body = newUser("x");
        body.put("email", "not-an-email");
        body.put("password", "short");
        body.put("role", "SUPERUSER");

        mvc.perform(withJson(post("/api/users"), adminToken(), body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists())
                .andExpect(jsonPath("$.errors.role").exists())
                .andExpect(jsonPath("$.errors.userName").exists());
    }

    @Test
    void updateKeepsThePasswordWhenNoneIsSupplied() throws Exception {
        String token = adminToken();
        Long id = idOf(mvc.perform(withJson(post("/api/users"), token, newUser("keeper")))
                .andReturn().getResponse().getContentAsString());

        Map<String, Object> update = newUser("keeper");
        update.remove("password");
        update.put("proficiency", "Staff Engineer");
        mvc.perform(withJson(put("/api/users/" + id), token, update))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proficiency").value("Staff Engineer"));

        mvc.perform(withJson(get("/api/auth/me"), loginAs("keeper", "Sup3rSecret!"), Map.of()))
                .andExpect(status().isOk());
    }

    @Test
    void unknownUserIsNotFound() throws Exception {
        mvc.perform(get("/api/users/999999").header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void administratorsCannotDeleteThemselves() throws Exception {
        String token = adminToken();
        Long ownId = idOf(mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString());

        mvc.perform(delete("/api/users/" + ownId).header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict());
    }

    @Test
    void deletingAUserAlsoRemovesTheirTasksAndSessions() throws Exception {
        String admin = adminToken();
        Long memberId = createMember("leaver").getId();
        String memberToken = memberToken("leaver");

        mvc.perform(withJson(post("/api/tasks"), admin, Map.of("title", "Hand over", "assigneeId", memberId)))
                .andExpect(status().isCreated());

        mvc.perform(delete("/api/users/" + memberId).header("Authorization", "Bearer " + admin))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/tasks").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.title=='Hand over')]").isEmpty());
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isUnauthorized());
    }
}
