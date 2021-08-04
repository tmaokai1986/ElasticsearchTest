package xyz.lockon.routing;

import com.sun.org.apache.bcel.internal.generic.ATHROW;
import lombok.Builder;
import lombok.Data;
import xyz.lockon.entry.OrderItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateTimeRoutingPolicy implements RoutingPolicy {
    @Data
    @Builder
    static class Policy {
        String routing;
        long startTime;
        long endTime;
    }

    private List<Policy> policyList = new ArrayList<>();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    CreateTimeRoutingPolicy() {
        try {
            policyList.add(Policy.builder().routing("3")
                .endTime(simpleDateFormat.parse("2019-12-31 23:59:59").getTime() / 1000).build());
            policyList.add(
                Policy.builder().routing("4").startTime(simpleDateFormat.parse("2020-01-01 00:00:00").getTime() / 1000)
                    .endTime(simpleDateFormat.parse("2021-12-31 23:59:59").getTime() / 1000).build());
            policyList.add(
                    Policy.builder().routing("7").startTime(simpleDateFormat.parse("2022-01-01 00:00:00").getTime() / 1000)
                            .endTime(simpleDateFormat.parse("2023-12-31 23:59:59").getTime() / 1000).build());
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getRouting(OrderItem orderItem) {
        long time = orderItem.getCreateTime().getTime() / 1000;
        for (Policy policy : policyList) {
            if (time >= policy.startTime && time <= policy.endTime) {
                return policy.getRouting();
            }
        }
        throw new IllegalStateException("");
    }
}
