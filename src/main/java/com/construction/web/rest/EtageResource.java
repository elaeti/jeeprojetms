package com.construction.web.rest;

import com.construction.domain.Etage;
import com.construction.repository.EtageRepository;
import com.construction.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.construction.domain.Etage}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class EtageResource {

    private final Logger log = LoggerFactory.getLogger(EtageResource.class);

    private static final String ENTITY_NAME = "constructionMsEtage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EtageRepository etageRepository;

    public EtageResource(EtageRepository etageRepository) {
        this.etageRepository = etageRepository;
    }

    /**
     * {@code POST  /etages} : Create a new etage.
     *
     * @param etage the etage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new etage, or with status {@code 400 (Bad Request)} if the etage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/etages")
    public ResponseEntity<Etage> createEtage(@Valid @RequestBody Etage etage) throws URISyntaxException {
        log.debug("REST request to save Etage : {}", etage);
        if (etage.getId() != null) {
            throw new BadRequestAlertException("A new etage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Etage result = etageRepository.save(etage);
        return ResponseEntity.created(new URI("/api/etages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /etages} : Updates an existing etage.
     *
     * @param etage the etage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etage,
     * or with status {@code 400 (Bad Request)} if the etage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the etage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/etages")
    public ResponseEntity<Etage> updateEtage(@Valid @RequestBody Etage etage) throws URISyntaxException {
        log.debug("REST request to update Etage : {}", etage);
        if (etage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Etage result = etageRepository.save(etage);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, etage.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /etages} : get all the etages.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of etages in body.
     */
    @GetMapping("/etages")
    public ResponseEntity<List<Etage>> getAllEtages(Pageable pageable) {
        log.debug("REST request to get a page of Etages");
        Page<Etage> page = etageRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /etages/:id} : get the "id" etage.
     *
     * @param id the id of the etage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the etage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/etages/{id}")
    public ResponseEntity<Etage> getEtage(@PathVariable Long id) {
        log.debug("REST request to get Etage : {}", id);
        Optional<Etage> etage = etageRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(etage);
    }

    /**
     * {@code DELETE  /etages/:id} : delete the "id" etage.
     *
     * @param id the id of the etage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/etages/{id}")
    public ResponseEntity<Void> deleteEtage(@PathVariable Long id) {
        log.debug("REST request to delete Etage : {}", id);
        etageRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
