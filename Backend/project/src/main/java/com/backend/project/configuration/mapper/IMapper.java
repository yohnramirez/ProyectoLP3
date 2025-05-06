package com.backend.project.configuration.mapper;

import com.backend.project.dto.*;
import com.backend.project.model.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IMapper {

    /**
     * REPORT
     */
    ReportDto toDto(Report user);

    Report toEntity(CreateRequestReportDto request);

    List<ReportDto> toReportDtoList(List<Report> reports);


    /**
     * REPORT TYPE
     */
    ReportTypeDto toDto(ReportType type);

    ReportType toEntity(ReportTypeDto reportTypeDto);

    List<ReportTypeDto> toReportTypeDtoToList(List<ReportType> reportTypes);


    /**
     * TYPE DOCUMENT
     */
    TypeDocumentDto toDto(TypeDocument typeDocument);

    TypeDocument toEntity(TypeDocumentDto typeDocumentDto);

    List<TypeDocumentDto> toTypeDocumentDtoToList(List<TypeDocument> typeDocuments);


    /**
     * GENDER
     */
    GenderDto toDto(Gender gender);

    Gender toEntity(GenderDto genderDto);

    List<GenderDto> toGenderListToDto(List<Gender> genders);


    /**
     * REPORT STATUS HISTORY
     */
    ReportStatusHistoryDto toDto(ReportStatusHistory reportStatusHistory);

    ReportStatusHistory toEntity(ReportStatusHistoryDto dto);

    List<ReportStatusHistoryDto> toReportStatusHistoryDtoToList(List<ReportStatusHistory> reportStatusHistories);
}
