package com.backend.project.logic;

import com.backend.project.configuration.mapper.IMapper;
import com.backend.project.dto.TypeDocumentDto;
import com.backend.project.model.TypeDocument;
import com.backend.project.repository.ITypeDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeDocumentLogic {

    /**
     * Instance of the repository layer
     */
    @Autowired
    private final ITypeDocumentRepository typeDocumentRepository;

    /**
     * Implementation mapper
     */
    @Autowired
    private final IMapper mapper;

    /**
     * Constructor
     * @param typeDocumentRepository instance of the logic layer
     * @param mapper instance of the repository layer
     */
    public TypeDocumentLogic(ITypeDocumentRepository typeDocumentRepository, IMapper mapper) {
        this.typeDocumentRepository = typeDocumentRepository;
        this.mapper = mapper;
    }

    /**
     * Create a type documents
     * @param typeDocumentDto data info
     * @return TypeDocumentDto
     */
    public TypeDocumentDto createTypeDocument(TypeDocumentDto typeDocumentDto) {
        try {

            TypeDocument typeDocument = this.mapper.toEntity(typeDocumentDto);
            typeDocument.setState(true);

            typeDocument = this.typeDocumentRepository.save(typeDocument);

            return this.mapper.toDto(typeDocument);

        } catch (Exception ex) {
            System.out.println("[createReport]: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Create all type documents
     * @return List to TypeDocumentDto
     */
    public List<TypeDocumentDto> getAll() {
        try {

            List<TypeDocument> typesDocuments = this.typeDocumentRepository.findAll();
            return this.mapper.toTypeDocumentDtoToList(typesDocuments);

        } catch (Exception ex) {
            System.out.println("[getAll]: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Get one
     * @param id identifier type document
     * @return TypeDocumentDto
     */
    public TypeDocumentDto getTypeDocumentById(Long id) {
        try {

            Optional<TypeDocument> typeDocument = this.typeDocumentRepository.findById(id);
            return typeDocument.map(this.mapper::toDto).orElse(null);

        } catch (Exception ex) {
            System.out.println("[getTypeDocumentById]: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Update type document
     * @param id identifier type document
     * @param updateData data to update
     * @return TypeDocumentDto
     */
    public TypeDocumentDto updateTypeDocument(Long id, TypeDocumentDto updateData) {
        try {

            Optional<TypeDocument> typeToUpdate = this.typeDocumentRepository.findById(id);

            if (typeToUpdate.isPresent()) {
                typeToUpdate.get().setName(updateData.getName());
                typeToUpdate.get().setMask(updateData.getMask());
                typeToUpdate.get().setState(updateData.isState());

                TypeDocument saveTypeDocument = this.typeDocumentRepository.save(typeToUpdate.get());

                return this.mapper.toDto(saveTypeDocument);
            }

            return null;

        } catch (Exception ex) {
            System.out.println("[updateTypeDocument]: " + ex.getMessage());
        }

        return null;
    }
}
