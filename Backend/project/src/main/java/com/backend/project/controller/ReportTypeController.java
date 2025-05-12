package com.backend.project.controller;

import com.backend.project.dto.ReportTypeDto;
import com.backend.project.dto.TypeDocumentDto;
import com.backend.project.logic.ReportTypeLogic;
import com.backend.project.logic.TypeDocumentLogic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report-types")
public class ReportTypeController {

    /**
     * Instance of the logic layer
     */
    private final ReportTypeLogic reportTypeLogic;

    /**
     * Constructor
     * @param reportTypeLogic instance of the logic layer
     */
    public ReportTypeController(ReportTypeLogic reportTypeLogic) {
        this.reportTypeLogic = reportTypeLogic;
    }

    /**
     * Create report type
     * @param data data info
     * @return ReportTypeDto
     */
    @PostMapping("/create")
    public ResponseEntity<ReportTypeDto> create(@RequestBody ReportTypeDto data) {
        ReportTypeDto createReportType = this.reportTypeLogic.createTypeReport(data);
        return ResponseEntity.ok(createReportType);
    }

    /**
     * Get all
     * @return ReportTypeDto list
     */
    @GetMapping("/")
    public ResponseEntity<List<ReportTypeDto>> getAll() {
        List<ReportTypeDto> getAll = this.reportTypeLogic.getAll();

        if (getAll.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        return ResponseEntity.ok(getAll);
    }

    /**
     * Get types report by id
     * @param id type report id
     * @return ReportTypeDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReportTypeDto> getOne(@PathVariable Long id) {
        ReportTypeDto getTypeReport = this.reportTypeLogic.getTypeReportById(id);

        if (getTypeReport == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        return ResponseEntity.ok(getTypeReport);
    }

    /**
     * update type report
     * @param id type report id
     * @param dataUpdate data to update
     * @return ReportTypeDto
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReportTypeDto> update(@PathVariable Long id, @RequestBody ReportTypeDto dataUpdate) {
        ReportTypeDto typeReport = this.reportTypeLogic.updateTypeReport(id, dataUpdate);

        if (typeReport == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        return ResponseEntity.ok(typeReport);
    }

    /**
     * delete type report
     * @param id type report id
     * @param dataUpdate data to update
     * @return ReportTypeDto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (id > 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid id");

        this.reportTypeLogic.deleteTypeReport(id);

        return ResponseEntity.ok("Type report with id " + id + "was deleted successfully");
    }
}
