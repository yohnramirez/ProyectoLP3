package com.backend.project.repository;

import com.backend.project.model.Report;
import com.backend.project.util.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IReportRepository extends JpaRepository<Report, Long> {
    @Query(value = """
        SELECT * FROM reports\s
        WHERE (6371000 * acos(cos(radians(:latitude)) * cos(radians(latitude))\s
        * cos(radians(longitude) - radians(:longitude)) + sin(radians(:latitude))\s
        * sin(radians(latitude)))) < :radius
   \s""", nativeQuery = true)
    List<Report> findNearbyReports(@Param("latitude") double latitude,
                                   @Param("longitude") double longitude,
                                   @Param("radius") double radius);

    List<Report> findByUserId(String idUser);

    List<Report> findByStatus(ReportStatus status);
}
