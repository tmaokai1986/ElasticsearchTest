package xyz.lockon;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;

import xyz.lockon.entry.OrderItem;

public class QueryMain {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws ParseException, IOException {
        ClientConfiguration clientConfiguration =
            ClientConfiguration.builder().connectedTo("192.168.3.101:9200").build();
        RestClients.ElasticsearchRestClient restClient = RestClients.create(clientConfiguration);
        ElasticsearchRestTemplate restTemplate = new ElasticsearchRestTemplate(restClient.rest());
        IndexCoordinates indexCoordinates = IndexCoordinates.of("order-2");
        SearchHits<OrderItem> resultList =
            restTemplate.search(buildTimeQuery(simpleDateFormat.parse("2012-01-01 00:00:00"), null, 10000, 100),
                OrderItem.class, indexCoordinates);
        // long count = restTemplate.count(buildTimeQuery(simpleDateFormat.parse("2012-01-01 00:00:00"), null),
        // OrderItem.class, indexCoordinates);
        // System.out.println(String.format("data count %d", count));
        restClient.close();
    }

    public static Query buildQuery(String name) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.nestedQuery("orderLineItems",
            QueryBuilders.matchQuery("orderLineItems.cloudTypeName.zhCN", name), ScoreMode.None));
        nativeSearchQueryBuilder.withFilter(queryBuilder);
        return nativeSearchQueryBuilder.build();
    }

    public static Query buildTimeQuery(Date startTime, Date endTime, int page, int size) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("createTime");
        Optional.ofNullable(startTime).ifPresent(t -> rangeQueryBuilder.from(startTime.getTime()));
        Optional.ofNullable(endTime).ifPresent(t -> rangeQueryBuilder.to(endTime.getTime()));
        queryBuilder.must(rangeQueryBuilder);
        nativeSearchQueryBuilder.withFilter(queryBuilder);
        nativeSearchQueryBuilder.withSort(new FieldSortBuilder("createTime").order(SortOrder.DESC));
        nativeSearchQueryBuilder.withRoute("0,1,2");
        Pageable pageable = PageRequest.of(page, size);
        nativeSearchQueryBuilder.withPageable(pageable);
        return nativeSearchQueryBuilder.build();
    }
}
