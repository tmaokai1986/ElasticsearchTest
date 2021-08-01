package xyz.lockon;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import xyz.lockon.entry.OrderItem;
import xyz.lockon.routing.RoutingPolicy;
import xyz.lockon.routing.RoutingPolicyFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;

public class CreateMain {
    private static AtomicLong totalTimecost = new AtomicLong(0);
    private static AtomicLong totalRecordCount = new AtomicLong(0);

    public static void main(String[] args) throws IOException, InterruptedException {
        int threadNum = 1;
        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            executorService.submit(() -> {
                doTest();
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println(String.format("All time cost:%d, total record %d, %d record per second.",
                totalTimecost.get(), totalRecordCount.get(), totalRecordCount.get() / (totalTimecost.get() / 1000)));
        System.exit(0);
    }

    public static void doTest() {
        String indexName = "order-2";
        ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo("127.0.0.1:9200").build();
        RestClients.ElasticsearchRestClient restClient = RestClients.create(clientConfiguration);
        ElasticsearchRestTemplate restTemplate = new ElasticsearchRestTemplate(restClient.rest());
        IndexCoordinates indexCoordinates = IndexCoordinates.of(indexName);
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(2012, 1, 1);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(2022, 12, 31);
        int orderNumPerCustomer = 1000;
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
            System.out.println(String.format("Thread %s, Batch num %d/%d, Time cost:%d",
                    Thread.currentThread().getName(), i, batchCount, System.currentTimeMillis() - startTime));
        }
        long allTimeCost = System.currentTimeMillis() - allStartTime;
        totalTimecost.addAndGet(allTimeCost);
        totalRecordCount.addAndGet(customerNum * orderNumPerCustomer);
        System.out.println(String.format("Thread %s, All time cost:%d, %d record per second.",
                Thread.currentThread().getName(), allTimeCost, customerNum * orderNumPerCustomer / (allTimeCost / 1000)));
        try {
            restClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void bulkIndex(List<OrderItem> orderItemList, ElasticsearchRestTemplate restTemplate,
                                 IndexCoordinates indexCoordinates) {
        RoutingPolicy routingPolicy = RoutingPolicyFactory.createRoutingPolicy();
        List<IndexQuery> indexQueryList = new ArrayList<>(orderItemList.size());
        for (OrderItem orderItem : orderItemList) {
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setId(orderItem.getOrderId());
            indexQuery.setObject(orderItem);
            indexQuery.setRouting(routingPolicy.getRouting(orderItem));
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
