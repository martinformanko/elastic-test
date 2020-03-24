package testing.aws;

import com.amazonaws.auth.AWS4Signer;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainWithoutSpring {

    private static String serviceName = "es";
    private static String region = "eu-west-1";
    private static String aesEndpoint = System.getenv("ES_ENDPOINT"); // e.g. https://search-mydomain.us-west-1.es.amazonaws.com
    private static String index = "my-index";
    private static String type = "_doc";
    private static String id = "1";
    static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

    public static void main(String[] args) throws IOException {
        RestHighLevelClient esClient = esClient(serviceName, region);
        // Create the document as a hash map
        Map<String, Object> document = new HashMap<>();
        document.put("title", "Walk the Line");
        document.put("director", "James Mangold");
        document.put("year", "2005");

//        THIS WORKS
//        IndexRequest request = new IndexRequest(index, type, id).source(document);
//        IndexResponse response = esClient.index(request, RequestOptions.DEFAULT);

        // THIS DOES NOT WORK
        RefreshRequest request = new RefreshRequest("index1");
        RefreshResponse refreshResponse = esClient.indices().refresh(request, RequestOptions.DEFAULT);
        System.out.println(refreshResponse.toString());
    }

    // Adds the interceptor to the ES REST client
    public static RestHighLevelClient esClient(String serviceName, String region) {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(serviceName);
        signer.setRegionName(region);
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);
        return new RestHighLevelClient(
                RestClient.builder(HttpHost.create(aesEndpoint)).setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
    }
}