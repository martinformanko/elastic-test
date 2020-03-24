package testing.aws;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

@SpringBootApplication
public class Main {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @PostConstruct
    public void testing() {
        IndexQuery ix = new IndexQuery();
        ix.setIndexName("testing");
        ix.setId("haha");
        ix.setObject(new TestingDocument());
// THIS WORKS
//        elasticsearchOperations.index(ix);

// THIS DOES NOT WORK
        elasticsearchOperations.refresh("testing");

    }


}
