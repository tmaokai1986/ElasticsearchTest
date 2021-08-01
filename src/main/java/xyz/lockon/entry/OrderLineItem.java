package xyz.lockon.entry;

import lombok.Data;

@Data
public class OrderLineItem {
    private String orderLineItemId;
    private String dataCenterId;
    private String dataCenterName;
    private String productId;
    private I18NItem productName;
    private String cloudTypeCode;
    private I18NItem cloudTypeName;
}
