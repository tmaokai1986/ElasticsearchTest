package xyz.lockon.query;

import org.springframework.data.elasticsearch.core.query.Query;

public interface QueryAction<T> {
    PageResultList<T> query(QueryCondition condition);
}
