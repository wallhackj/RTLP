package org.wallhack.logprocessor.realtimelogprocessor.repository.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class LogRepository {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    private final String indexName = "realtime-logs";

    public String createOrUpdateDocument(LogDocument doc) throws IOException {

        IndexResponse response = elasticsearchClient.index(
                i -> i.index(indexName)
                        .id(doc.getId())
                        .document(doc)
        );
        if(response.result().name().equals("Created")){
            return "LogDocument document has been created successfully.";
        } else if(response.result().name().equals("Updated")){
            return "LogDocument document has been updated successfully.";
        }
        return "Error while performing the operation.";
    }

    public LogDocument getLogDocumentById(String id) throws IOException{
        LogDocument doc = null;
        GetResponse<LogDocument> response = elasticsearchClient.get(
                g -> g.index(indexName)
                        .id(id),
                LogDocument.class
        );

        if (response.found()) {
            doc = response.source();
            assert doc != null;
            System.out.println("LogDocument message is: " + doc.getMessage());
        } else {
            System.out.println ("LogDocument not found");
        }
        return doc;
    }

    public String deleteLogDocumentById(String id) throws IOException {

        DeleteRequest request = DeleteRequest.of(d -> d.index(indexName).id(id));

        DeleteResponse deleteResponse = elasticsearchClient.delete(request);
        if (Objects.nonNull(deleteResponse.result()) && !deleteResponse.result().name().equals("NotFound")) {
            return "LogDocument with id : " + deleteResponse.id() + " has been deleted successfully !.";
        }
        System.out.println("LogDocument not found");
        return "LogDocument with id : " + deleteResponse.id() + " does not exist.";
    }

    public List<LogDocument> getAllLogDocument() throws IOException {

        SearchRequest searchRequest = SearchRequest.of(s -> s.index(indexName));
        SearchResponse<LogDocument> searchResponse = elasticsearchClient.search(searchRequest, LogDocument.class);
        List<Hit<LogDocument>> hits = searchResponse.hits().hits();
        List<LogDocument> docs = new ArrayList<>();
        for(Hit<LogDocument> object : hits){
            docs.add(object.source());
        }
        return docs;
    }
}