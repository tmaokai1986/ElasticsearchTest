package xyz.lockon;

import org.apache.lucene.util.IOUtils;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import java.io.IOException;
import java.util.Map;


public class CreateIndexMain {
    public static void main(String[] args) throws IOException {
        Document doc = Document.parse("{\n" + "  \"settings\": {\n" + "    \"number_of_shards\": 8,\n"
            + "    \"number_of_routing_shards\": 1024,\n" + "    \"number_of_replicas\": 0\n" + "  },\n"
            + "  \"mappings\": {\n" + "    \"properties\": {\n" + "      \"orderId\": {\n"
            + "        \"type\": \"text\"\n" + "      },\n" + "      \"createTime\": {\n"
            + "        \"type\": \"date\",\n" + "        \"format\": \"yyyy-MM-dd HH:mm:ss.SSS||epoch_millis\"\n"
            + "      },\n" + "      \"payTime\": {\n" + "        \"type\": \"date\",\n"
            + "        \"format\": \"yyyy-MM-dd HH:mm:ss.SSS||epoch_millis\"\n" + "      },\n"
            + "      \"customerId\": {\n" + "        \"type\": \"keyword\"\n" + "      },\n"
            + "      \"customerName\": {\n" + "        \"type\": \"keyword\"\n" + "      },\n"
            + "      \"orderType\": {\n" + "        \"type\": \"keyword\"\n" + "      },\n"
            + "      \"sourceType\": {\n" + "        \"type\": \"keyword\"\n" + "      },\n" + "      \"status\": {\n"
            + "        \"type\": \"keyword\"\n" + "      },\n" + "      \"contractId\": {\n"
            + "        \"type\": \"keyword\"\n" + "      },\n" + "      \"salesManId\": {\n"
            + "        \"type\": \"keyword\"\n" + "      },\n" + "      \"lastUpdateTime\": {\n"
            + "        \"type\": \"date\",\n" + "        \"format\": \"yyyy-MM-dd HH:mm:ss.SSS||epoch_millis\"\n"
            + "      },\n" + "      \"orderLineItems\": {\n" + "        \"type\": \"nested\",\n"
            + "        \"properties\": {\n" + "          \"orderLineItemId\": {\n"
            + "            \"type\": \"keyword\"\n" + "          },\n" + "          \"dataCenterId\": {\n"
            + "            \"type\": \"keyword\"\n" + "          },\n" + "          \"dataCenterName\": {\n"
            + "            \"type\": \"keyword\"\n" + "          },\n" + "          \"productId\": {\n"
            + "            \"type\": \"keyword\"\n" + "          },\n" + "          \"productName\": {\n"
            + "            \"type\": \"object\",\n" + "            \"properties\": {\n" + "              \"zhCN\": {\n"
            + "                \"type\": \"text\"\n" + "              },\n" + "              \"enUS\": {\n"
            + "                \"type\": \"text\"\n" + "              },\n" + "              \"localLang\": {\n"
            + "                \"type\": \"text\"\n" + "              }\n" + "            }\n" + "          },\n"
            + "          \"cloudTypeCode\": {\n" + "            \"type\": \"keyword\"\n" + "          },\n"
            + "          \"cloudTypeName\": {\n" + "            \"type\": \"object\",\n"
            + "            \"properties\": {\n" + "              \"zhCN\": {\n" + "                \"type\": \"text\"\n"
            + "              },\n" + "              \"enUS\": {\n" + "                \"type\": \"text\"\n"
            + "              },\n" + "              \"localLang\": {\n" + "                \"type\": \"text\"\n"
            + "              }\n" + "            }\n" + "          }\n" + "        }\n" + "      }\n" + "    }\n"
            + "  }\n" + "}");
        IndexCoordinates.of("");
        System.out.println(doc);

        ClientConfiguration clientConfiguration =
            ClientConfiguration.builder().connectedTo("192.168.3.101:9200").build();
        RestClients.ElasticsearchRestClient restClient = RestClients.create(clientConfiguration);
        ElasticsearchRestTemplate restTemplate = new ElasticsearchRestTemplate(restClient.rest());
        RestHighLevelClient restHighLevelClient = restClient.rest();
        try {
            CreateIndexRequest request = new CreateIndexRequest("order-3");
            request.settings(doc.get("settings", Map.class));
            request.mapping(doc.get("mappings", Map.class));
            if (!restHighLevelClient.indices().exists(new GetIndexRequest("order-3"), RequestOptions.DEFAULT)) {
                restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            }
//            PutIndexTemplateRequest putIndexTemplateRequest = new PutIndexTemplateRequest("order2-3");
//            List<String> indexPatterns = new ArrayList<String>();
//            indexPatterns.add("order2*");
//            putIndexTemplateRequest.patterns(indexPatterns);
//            putIndexTemplateRequest.settings(doc.get("settings", Map.class));
//            putIndexTemplateRequest.mapping(doc.get("mappings", Map.class));
//            restHighLevelClient.indices().putTemplate(putIndexTemplateRequest, RequestOptions.DEFAULT);

        } catch (IOException e) {
            e.printStackTrace();
        }
        restHighLevelClient.close();
    }
}
