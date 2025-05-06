package com.backend.project.controller;

import com.backend.project.dto.CreateRequestReportDto;
import com.backend.project.dto.ReportDto;
import com.backend.project.dto.ReportStatusHistoryDto;
import com.backend.project.logic.ReportLogic;
import com.backend.project.util.enums.ReportStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    /**
     * Instance of the logic layer
     */
    private final ReportLogic reportLogic;

    /**
     * Constructor
     * @param reportLogic instance of the logic layer
     */
    public ReportController(ReportLogic reportLogic) {
        this.reportLogic = reportLogic;
    }

    /**
     * Create report
     * @param data data info
     * @return ReportDto
     */
    @PostMapping("/create")
    public ResponseEntity<ReportDto> create(@RequestBody CreateRequestReportDto data) {
        ReportDto createReport = this.reportLogic.createReport(data);
        return ResponseEntity.ok(createReport);
    }

    /**
     * Get near reports
     *  @param lat Get latitude
     *  @param lng Get longitude
     *  @param rad Get radius
     * @return List of reports
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<ReportDto>> getNearbyReports(@RequestParam double lat, @RequestParam double lng, @RequestParam double rad) {
        List<ReportDto> reports = this.reportLogic.getNearbyReports(lat, lng, rad);
        return ResponseEntity.ok(reports);
    }

    /**
     * Get reports by user id
     * @param id user id
     * @return ReportDto list
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<List<ReportDto>> getReportsByUserId(@PathVariable Long id) {
        List<ReportDto> reportsByUser = this.reportLogic.getReportsByUser(id);
        return ResponseEntity.ok(reportsByUser);
    }

    /**
     * Get own reports
     * @param request request
     * @return ReportDto
     */
    @GetMapping("/my-reports")
    public ResponseEntity<List<ReportDto>> getMyReports(HttpServletRequest request) {
        long userId = Long.parseLong(request.getAttribute("userId").toString());
        List<ReportDto> reportsByUser = this.reportLogic.getReportsByUser(userId);
        return ResponseEntity.ok(reportsByUser);
    }

    /**
     * Update report status
     * @param reportId Id del reporte.
     * @param newStatus Nuevo estado del reporte.
     * @return ReportDto con los detalles del reporte actualizado.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ReportDto> updateReportStatus(@PathVariable("id") Long reportId,
                                                     @RequestParam("status") String newStatus) {
        try {
            ReportStatus status = ReportStatus.fromString(newStatus);

            ReportDto updatedReport = this.reportLogic.updateStatus(reportId, status);
            return ResponseEntity.ok(updatedReport);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Get reports by state
     * @param status Status to filter.
     * @return ReportDto list
     */
    @GetMapping("/status")
    public ResponseEntity<List<ReportDto>> getReportsByStatus(@RequestParam("status") String status) {
        try {

            ReportStatus reportStatus = ReportStatus.fromString(status);
            List<ReportDto> reports = this.reportLogic.getReportsByStatus(reportStatus);

            return ResponseEntity.ok(reports);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(Collections.emptyList());
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    /**
     * Get reports history by id
     * @param reportId Status to filter.
     * @return ReportStatusHistoryDto list
     */
    @GetMapping("/{id}/status-history")
    public ResponseEntity<List<ReportStatusHistoryDto>> getReportStatusHistory(@PathVariable("id") Long reportId) {

        List<ReportStatusHistoryDto> historyList = this.reportLogic.getStatusHistory(reportId);

        return ResponseEntity.ok(historyList);
    }
}
