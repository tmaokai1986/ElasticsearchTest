package xyz.lockon.query;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryCondition {
    private int pageIndex;
    private int pageSize;
    private Date startCreateTime;
    private Date endCreateTime;
}
