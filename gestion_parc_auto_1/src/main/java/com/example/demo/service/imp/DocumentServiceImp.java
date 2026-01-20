package com.example.demo.service.imp;


import com.example.demo.Repository.DocumentRepository;
import com.example.demo.model.Document;
import com.example.demo.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentServiceImp implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public Document saveDocument(Document document) {
        return documentRepository.save(document);
    }

    @Override
    public Document updateDocument(Long id, Document document) {
        Optional<Document> optional = documentRepository.findById(id);
        if (optional.isPresent()) {
            Document existing = optional.get();
            existing.setType(document.getType());
            existing.setVehicule(document.getVehicule());
            return documentRepository.save(existing);
        } else {
            throw new RuntimeException("Document non trouvé avec id: " + id);
        }
    }

    @Override
    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }

    @Override
    public Document getDocumentById(Long id) {
        return documentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
}

