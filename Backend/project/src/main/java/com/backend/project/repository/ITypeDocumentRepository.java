package com.backend.project.repository;

import com.backend.project.model.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITypeDocumentRepository extends JpaRepository<TypeDocument, Long>  {
}
