package xyz.lockon.routing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import xyz.lockon.entry.OrderItem;
import xyz.lockon.query.QueryCondition;

public class CreateTimeRoutingPolicy implements RoutingPolicy {

    private List<Policy> policyList = new ArrayList<>();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    CreateTimeRoutingPolicy() {
        try {
            policyList.add(Policy.builder().routing("1").endTime(getTime("2020-01-01 00:00:00")).build());
            policyList.add(Policy.builder().routing("3").startTime(getTime("2020-01-01 00:00:00"))
                .endTime(getTime("2021-01-01 00:00:00")).build());
            policyList.add(Policy.builder().routing("7").startTime(getTime("2021-01-01 00:00:00"))
                .endTime(getTime("2022-01-01 00:00:00")).build());
            policyList.add(Policy.builder().routing("5").startTime(getTime("2022-01-01 00:00:00"))
                .endTime(getTime("2023-01-01 00:00:00")).build());
            policyList.add(Policy.builder().routing("8").startTime(getTime("2023-01-01 00:00:00"))
                .endTime(getTime("2024-01-01 00:00:00")).build());
            policyList.add(Policy.builder().routing("0").startTime(getTime("2024-01-01 00:00:00"))
                .endTime(getTime("2025-01-01 00:00:00")).build());
            policyList.add(Policy.builder().routing("14").startTime(getTime("2025-01-01 00:00:00"))
                .endTime(getTime("2026-01-01 00:00:00")).build());
            policyList.add(Policy.builder().routing("4").startTime(getTime("2026-01-01 00:00:00"))
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
    public Policy getRouting(OrderItem orderItem) {
        long time = orderItem.getCreateTime().getTime() / 1000;
        for (Policy policy : policyList) {
            if (time >= policy.startTime && time < policy.endTime) {
                return policy;
            }
        }
        throw new IllegalStateException("");
    }

    @Override
    public List<Policy> getRouting(QueryCondition condition) {
        long start = 0;
        if (condition.getStartCreateTime() != null) {
            start = condition.getStartCreateTime().getTime() / 1000;
        }
        long end = Long.MAX_VALUE - 1;
        if (condition.getEndCreateTime() != null) {
            end = condition.getEndCreateTime().getTime() / 1000;
        }
        boolean isStarted = false;
        List<Policy> routingList = new ArrayList<>(policyList.size());
        for (Policy policy : policyList) {
            if (start >= policy.startTime && start < policy.endTime) {
                isStarted = true;
            }
            if (isStarted) {
                routingList.add(policy);
            }
            if (end >= policy.startTime && end < policy.endTime) {
                break;
            }
        }
        return routingList;
    }
}
