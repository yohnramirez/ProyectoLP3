package com.backend.project.controller;

import com.backend.project.dto.GenderDto;
import com.backend.project.logic.GenderLogic;
import com.backend.project.logic.TypeDocumentLogic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genders")
public class GenderController {

    /**
     * Instance of the logic layer
     */
    private final GenderLogic genderLogic;

    /**
     * Constructor
     * @param genderLogic instance of the logic layer
     */
    public GenderController(GenderLogic genderLogic) {
        this.genderLogic = genderLogic;
    }

    /**
     * Create report
     * @param data data info
     * @return GenderDto
     */
    @PostMapping("/create")
    public ResponseEntity<GenderDto> create(@RequestBody GenderDto data) {
        GenderDto createTypeDocument = this.genderLogic.createGender(data);
        return ResponseEntity.ok(createTypeDocument);
    }

    /**
     * Get all
     * @return GenderDto list
     */
    @GetMapping("/")
    public ResponseEntity<List<GenderDto>> getAll() {
        List<GenderDto> getAll = this.genderLogic.getAll();

        if (getAll.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        return ResponseEntity.ok(getAll);
    }

    /**
     * Get types document by id
     * @param id type document id
     * @return GenderDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<GenderDto> getOne(@PathVariable Long id) {
        GenderDto getType = this.genderLogic.getGenderById(id);

        if (getType == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        return ResponseEntity.ok(getType);
    }

    /**
     * update type document
     * @param id type document id
     * @param dataUpdate data to update
     * @return GenderDto
     */
    @PostMapping("/{id}")
    public ResponseEntity<GenderDto> update(@PathVariable Long id, @RequestBody GenderDto dataUpdate) {
        GenderDto typeDocument = this.genderLogic.updateGender(id, dataUpdate);

        if (typeDocument == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        return ResponseEntity.ok(typeDocument);
    }
}
