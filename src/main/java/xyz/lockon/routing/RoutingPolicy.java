package xyz.lockon.routing;


import java.util.List;

import xyz.lockon.entry.OrderItem;
import xyz.lockon.query.QueryCondition;

/**
 * 分片路由处理
 */
public interface RoutingPolicy {
    Policy getRouting(OrderItem orderItem);

    List<Policy> getRouting(QueryCondition condition);
}
