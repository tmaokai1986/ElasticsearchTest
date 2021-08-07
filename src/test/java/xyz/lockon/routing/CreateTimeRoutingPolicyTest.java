package xyz.lockon.routing;

import org.junit.Assert;
import org.junit.Test;
import xyz.lockon.entry.OrderItem;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class CreateTimeRoutingPolicyTest {

    @Test
    public void getRouting() {
        CreateTimeRoutingPolicy routingPolicy = new CreateTimeRoutingPolicy();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2022,2,2);
        Assert.assertEquals(routingPolicy.getRouting(OrderItem.builder().createTime(calendar.getTime()).build()), "5");
        calendar.set(2021,2,2);
        Assert.assertEquals(routingPolicy.getRouting(OrderItem.builder().createTime(calendar.getTime()).build()), "7");
        calendar.set(2020,2,2);
        Assert.assertEquals(routingPolicy.getRouting(OrderItem.builder().createTime(calendar.getTime()).build()), "3");
        calendar.set(2019,2,2);
        Assert.assertEquals(routingPolicy.getRouting(OrderItem.builder().createTime(calendar.getTime()).build()), "1");
    }
}