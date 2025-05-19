package com.backend.project.logic;

import com.backend.project.configuration.mapper.IMapper;
import com.backend.project.dto.ReportCommentDto;
import com.backend.project.model.Report;
import com.backend.project.model.ReportComment;
import com.backend.project.model.ReportType;
import com.backend.project.repository.IReportCommentRepository;
import com.backend.project.repository.IReportRepository;
import com.backend.project.repository.IReportStatusHistoryRepository;
import com.backend.project.util.enums.ReportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportCommentLogic {

    /**
     * Instance of the repository type layer
     */
    private final IReportCommentRepository reportCommentRepository;

    /**
     * Instance of the repository layer
     */
    private final IReportRepository reportRepository;

    /**
     * Implementation of the user mapper
     */
    private final IMapper mapper;

    /**
     * Create a comment
     * @param ReportCommentDto data info
     * @return ReportCommentDto
     */
    public ReportCommentDto createComment(ReportCommentDto commentDto) {
        try {
            Report report = reportRepository.findById(commentDto.getReportId())
                    .orElseThrow(() -> new RuntimeException("Report not found"));

            ReportComment reportComment = this.mapper.toEntity(commentDto);
            reportComment.setReport(report);

            reportComment = this.reportCommentRepository.save(reportComment);

            return this.mapper.toDto(reportComment);

        } catch (Exception ex) {
            System.out.println("[createComment]: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Get comments by report
     * @param Long idReport
     * @return List<ReportCommentDto>
     */
    public List<ReportCommentDto> getCommentsByReportId(Long idReport) {
        try {

            List<ReportComment> comments = this.reportCommentRepository.findCommentsByReportId(idReport);
            return this.mapper.toReportCommentDtoToList(comments);

        } catch (Exception ex) {
            System.out.println("[getCommentsByReportId]: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Delete comment report
     * @param id identifier comment report
     * @return void
     */
    public void deleteComment(Long id) {
        try {
            Optional<ReportComment> commentToDelete = this.reportCommentRepository.findById(id);

            if (commentToDelete.isPresent()) {
                this.reportCommentRepository.deleteById(id);
            }

        } catch (Exception ex) {
            System.out.println("[deleteComment]: " + ex.getMessage());
        }
    }
}
