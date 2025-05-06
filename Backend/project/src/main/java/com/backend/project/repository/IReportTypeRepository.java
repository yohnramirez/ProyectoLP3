package com.backend.project.repository;

import com.backend.project.model.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IReportTypeRepository extends JpaRepository<ReportType, Long> {

    Optional<ReportType> findByName(String name);
}
