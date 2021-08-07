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
import java.util.TimeZone;

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
            policyList.add(Policy.builder().routing("1")
                .endTime(getTime("2020-01-01 00:00:00")).build());
            policyList.add(
                Policy.builder().routing("3").startTime(getTime("2020-01-01 00:00:00"))
                    .endTime(getTime("2021-01-01 00:00:00")).build());
            policyList.add(
                    Policy.builder().routing("7").startTime(getTime("2021-01-01 00:00:00"))
                            .endTime(getTime("2022-01-01 00:00:00")).build());
            policyList.add(
                    Policy.builder().routing("5").startTime(getTime("2022-01-01 00:00:00"))
                            .endTime(getTime("2023-01-01 00:00:00")).build());
            policyList.add(
                    Policy.builder().routing("8").startTime(getTime("2023-01-01 00:00:00"))
                            .endTime(getTime("2024-01-01 00:00:00")).build());
            policyList.add(
                    Policy.builder().routing("0").startTime(getTime("2024-01-01 00:00:00"))
                            .endTime(getTime("2025-01-01 00:00:00")).build());
            policyList.add(
                    Policy.builder().routing("14").startTime(getTime("2025-01-01 00:00:00"))
                            .endTime(getTime("2026-01-01 00:00:00")).build());
            policyList.add(
                    Policy.builder().routing("4").startTime(getTime("2026-01-01 00:00:00"))
                            .endTime(Long.MAX_VALUE - 1).build());
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    private long getTime(String timeStr) throws ParseException {
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.parse(timeStr).getTime() / 1000;
    }

    @Override
    public String getRouting(OrderItem orderItem) {
        long time = orderItem.getCreateTime().getTime() / 1000;
        for (Policy policy : policyList) {
            if (time >= policy.startTime && time < policy.endTime) {
                return policy.getRouting();
            }
        }
        throw new IllegalStateException("");
    }
}
