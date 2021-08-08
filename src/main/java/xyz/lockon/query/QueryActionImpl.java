package xyz.lockon.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;

import xyz.lockon.entry.OrderItem;
import xyz.lockon.routing.Policy;
import xyz.lockon.routing.RoutingPolicy;
import xyz.lockon.routing.RoutingPolicyFactory;

/**
 * 查询实现类
 */
public class QueryActionImpl implements QueryAction<OrderItem> {
    private static final Logger logger = LoggerFactory.getLogger(QueryActionImpl.class);

    private RoutingPolicy routingPolicy = RoutingPolicyFactory.createRoutingPolicy();

    private RestClients.ElasticsearchRestClient restClient;

    public QueryActionImpl(RestClients.ElasticsearchRestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PageResultList<OrderItem> query(QueryCondition condition) {
        List<Policy> policyList = routingPolicy.getRouting(condition);
        List<Query> queryList = buildQuery(condition, policyList);
        List<Long> countList = count(queryList);
        List<OrderItem> resultData = new ArrayList<>(condition.getPageSize());
        long totalRecord = countList.stream().mapToLong(Long::longValue).sum();
        long startPos = (condition.getPageIndex() - 1) * condition.getPageSize();
        long total = 0;
        for (int i = 0; i < countList.size(); i++) {
            total += countList.get(i).longValue();
            if (total > startPos) {
                Query query = queryList.get(i);
                query.setPageable(PageRequest.of(Math.toIntExact((startPos - total + countList.get(i).longValue())),
                    Math.toIntExact(Math.min(condition.getPageSize(), total - startPos - resultData.size()))));
                resultData.addAll(search(query));
            }
            if (resultData.size() == condition.getPageSize()) {
                break;
            }
        }

        return PageResultList.<OrderItem>builder().totalRecordNum(totalRecord).currentPage(condition.getPageIndex())
            .dataList(resultData).build();
    }

    private List<OrderItem> search(Query query) {
        RestClients.ElasticsearchRestClient restClient = getElasticsearchRestClient();
        ElasticsearchRestTemplate restTemplate = new ElasticsearchRestTemplate(restClient.rest());
        IndexCoordinates indexCoordinates = IndexCoordinates.of("order-2");
        SearchHits<OrderItem> searchHits = restTemplate.search(query, OrderItem.class, indexCoordinates);
        return searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    private RestClients.ElasticsearchRestClient getElasticsearchRestClient() {
        return restClient;
    }

    private List<Long> count(List<Query> queryList) {
        List<Long> countList = new ArrayList<>(queryList.size());
        RestClients.ElasticsearchRestClient restClient = getElasticsearchRestClient();
        ElasticsearchRestTemplate restTemplate = new ElasticsearchRestTemplate(restClient.rest());
        IndexCoordinates indexCoordinates = IndexCoordinates.of("order-2");
        queryList.forEach(query -> {
            countList.add(restTemplate.count(query, OrderItem.class, indexCoordinates));

        });
        return countList;
    }

    private List<Query> buildQuery(QueryCondition condition, List<Policy> policyList) {
        List<Query> queryList = new ArrayList<>(policyList.size());
        for (Policy policy : policyList) {
            queryList.add(buildQuery(condition, policy));
        }
        return queryList;
    }

    private Query buildQuery(QueryCondition condition, Policy policy) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("createTime");
        Optional.ofNullable(condition.getStartCreateTime()).ifPresent(t -> {
            rangeQueryBuilder.from(condition.getStartCreateTime().getTime());
        });
        Optional.ofNullable(condition.getEndCreateTime()).ifPresent(t -> {
            rangeQueryBuilder.to(condition.getEndCreateTime().getTime());
        });
        queryBuilder.must(rangeQueryBuilder);
        nativeSearchQueryBuilder.withFilter(queryBuilder);
        nativeSearchQueryBuilder.withSort(new FieldSortBuilder("createTime").order(SortOrder.DESC));
        nativeSearchQueryBuilder.withRoute(policy.getRouting());
        return nativeSearchQueryBuilder.build();
    }

}
