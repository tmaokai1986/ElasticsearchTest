package xyz.lockon.routing;

public class RoutingPolicyFactory {
    public static RoutingPolicy createRoutingPolicy() {
        return new CreateTimeRoutingPolicy();
    }
}
