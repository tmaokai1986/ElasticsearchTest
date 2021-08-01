package xyz.lockon;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Parameter {
    private int maxCustomerNum;
    private int maxTotalOrderNum;
    private int maxPerCustomerOrderNum;
    private long startTime;
    private long endTime;
    private CustomerIdGenerator customerIdGenerator;
    private RandomLongGenerator createTimeGenerator;
}
