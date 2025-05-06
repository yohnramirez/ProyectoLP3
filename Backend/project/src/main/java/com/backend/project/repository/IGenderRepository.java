package com.backend.project.repository;

import com.backend.project.model.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGenderRepository extends JpaRepository<Gender, Long> {
}
