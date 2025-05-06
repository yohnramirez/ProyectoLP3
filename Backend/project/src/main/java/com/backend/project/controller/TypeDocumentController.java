package com.backend.project.controller;

import com.backend.project.dto.TypeDocumentDto;
import com.backend.project.logic.TypeDocumentLogic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/type-documents")
public class TypeDocumentController {

    /**
     * Instance of the logic layer
     */
    private final TypeDocumentLogic typeDocumentLogic;

    /**
     * Constructor
     * @param typeDocumentLogic instance of the logic layer
     */
    public TypeDocumentController(TypeDocumentLogic typeDocumentLogic) {
        this.typeDocumentLogic = typeDocumentLogic;
    }

    /**
     * Create report
     * @param data data info
     * @return ReportDto
     */
    @PostMapping("/create")
    public ResponseEntity<TypeDocumentDto> create(@RequestBody TypeDocumentDto data) {
        TypeDocumentDto createTypeDocument = this.typeDocumentLogic.createTypeDocument(data);
        return ResponseEntity.ok(createTypeDocument);
    }

    /**
     * Get all
     * @return TypeDocumentDto list
     */
    @GetMapping("/")
    public ResponseEntity<List<TypeDocumentDto>> getAll() {
        List<TypeDocumentDto> getAll = this.typeDocumentLogic.getAll();

        if (getAll.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        return ResponseEntity.ok(getAll);
    }

    /**
     * Get types document by id
     * @param id type document id
     * @return TypeDocumentDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<TypeDocumentDto> getOne(@PathVariable Long id) {
        TypeDocumentDto getType = this.typeDocumentLogic.getTypeDocumentById(id);

        if (getType == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        return ResponseEntity.ok(getType);
    }

    /**
     * update type document
     * @param id type document id
     * @param dataUpdate data to update
     * @return TypeDocumentDto
     */
    @PostMapping("/{id}")
    public ResponseEntity<TypeDocumentDto> update(@PathVariable Long id, @RequestBody TypeDocumentDto dataUpdate) {
        TypeDocumentDto typeDocument = this.typeDocumentLogic.updateTypeDocument(id, dataUpdate);

        if (typeDocument == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        return ResponseEntity.ok(typeDocument);
    }
}
