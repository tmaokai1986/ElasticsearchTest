package xyz.lockon.entry;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
