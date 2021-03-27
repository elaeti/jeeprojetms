package com.construction.web.rest;

import com.construction.ConstructionMsApp;
import com.construction.config.TestSecurityConfiguration;
import com.construction.domain.Appartement;
import com.construction.repository.AppartementRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AppartementResource} REST controller.
 */
@SpringBootTest(classes = { ConstructionMsApp.class, TestSecurityConfiguration.class })
@AutoConfigureMockMvc
@WithMockUser
public class AppartementResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUMEROAPPART = 1;
    private static final Integer UPDATED_NUMEROAPPART = 2;

    @Autowired
    private AppartementRepository appartementRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppartementMockMvc;

    private Appartement appartement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appartement createEntity(EntityManager em) {
        Appartement appartement = new Appartement()
            .name(DEFAULT_NAME)
            .numeroappart(DEFAULT_NUMEROAPPART);
        return appartement;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appartement createUpdatedEntity(EntityManager em) {
        Appartement appartement = new Appartement()
            .name(UPDATED_NAME)
            .numeroappart(UPDATED_NUMEROAPPART);
        return appartement;
    }

    @BeforeEach
    public void initTest() {
        appartement = createEntity(em);
    }

    @Test
    @Transactional
    public void createAppartement() throws Exception {
        int databaseSizeBeforeCreate = appartementRepository.findAll().size();
        // Create the Appartement
        restAppartementMockMvc.perform(post("/api/appartements").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(appartement)))
            .andExpect(status().isCreated());

        // Validate the Appartement in the database
        List<Appartement> appartementList = appartementRepository.findAll();
        assertThat(appartementList).hasSize(databaseSizeBeforeCreate + 1);
        Appartement testAppartement = appartementList.get(appartementList.size() - 1);
        assertThat(testAppartement.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAppartement.getNumeroappart()).isEqualTo(DEFAULT_NUMEROAPPART);
    }

    @Test
    @Transactional
    public void createAppartementWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = appartementRepository.findAll().size();

        // Create the Appartement with an existing ID
        appartement.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppartementMockMvc.perform(post("/api/appartements").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(appartement)))
            .andExpect(status().isBadRequest());

        // Validate the Appartement in the database
        List<Appartement> appartementList = appartementRepository.findAll();
        assertThat(appartementList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = appartementRepository.findAll().size();
        // set the field null
        appartement.setName(null);

        // Create the Appartement, which fails.


        restAppartementMockMvc.perform(post("/api/appartements").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(appartement)))
            .andExpect(status().isBadRequest());

        List<Appartement> appartementList = appartementRepository.findAll();
        assertThat(appartementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAppartements() throws Exception {
        // Initialize the database
        appartementRepository.saveAndFlush(appartement);

        // Get all the appartementList
        restAppartementMockMvc.perform(get("/api/appartements?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appartement.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].numeroappart").value(hasItem(DEFAULT_NUMEROAPPART)));
    }
    
    @Test
    @Transactional
    public void getAppartement() throws Exception {
        // Initialize the database
        appartementRepository.saveAndFlush(appartement);

        // Get the appartement
        restAppartementMockMvc.perform(get("/api/appartements/{id}", appartement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appartement.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.numeroappart").value(DEFAULT_NUMEROAPPART));
    }
    @Test
    @Transactional
    public void getNonExistingAppartement() throws Exception {
        // Get the appartement
        restAppartementMockMvc.perform(get("/api/appartements/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAppartement() throws Exception {
        // Initialize the database
        appartementRepository.saveAndFlush(appartement);

        int databaseSizeBeforeUpdate = appartementRepository.findAll().size();

        // Update the appartement
        Appartement updatedAppartement = appartementRepository.findById(appartement.getId()).get();
        // Disconnect from session so that the updates on updatedAppartement are not directly saved in db
        em.detach(updatedAppartement);
        updatedAppartement
            .name(UPDATED_NAME)
            .numeroappart(UPDATED_NUMEROAPPART);

        restAppartementMockMvc.perform(put("/api/appartements").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedAppartement)))
            .andExpect(status().isOk());

        // Validate the Appartement in the database
        List<Appartement> appartementList = appartementRepository.findAll();
        assertThat(appartementList).hasSize(databaseSizeBeforeUpdate);
        Appartement testAppartement = appartementList.get(appartementList.size() - 1);
        assertThat(testAppartement.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAppartement.getNumeroappart()).isEqualTo(UPDATED_NUMEROAPPART);
    }

    @Test
    @Transactional
    public void updateNonExistingAppartement() throws Exception {
        int databaseSizeBeforeUpdate = appartementRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppartementMockMvc.perform(put("/api/appartements").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(appartement)))
            .andExpect(status().isBadRequest());

        // Validate the Appartement in the database
        List<Appartement> appartementList = appartementRepository.findAll();
        assertThat(appartementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteAppartement() throws Exception {
        // Initialize the database
        appartementRepository.saveAndFlush(appartement);

        int databaseSizeBeforeDelete = appartementRepository.findAll().size();

        // Delete the appartement
        restAppartementMockMvc.perform(delete("/api/appartements/{id}", appartement.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Appartement> appartementList = appartementRepository.findAll();
        assertThat(appartementList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
