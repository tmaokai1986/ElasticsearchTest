package xyz.lockon.query;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Ignore
public class QueryActionImplTest {

    @Test
    public void queryTest() throws ParseException, IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        ClientConfiguration clientConfiguration =
            ClientConfiguration.builder().connectedTo("192.168.3.101:9200").build();
        RestClients.ElasticsearchRestClient restClient = RestClients.create(clientConfiguration);
        QueryActionImpl queryAction = new QueryActionImpl(restClient);
        QueryCondition condition =
            QueryCondition.builder().startCreateTime(simpleDateFormat.parse("2012-01-01 00:00:00"))
                .endCreateTime(simpleDateFormat.parse("2021-12-31 23:59:59")).offset(10000888).limit(100).build();
        long start = System.currentTimeMillis();
        Assert.assertEquals(queryAction.query(condition).getDataList().size(), 100);
        System.out.println(String.format("time cost %d", (System.currentTimeMillis() - start)));
        restClient.close();
    }
}