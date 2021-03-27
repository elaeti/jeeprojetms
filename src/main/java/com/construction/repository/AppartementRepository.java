package com.construction.repository;

import com.construction.domain.Appartement;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Appartement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppartementRepository extends JpaRepository<Appartement, Long> {
}
