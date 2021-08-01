package xyz.lockon;

import com.sun.xml.internal.ws.api.policy.SourceModel;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import xyz.lockon.entry.OrderItem;

public class QueryMain {

    public static void main(String[] args) {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo("127.0.0.1:9200").build();
        ElasticsearchRestTemplate restTemplate =
            new ElasticsearchRestTemplate(RestClients.create(clientConfiguration).rest());
        IndexCoordinates indexCoordinates = IndexCoordinates.of("order");
        SearchHits<OrderItem> resultList =
            restTemplate.search(buildQuery("dcef4edd1cd3"), OrderItem.class, indexCoordinates);
        long count = restTemplate.count(buildQuery("dcef4edd1cd3"), OrderItem.class, indexCoordinates);
        System.out.println(String.format("data count %d", count));
    }

    public static Query buildQuery(String name) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.nestedQuery("orderLineItems",
            QueryBuilders.matchQuery("orderLineItems.cloudTypeName.zhCN", name), ScoreMode.None));
        nativeSearchQueryBuilder.withFilter(queryBuilder);
        return nativeSearchQueryBuilder.build();
    }
}
