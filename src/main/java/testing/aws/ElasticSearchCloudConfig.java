package testing.aws;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.internal.AWS4SignerUtils;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Configuration
@RequiredArgsConstructor
public class ElasticSearchCloudConfig extends AbstractElasticsearchConfiguration {

    private static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

    @Bean(destroyMethod = "close")
    @Override
    public RestHighLevelClient elasticsearchClient() {

        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor("es", aws4Signer(),
                credentialsProvider);

        return new RestHighLevelClient(
                RestClient.builder(
                        HttpHost.create(System.getenv("ES_ENDPOINT"))).
                        setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
    }

    private AWS4Signer aws4Signer() {
        final AWS4Signer signer = new AWS4Signer();
        signer.setServiceName("es");
        signer.setRegionName("eu-west-1");
        return signer;
    }

}

