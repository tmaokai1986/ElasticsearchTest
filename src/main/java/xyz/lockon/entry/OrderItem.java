package xyz.lockon.entry;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderItem {
    private String orderId;
    private Date createTime;
    private String customerId;
    private String customerName;
    private int orderType;
    private Date payTime;
    private int sourceType;
    private int status;
    private String contractId;
    private String salesManId;
    private Date lastUpdateTime;
    private List<OrderLineItem> orderLineItems;
}
