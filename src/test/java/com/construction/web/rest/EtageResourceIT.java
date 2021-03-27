package com.construction.web.rest;

import com.construction.ConstructionMsApp;
import com.construction.config.TestSecurityConfiguration;
import com.construction.domain.Etage;
import com.construction.repository.EtageRepository;

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
 * Integration tests for the {@link EtageResource} REST controller.
 */
@SpringBootTest(classes = { ConstructionMsApp.class, TestSecurityConfiguration.class })
@AutoConfigureMockMvc
@WithMockUser
public class EtageResourceIT {

    private static final Integer DEFAULT_NUMEROETAGE = 1;
    private static final Integer UPDATED_NUMEROETAGE = 2;

    @Autowired
    private EtageRepository etageRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEtageMockMvc;

    private Etage etage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Etage createEntity(EntityManager em) {
        Etage etage = new Etage()
            .numeroetage(DEFAULT_NUMEROETAGE);
        return etage;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Etage createUpdatedEntity(EntityManager em) {
        Etage etage = new Etage()
            .numeroetage(UPDATED_NUMEROETAGE);
        return etage;
    }

    @BeforeEach
    public void initTest() {
        etage = createEntity(em);
    }

    @Test
    @Transactional
    public void createEtage() throws Exception {
        int databaseSizeBeforeCreate = etageRepository.findAll().size();
        // Create the Etage
        restEtageMockMvc.perform(post("/api/etages").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(etage)))
            .andExpect(status().isCreated());

        // Validate the Etage in the database
        List<Etage> etageList = etageRepository.findAll();
        assertThat(etageList).hasSize(databaseSizeBeforeCreate + 1);
        Etage testEtage = etageList.get(etageList.size() - 1);
        assertThat(testEtage.getNumeroetage()).isEqualTo(DEFAULT_NUMEROETAGE);
    }

    @Test
    @Transactional
    public void createEtageWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = etageRepository.findAll().size();

        // Create the Etage with an existing ID
        etage.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEtageMockMvc.perform(post("/api/etages").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(etage)))
            .andExpect(status().isBadRequest());

        // Validate the Etage in the database
        List<Etage> etageList = etageRepository.findAll();
        assertThat(etageList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNumeroetageIsRequired() throws Exception {
        int databaseSizeBeforeTest = etageRepository.findAll().size();
        // set the field null
        etage.setNumeroetage(null);

        // Create the Etage, which fails.


        restEtageMockMvc.perform(post("/api/etages").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(etage)))
            .andExpect(status().isBadRequest());

        List<Etage> etageList = etageRepository.findAll();
        assertThat(etageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEtages() throws Exception {
        // Initialize the database
        etageRepository.saveAndFlush(etage);

        // Get all the etageList
        restEtageMockMvc.perform(get("/api/etages?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(etage.getId().intValue())))
            .andExpect(jsonPath("$.[*].numeroetage").value(hasItem(DEFAULT_NUMEROETAGE)));
    }
    
    @Test
    @Transactional
    public void getEtage() throws Exception {
        // Initialize the database
        etageRepository.saveAndFlush(etage);

        // Get the etage
        restEtageMockMvc.perform(get("/api/etages/{id}", etage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(etage.getId().intValue()))
            .andExpect(jsonPath("$.numeroetage").value(DEFAULT_NUMEROETAGE));
    }
    @Test
    @Transactional
    public void getNonExistingEtage() throws Exception {
        // Get the etage
        restEtageMockMvc.perform(get("/api/etages/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEtage() throws Exception {
        // Initialize the database
        etageRepository.saveAndFlush(etage);

        int databaseSizeBeforeUpdate = etageRepository.findAll().size();

        // Update the etage
        Etage updatedEtage = etageRepository.findById(etage.getId()).get();
        // Disconnect from session so that the updates on updatedEtage are not directly saved in db
        em.detach(updatedEtage);
        updatedEtage
            .numeroetage(UPDATED_NUMEROETAGE);

        restEtageMockMvc.perform(put("/api/etages").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedEtage)))
            .andExpect(status().isOk());

        // Validate the Etage in the database
        List<Etage> etageList = etageRepository.findAll();
        assertThat(etageList).hasSize(databaseSizeBeforeUpdate);
        Etage testEtage = etageList.get(etageList.size() - 1);
        assertThat(testEtage.getNumeroetage()).isEqualTo(UPDATED_NUMEROETAGE);
    }

    @Test
    @Transactional
    public void updateNonExistingEtage() throws Exception {
        int databaseSizeBeforeUpdate = etageRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEtageMockMvc.perform(put("/api/etages").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(etage)))
            .andExpect(status().isBadRequest());

        // Validate the Etage in the database
        List<Etage> etageList = etageRepository.findAll();
        assertThat(etageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteEtage() throws Exception {
        // Initialize the database
        etageRepository.saveAndFlush(etage);

        int databaseSizeBeforeDelete = etageRepository.findAll().size();

        // Delete the etage
        restEtageMockMvc.perform(delete("/api/etages/{id}", etage.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Etage> etageList = etageRepository.findAll();
        assertThat(etageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
