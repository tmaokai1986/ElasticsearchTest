package xyz.lockon;

import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.Builder;
import lombok.Data;
import xyz.lockon.entry.I18NItem;
import xyz.lockon.entry.OrderItem;
import xyz.lockon.entry.OrderLineItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class OrderCreater {
    private String customerId;
    private int orderNum;
    private int orderNumPerCustomer;
    private CustomerIdGenerator customerIdGenerator;
    private RandomLongGenerator createTimeGenerator;

    public OrderItem newOrder() {
        if (orderNum > orderNumPerCustomer || customerId == null) {
            orderNum = 0;
            customerId = getCustomerID();
        }
        orderNum++;
        return doCreateOrder();
    }

    private OrderItem doCreateOrder() {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(UUID.randomUUID().toString());
        orderItem.setCreateTime(new Date(createTimeGenerator.nextNumber()));
        orderItem.setPayTime(orderItem.getCreateTime());
        orderItem.setOrderType(1);
        orderItem.setContractId(UUID.randomUUID().toString());
        orderItem.setSalesManId(UUID.randomUUID().toString());
        orderItem.setCustomerId(customerId);
        orderItem.setCustomerName(customerId);
        orderItem.setStatus(1);
        orderItem.setLastUpdateTime(orderItem.getCreateTime());
        orderItem.setOrderLineItems(doCreateOrderLine(orderItem));
        return orderItem;
    }

    private List<OrderLineItem> doCreateOrderLine(OrderItem orderItem) {
        List<OrderLineItem> orderLineItemList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            OrderLineItem orderLineItem = new OrderLineItem();
            orderLineItem.setDataCenterId(UUID.randomUUID().toString());
            orderLineItem.setDataCenterName(orderLineItem.getDataCenterId());
            orderLineItem.setOrderLineItemId(orderItem.getOrderId() + "-" + i);
            orderLineItem.setProductId(UUID.randomUUID().toString());
            orderLineItem.setProductName(I18NItem.builder().zhCN(orderLineItem.getProductId())
                .enUS(orderLineItem.getProductId()).localLang(orderLineItem.getProductId()).build());
            orderLineItem.setCloudTypeCode(UUID.randomUUID().toString());
            orderLineItem.setCloudTypeName(I18NItem.builder().zhCN(orderLineItem.getCloudTypeCode())
                .enUS(orderLineItem.getCloudTypeCode()).localLang(orderLineItem.getCloudTypeCode()).build());
            orderLineItemList.add(orderLineItem);
        }
        return orderLineItemList;
    }

    private String getCustomerID() {
        return customerIdGenerator.nextCustomerId();
    }

    private long getCreateTime() {
        return createTimeGenerator.nextNumber();
    }

}
