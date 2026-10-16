package com.haya;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommitmentAndResourceApiTest extends IntegrationTest {

    @Test
    void commitmentsAreAlwaysRecordedForTheSignedInMember() throws Exception {
        Long id = createMember("kai").getId();
        createMember("lena");

        mvc.perform(withJson(post("/api/commitments"), memberToken("kai"),
                        Map.of("fromWhen", "09:00", "toWhen", "13:00", "hours", 4, "note", "Focus time")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.id").value(id))
                .andExpect(jsonPath("$.hours").value(4));
    }

    @Test
    void commitmentTimesMustBeValid() throws Exception {
        createMember("max");

        mvc.perform(withJson(post("/api/commitments"), memberToken("max"),
                        Map.of("fromWhen", "25:00", "toWhen", "noon", "hours", 0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.fromWhen").exists())
                .andExpect(jsonPath("$.errors.toWhen").exists())
                .andExpect(jsonPath("$.errors.hours").exists());
    }

    @Test
    void membersCannotDeleteOthersCommitments() throws Exception {
        createMember("nina");
        createMember("owen");
        Long id = idOf(mvc.perform(withJson(post("/api/commitments"), memberToken("nina"),
                        Map.of("fromWhen", "09:00", "toWhen", "10:00", "hours", 1)))
                .andReturn().getResponse().getContentAsString());

        mvc.perform(delete("/api/commitments/" + id).header("Authorization", "Bearer " + memberToken("owen")))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/commitments/" + id).header("Authorization", "Bearer " + memberToken("nina")))
                .andExpect(status().isNoContent());
    }

    @Test
    void anyMemberCanShareAResourceButOnlyAdminsCanRemoveIt() throws Exception {
        createMember("pia");
        Long id = idOf(mvc.perform(withJson(post("/api/resources"), memberToken("pia"),
                        Map.of("url", "example.com/guide", "highlights", "Handy guide")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        mvc.perform(delete("/api/resources/" + id).header("Authorization", "Bearer " + memberToken("pia")))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/resources/" + id).header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isNoContent());
    }

    @Test
    void resourceUrlsWithScriptSchemesAreRejected() throws Exception {
        mvc.perform(withJson(post("/api/resources"), adminToken(),
                        Map.of("url", "javascript:alert(1)", "highlights", "x")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.url").exists());
    }

    @Test
    void dashboardAggregatesTeamWorkload() throws Exception {
        Long id = createMember("quinn").getId();
        String admin = adminToken();
        mvc.perform(withJson(post("/api/tasks"), admin,
                Map.of("title", "Open one", "dueDate", "2001-01-01", "assigneeId", id)));
        mvc.perform(withJson(post("/api/tasks"), admin,
                Map.of("title", "Done one", "completed", true, "assigneeId", id)));

        mvc.perform(get("/api/dashboard").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openTasks").value(1))
                .andExpect(jsonPath("$.completedTasks").value(1))
                .andExpect(jsonPath("$.overdueTasks").value(1))
                .andExpect(jsonPath("$.workload[?(@.member.id==" + id + ")].openTasks").value(1));
    }
}
