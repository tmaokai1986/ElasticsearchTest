package xyz.lockon.routing;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Policy {
    String routing;
    long startTime;
    long endTime;
}
