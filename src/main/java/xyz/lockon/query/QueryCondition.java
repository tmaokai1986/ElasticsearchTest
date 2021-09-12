package xyz.lockon.query;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryCondition {
    private long offset;

    private int limit;

    private Date startCreateTime;

    private Date endCreateTime;
}
