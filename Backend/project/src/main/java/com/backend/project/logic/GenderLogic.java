package com.backend.project.logic;

import com.backend.project.configuration.mapper.IMapper;
import com.backend.project.dto.GenderDto;
import com.backend.project.model.Gender;
import com.backend.project.repository.IGenderRepository;
import com.backend.project.repository.IGenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GenderLogic {

    /**
     * Instance of the repository layer
     */
    @Autowired
    private final IGenderRepository genderRepository;

    /**
     * Implementation mapper
     */
    @Autowired
    private final IMapper mapper;

    /**
     * Constructor
     * @param genderRepository instance of the logic layer
     * @param mapper instance of the repository layer
     */
    public GenderLogic(IGenderRepository genderRepository, IMapper mapper) {
        this.genderRepository = genderRepository;
        this.mapper = mapper;
    }

    /**
     * Create a type documents
     * @param typeDocumentDto data info
     * @return GenderDto
     */
    public GenderDto createGender(GenderDto typeDocumentDto) {
        try {

            Gender typeDocument = this.mapper.toEntity(typeDocumentDto);
            typeDocument.setState(true);

            typeDocument = this.genderRepository.save(typeDocument);

            return this.mapper.toDto(typeDocument);

        } catch (Exception ex) {
            System.out.println("[createReport]: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Create all type documents
     * @return List to GenderDto
     */
    public List<GenderDto> getAll() {
        try {

            List<Gender> genders = this.genderRepository.findAll();
            return this.mapper.toGenderListToDto(genders);

        } catch (Exception ex) {
            System.out.println("[getAll]: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Get one
     * @param id identifier type document
     * @return GenderDto
     */
    public GenderDto getGenderById(Long id) {
        try {

            Optional<Gender> gender = this.genderRepository.findById(id);
            return gender.map(this.mapper::toDto).orElse(null);

        } catch (Exception ex) {
            System.out.println("[getGenderById]: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Update type document
     * @param id identifier type document
     * @param updateData data to update
     * @return GenderDto
     */
    public GenderDto updateGender(Long id, GenderDto updateData) {
        try {

            Optional<Gender> genderToUpdate = this.genderRepository.findById(id);

            if (genderToUpdate.isPresent()) {
                genderToUpdate.get().setName(updateData.getName());
                genderToUpdate.get().setMask(updateData.getMask());
                genderToUpdate.get().setState(updateData.isState());

                Gender saveGender = this.genderRepository.save(genderToUpdate.get());

                return this.mapper.toDto(saveGender);
            }

            return null;

        } catch (Exception ex) {
            System.out.println("[updateGender]: " + ex.getMessage());
        }

        return null;
    }
    
    
}
