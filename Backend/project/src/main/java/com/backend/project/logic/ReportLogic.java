package com.backend.project.logic;

import com.backend.project.configuration.mapper.IMapper;
import com.backend.project.dto.CreateRequestReportDto;
import com.backend.project.dto.ReportBroadcastDto;
import com.backend.project.dto.ReportDto;
import com.backend.project.dto.ReportStatusHistoryDto;
import com.backend.project.model.Report;
import com.backend.project.model.ReportStatusHistory;
import com.backend.project.model.ReportType;
import com.backend.project.repository.IReportRepository;
import com.backend.project.repository.IReportStatusHistoryRepository;
import com.backend.project.repository.IReportTypeRepository;
import com.backend.project.util.enums.ReportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportLogic {

    /**
     * Instance of the repository layer
     */
    private final IReportRepository reportRepository;

    /**
     * Instance of the repository type layer
     */
    private final IReportTypeRepository reportTypeRepository;

    /**
     * Instance of the repository type layer
     */
    private final IReportStatusHistoryRepository reportStatusHistoryRepository;

    /**
     * Implementation of the user mapper
     */
    private final IMapper mapper;

    /**
     * Implementation web socket messaging
     */
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Create a report
     * @param dataReport data info
     * @return ReportDto
     */
    public ReportDto createReport(CreateRequestReportDto dataReport) {
        try {
            ReportType type = this.reportTypeRepository.findById(dataReport.getTypeId())
                    .orElseThrow(() -> new RuntimeException("Report type not found."));

            Report report = this.mapper.toEntity(dataReport);
            report.setType(type);
            report.setState(true);

            report = this.reportRepository.save(report);

            this.notifyCreationReport(report);

            return this.mapper.toDto(report);

        } catch (Exception ex) {
            System.out.println("[createReport]: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Get near reports
     * @param latitude Get latitude
     * @param longitude Get longitude
     * @param radius Get radius
     * @return List of reports
     */
    public List<ReportDto> getNearbyReports(double latitude, double longitude, double radius) {
        return this.reportRepository.findNearbyReports(latitude, longitude, radius)
                .stream().map(this.mapper::toDto).collect(Collectors.toList());
    }

    /**
     * Get reports by user id
     * @param idUser user id
     * @return List of reports
     */
    public List<ReportDto> getReportsByUser(long idUser) {
        return this.reportRepository.findByUserId(idUser)
                .stream().map(this.mapper::toDto).collect(Collectors.toList());
    }

    /**
     * Get reports by user id
     * @param reportId id report
     * @param newStatus new Status
     * @return ReportDto
     */
    public ReportDto updateStatus(Long reportId, ReportStatus newStatus) {
        Report report = this.reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        ReportStatusHistory statusHistory = new ReportStatusHistory(report, newStatus);
        this.reportStatusHistoryRepository.save(statusHistory);

        report.setStatus(newStatus);
        report = this.reportRepository.save(report);

        return this.mapper.toDto(report);
    }

    /**
     * Get status history
     * @param reportId id report
     * @return ReportDto
     */
    public List<ReportStatusHistoryDto> getStatusHistory(Long reportId) {
        List<ReportStatusHistory> reports = this.reportStatusHistoryRepository.findByReportId(reportId);
        return this.mapper.toReportStatusHistoryDtoToList(reports);
    }

    /**
     * Get reports by status
     * @param status status to update
     * @return ReportDto list
     */
    public List<ReportDto> getReportsByStatus(ReportStatus status) {
        List<Report> reports = this.reportRepository.findByStatus(status);
        return this.mapper.toReportDtoList(reports);
    }

    /**
     * Emmit the message with new reports
     * @param report report to notify
     */
    private void notifyCreationReport(Report report) {

        ReportBroadcastDto newReport = new ReportBroadcastDto(
                report.getId(),
                report.getLatitude(),
                report.getLongitude(),
                report.getType().getName(),
                report.getDescription(),
                report.getDateCreated()
        );

        this.messagingTemplate.convertAndSend("/topic/reports", newReport);
    }
}
