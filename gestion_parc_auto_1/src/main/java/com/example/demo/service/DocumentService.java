package com.example.demo.service;

import com.example.demo.model.Document;
import java.util.List;

public interface DocumentService {
    Document saveDocument(Document document);
    Document updateDocument(Long id, Document document);
    void deleteDocument(Long id);
    Document getDocumentById(Long id);
    List<Document> getAllDocuments();
}
