package com.construction.repository;

import com.construction.domain.Etage;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Etage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EtageRepository extends JpaRepository<Etage, Long> {
}
