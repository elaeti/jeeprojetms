package com.construction.web.rest;

import com.construction.domain.Appartement;
import com.construction.repository.AppartementRepository;
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
 * REST controller for managing {@link com.construction.domain.Appartement}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AppartementResource {

    private final Logger log = LoggerFactory.getLogger(AppartementResource.class);

    private static final String ENTITY_NAME = "constructionMsAppartement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppartementRepository appartementRepository;

    public AppartementResource(AppartementRepository appartementRepository) {
        this.appartementRepository = appartementRepository;
    }

    /**
     * {@code POST  /appartements} : Create a new appartement.
     *
     * @param appartement the appartement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appartement, or with status {@code 400 (Bad Request)} if the appartement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/appartements")
    public ResponseEntity<Appartement> createAppartement(@Valid @RequestBody Appartement appartement) throws URISyntaxException {
        log.debug("REST request to save Appartement : {}", appartement);
        if (appartement.getId() != null) {
            throw new BadRequestAlertException("A new appartement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Appartement result = appartementRepository.save(appartement);
        return ResponseEntity.created(new URI("/api/appartements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /appartements} : Updates an existing appartement.
     *
     * @param appartement the appartement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appartement,
     * or with status {@code 400 (Bad Request)} if the appartement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appartement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/appartements")
    public ResponseEntity<Appartement> updateAppartement(@Valid @RequestBody Appartement appartement) throws URISyntaxException {
        log.debug("REST request to update Appartement : {}", appartement);
        if (appartement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Appartement result = appartementRepository.save(appartement);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appartement.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /appartements} : get all the appartements.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appartements in body.
     */
    @GetMapping("/appartements")
    public ResponseEntity<List<Appartement>> getAllAppartements(Pageable pageable) {
        log.debug("REST request to get a page of Appartements");
        Page<Appartement> page = appartementRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /appartements/:id} : get the "id" appartement.
     *
     * @param id the id of the appartement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appartement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/appartements/{id}")
    public ResponseEntity<Appartement> getAppartement(@PathVariable Long id) {
        log.debug("REST request to get Appartement : {}", id);
        Optional<Appartement> appartement = appartementRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(appartement);
    }

    /**
     * {@code DELETE  /appartements/:id} : delete the "id" appartement.
     *
     * @param id the id of the appartement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/appartements/{id}")
    public ResponseEntity<Void> deleteAppartement(@PathVariable Long id) {
        log.debug("REST request to delete Appartement : {}", id);
        appartementRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
