package com.backend.project.repository;

import com.backend.project.model.Report;
import com.backend.project.model.ReportComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IReportCommentRepository extends JpaRepository<ReportComment, Long> {

    List<ReportComment> findCommentsByReportId(Long idReport);
}
