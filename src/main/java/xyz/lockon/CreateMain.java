package xyz.lockon;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import xyz.lockon.entry.OrderItem;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateMain {

    public static void main(String[] args) throws IOException {
        String indexName = "order-2";
        ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo("127.0.0.1:9200").build();
        RestClients.ElasticsearchRestClient restClient = RestClients.create(clientConfiguration);
        ElasticsearchRestTemplate restTemplate =
            new ElasticsearchRestTemplate(restClient.rest());
        IndexCoordinates indexCoordinates = IndexCoordinates.of(indexName);
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(2019, 1, 1);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(2022, 1, 1);
        int orderNumPerCustomer = 10000;
        int customerNum = 100;
        int customerIdStartNum = 1000;
        OrderCreater orderCreater = OrderCreater.builder().orderNumPerCustomer(orderNumPerCustomer)
            .customerIdGenerator(new CustomerIdGenerator(customerIdStartNum, customerNum)).createTimeGenerator(
                new RandomLongGenerator(startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis()))
            .build();
        long allStartTime = System.currentTimeMillis();
        int batchCount = customerNum * orderNumPerCustomer / 100;
        for (int i = 0; i < batchCount; i++) {
            long startTime = System.currentTimeMillis();
            List<OrderItem> orderItemList = new ArrayList<>(100);
            for (int batchIndex = 0; batchIndex < 100; batchIndex++) {
                orderItemList.add(orderCreater.newOrder());
            }
            bulkIndex(orderItemList, restTemplate, indexCoordinates);
            System.out.println(
                String.format("Batch num %d/%d, Time cost:%d", i, batchCount, System.currentTimeMillis() - startTime));
        }
        long allTimeCost = System.currentTimeMillis() - allStartTime;
        System.out.println(String.format("All time cost:%d, %d record per second.", allTimeCost, customerNum * orderNumPerCustomer / (allTimeCost / 1000)));
        restClient.close();
    }

    public static void bulkIndex(List<OrderItem> orderItemList, ElasticsearchRestTemplate restTemplate,
        IndexCoordinates indexCoordinates) {
        List<IndexQuery> indexQueryList = new ArrayList<>(orderItemList.size());
        for (OrderItem orderItem : orderItemList) {
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setId(orderItem.getOrderId());
            indexQuery.setObject(orderItem);
            indexQueryList.add(indexQuery);
        }
        while (true) {
            try {
                restTemplate.bulkIndex(indexQueryList, indexCoordinates);
                break;
            } catch (DataAccessResourceFailureException e) {
                e.printStackTrace();
            }
        }
    }
}
