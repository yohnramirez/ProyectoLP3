package com.backend.project.configuration.data;

import com.backend.project.model.Gender;
import com.backend.project.model.ReportType;
import com.backend.project.model.TypeDocument;
import com.backend.project.repository.IGenderRepository;
import com.backend.project.repository.IReportTypeRepository;
import com.backend.project.repository.ITypeDocumentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SeedData {

    @Bean
    CommandLineRunner initReportTypes(IReportTypeRepository reportTypeRepository) {
        return args -> {
            if (reportTypeRepository.count() == 0) {
                List<ReportType> types = List.of(
                        new ReportType("Robo", "Tipo de reporte para robos", true),
                        new ReportType("Accidente", "Tipo de reporte para accidentes", true),
                        new ReportType("Emergencia médica", "Tipo de reporte para emergencias médicas", true),
                        new ReportType("Incendio", "Tipo de reporte para incendios", true),
                        new ReportType("Actividad Sospechosa", "Tipo de reporte para actividades sospechosas", true)
                );

                reportTypeRepository.saveAll(types);
                System.out.println("Init report types was created successfully.");
            }
        };
    }

    @Bean
    CommandLineRunner initGenders(IGenderRepository genderRepository) {
        return args -> {
            if (genderRepository.count() == 0) {
                List<Gender> types = List.of(
                        new Gender("Femenino", "FEM", true),
                        new Gender("Masculino", "MAS", true),
                        new Gender("Prefiero No Especificar", "NE", true),
                        new Gender("Otro", "OT", true)
                );

                genderRepository.saveAll(types);
                System.out.println("Init genders was created successfully.");
            }
        };
    }

    @Bean
    CommandLineRunner initTypeDocuments(ITypeDocumentRepository typeDocumentRepository) {
        return args -> {
            if (typeDocumentRepository.count() == 0) {
                List<TypeDocument> types = List.of(
                        new TypeDocument("Cédula de Ciudadanía", "CC", true),
                        new TypeDocument("Tarjeta de Identidad", "TI", true),
                        new TypeDocument("Pasaporte", "PAS", true)
                );

                typeDocumentRepository.saveAll(types);
                System.out.println("Init type documents was created successfully.");
            }
        };
    }
}
