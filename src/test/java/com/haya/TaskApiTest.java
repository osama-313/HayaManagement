package com.haya;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskApiTest extends IntegrationTest {

    private Map<String, Object> task(String title, Long assigneeId) {
        return Map.of("title", title, "description", "Details", "dueDate", "2030-01-31",
                "completed", false, "assigneeId", assigneeId);
    }

    @Test
    void memberCreatesATaskForThemselves() throws Exception {
        Long id = createMember("alice").getId();

        mvc.perform(withJson(post("/api/tasks"), memberToken("alice"), task("Write docs", id)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Write docs"))
                .andExpect(jsonPath("$.assignee.id").value(id))
                .andExpect(jsonPath("$.assignee.password").doesNotExist());
    }

    @Test
    void memberCannotAssignTasksToSomeoneElse() throws Exception {
        createMember("bob");
        Long carolId = createMember("carol").getId();

        mvc.perform(withJson(post("/api/tasks"), memberToken("bob"), task("Not mine", carolId)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAssignToAnyone() throws Exception {
        Long id = createMember("dave").getId();

        mvc.perform(withJson(post("/api/tasks"), adminToken(), task("Delegated", id)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assignee.id").value(id));
    }

    @Test
    void memberCannotEditOrDeleteSomeoneElsesTask() throws Exception {
        Long erinId = createMember("erin").getId();
        createMember("frank");
        Long taskId = idOf(mvc.perform(withJson(post("/api/tasks"), adminToken(), task("Erin's", erinId)))
                .andReturn().getResponse().getContentAsString());
        String frank = memberToken("frank");

        mvc.perform(withJson(put("/api/tasks/" + taskId), frank, task("Hijacked", erinId)))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/tasks/" + taskId).header("Authorization", "Bearer " + frank))
                .andExpect(status().isForbidden());
    }

    @Test
    void ownerCanCompleteTheirTask() throws Exception {
        Long id = createMember("gina").getId();
        String token = memberToken("gina");
        Long taskId = idOf(mvc.perform(withJson(post("/api/tasks"), token, task("Finish me", id)))
                .andReturn().getResponse().getContentAsString());

        mvc.perform(withJson(put("/api/tasks/" + taskId), token,
                        Map.of("title", "Finish me", "completed", true, "assigneeId", id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void tasksCanBeFilteredByAssignee() throws Exception {
        Long hankId = createMember("hank").getId();
        Long ivyId = createMember("ivy").getId();
        String admin = adminToken();
        mvc.perform(withJson(post("/api/tasks"), admin, task("Hank's", hankId))).andExpect(status().isCreated());
        mvc.perform(withJson(post("/api/tasks"), admin, task("Ivy's", ivyId))).andExpect(status().isCreated());

        mvc.perform(get("/api/tasks").param("assigneeId", hankId.toString()).header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Hank's"));
    }

    @Test
    void malformedDueDateIsRejected() throws Exception {
        Long id = createMember("jo").getId();

        mvc.perform(withJson(post("/api/tasks"), memberToken("jo"),
                        Map.of("title", "Bad date", "dueDate", "31/01/2030", "assigneeId", id)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.dueDate").exists());
    }

    @Test
    void missingTaskIsNotFound() throws Exception {
        mvc.perform(delete("/api/tasks/424242").header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isNotFound());
    }
}
