package xyz.lockon.routing;


import xyz.lockon.entry.OrderItem;

/**
 * 分片路由处理
 */
public interface RoutingPolicy {
    String getRouting(OrderItem orderItem);
}
