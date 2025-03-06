package com.seekglobal.taskapi.builder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Query;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TaskBuilderTest {
    @Test
    void shouldBuildQueryWithSearchTerm() {
        Query query = new TaskQueryBuilder()
                .withSearchTerm("Task")
                .build();

        assertNotNull(query);
        assertTrue(query.getQueryObject().containsKey("title"));
        assertEquals(Pattern.class, query.getQueryObject().get("title").getClass());

        Pattern pattern = (Pattern) query.getQueryObject().get("title");
        assertEquals("Task", pattern.pattern());
        assertEquals("i", pattern.flags() == Pattern.CASE_INSENSITIVE ? "i" : "");
    }

    @Test
    void shouldBuildQueryWithStatus() {
        Query query = new TaskQueryBuilder()
                .withStatus("TODO")
                .build();

        assertNotNull(query);
        assertTrue(query.getQueryObject().containsKey("status"));
        assertEquals("TODO", query.getQueryObject().get("status"));
    }

    @Test
    void shouldBuildQueryWithBothParameters() {
        Query query = new TaskQueryBuilder()
                .withSearchTerm("Task")
                .withStatus("TODO")
                .build();

        assertNotNull(query);
        assertTrue(query.getQueryObject().containsKey("title"));
        assertTrue(query.getQueryObject().containsKey("status"));

        Pattern pattern = (Pattern) query.getQueryObject().get("title");
        assertEquals("Task", pattern.pattern());
        assertEquals("i", pattern.flags() == Pattern.CASE_INSENSITIVE ? "i" : "");
    }

    @Test
    void shouldBuildEmptyQueryWhenNoParameters() {
        Query query = new TaskQueryBuilder().build();

        assertNotNull(query);
        assertTrue(query.getQueryObject().isEmpty());
    }

}
