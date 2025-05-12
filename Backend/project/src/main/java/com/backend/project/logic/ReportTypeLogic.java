package com.backend.project.logic;

import com.backend.project.configuration.mapper.IMapper;
import com.backend.project.dto.GenderDto;
import com.backend.project.dto.ReportTypeDto;
import com.backend.project.model.Gender;
import com.backend.project.model.ReportType;
import com.backend.project.repository.IGenderRepository;
import com.backend.project.repository.IReportTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportTypeLogic {

    /**
     * Instance of the repository layer
     */
    @Autowired
    private final IReportTypeRepository reportTypeRepository;

    /**
     * Implementation mapper
     */
    @Autowired
    private final IMapper mapper;

    /**
     * Constructor
     * @param reportTypeRepository instance of the logic layer
     * @param mapper instance of the repository layer
     */
    public ReportTypeLogic(IReportTypeRepository reportTypeRepository, IMapper mapper) {
        this.reportTypeRepository = reportTypeRepository;
        this.mapper = mapper;
    }

    /**
     * Create a type report
     * @param reportTypeDto data info
     * @return ReportTypeDto
     */
    public ReportTypeDto createTypeReport(ReportTypeDto reportTypeDto) {
        try {

            ReportType reportType = this.mapper.toEntity(reportTypeDto);
            reportType.setState(true);

            reportType = this.reportTypeRepository.save(reportType);

            return this.mapper.toDto(reportType);

        } catch (Exception ex) {
            System.out.println("[createReportType]: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Create all type reports
     * @return List to ReportTypeDto
     */
    public List<ReportTypeDto> getAll() {
        try {

            List<ReportType> reportTypes = this.reportTypeRepository.findAll();
            return this.mapper.toReportTypeDtoToList(reportTypes);

        } catch (Exception ex) {
            System.out.println("[getAll]: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Get one
     * @param id identifier type report
     * @return ReportTypeDto
     */
    public ReportTypeDto getTypeReportById(Long id) {
        try {

            Optional<ReportType> reportType = this.reportTypeRepository.findById(id);
            return reportType.map(this.mapper::toDto).orElse(null);

        } catch (Exception ex) {
            System.out.println("[getTypeReportById]: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Update type report
     * @param id identifier type report
     * @param updateData data to update
     * @return ReportTypeDto
     */
    public ReportTypeDto updateTypeReport(Long id, ReportTypeDto updateData) {
        try {

            Optional<ReportType> typeToUpdate = this.reportTypeRepository.findById(id);

            if (typeToUpdate.isPresent()) {
                typeToUpdate.get().setName(updateData.getName());
                typeToUpdate.get().setDescription(updateData.getDescription());
                typeToUpdate.get().setState(updateData.isState());

                ReportType saveTypeReport = this.reportTypeRepository.save(typeToUpdate.get());

                return this.mapper.toDto(saveTypeReport);
            }

            return null;

        } catch (Exception ex) {
            System.out.println("[updateTypeReport]: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Delete type report
     * @param id identifier type report
     * @return ReportTypeDto
     */
    public void deleteTypeReport(Long id) {
        try {
            Optional<ReportType> typeToDelete = this.reportTypeRepository.findById(id);

            if (typeToDelete.isPresent()) {
                this.reportTypeRepository.deleteById(id);
            }

        } catch (Exception ex) {
            System.out.println("[deleteTypeReport]: " + ex.getMessage());
        }
    }
}
