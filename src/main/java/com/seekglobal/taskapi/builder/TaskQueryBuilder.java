package com.seekglobal.taskapi.builder;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class TaskQueryBuilder {
    private final Query query;

    public TaskQueryBuilder() {
        this.query = new Query();
    }

    public TaskQueryBuilder withSearchTerm(String searchTerm) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            query.addCriteria(Criteria.where("title").regex(searchTerm, "i"));
        }
        return this;
    }

    public TaskQueryBuilder withStatus(String status) {
        if (status != null && !status.isEmpty()) {
            query.addCriteria(Criteria.where("status").is(status));
        }
        return this;
    }

    public Query build() {
        return query;
    }
}
