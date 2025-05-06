package com.backend.project.repository;

import com.backend.project.model.ReportStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IReportStatusHistoryRepository extends JpaRepository<ReportStatusHistory, Long> {

    List<ReportStatusHistory> findByReportId(Long reportId);
}
