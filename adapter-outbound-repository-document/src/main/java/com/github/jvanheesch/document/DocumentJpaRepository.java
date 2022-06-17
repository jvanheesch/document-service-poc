package com.github.jvanheesch.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentJpaRepository extends JpaRepository<DocumentDTO, Long> {
    Optional<DocumentDTO> findByCorrelation(String correlation);
}
