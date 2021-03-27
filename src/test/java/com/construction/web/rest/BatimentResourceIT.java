package com.construction.web.rest;

import com.construction.ConstructionMsApp;
import com.construction.config.TestSecurityConfiguration;
import com.construction.domain.Batiment;
import com.construction.repository.BatimentRepository;

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
 * Integration tests for the {@link BatimentResource} REST controller.
 */
@SpringBootTest(classes = { ConstructionMsApp.class, TestSecurityConfiguration.class })
@AutoConfigureMockMvc
@WithMockUser
public class BatimentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private BatimentRepository batimentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBatimentMockMvc;

    private Batiment batiment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Batiment createEntity(EntityManager em) {
        Batiment batiment = new Batiment()
            .name(DEFAULT_NAME);
        return batiment;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Batiment createUpdatedEntity(EntityManager em) {
        Batiment batiment = new Batiment()
            .name(UPDATED_NAME);
        return batiment;
    }

    @BeforeEach
    public void initTest() {
        batiment = createEntity(em);
    }

    @Test
    @Transactional
    public void createBatiment() throws Exception {
        int databaseSizeBeforeCreate = batimentRepository.findAll().size();
        // Create the Batiment
        restBatimentMockMvc.perform(post("/api/batiments").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(batiment)))
            .andExpect(status().isCreated());

        // Validate the Batiment in the database
        List<Batiment> batimentList = batimentRepository.findAll();
        assertThat(batimentList).hasSize(databaseSizeBeforeCreate + 1);
        Batiment testBatiment = batimentList.get(batimentList.size() - 1);
        assertThat(testBatiment.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createBatimentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = batimentRepository.findAll().size();

        // Create the Batiment with an existing ID
        batiment.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBatimentMockMvc.perform(post("/api/batiments").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(batiment)))
            .andExpect(status().isBadRequest());

        // Validate the Batiment in the database
        List<Batiment> batimentList = batimentRepository.findAll();
        assertThat(batimentList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = batimentRepository.findAll().size();
        // set the field null
        batiment.setName(null);

        // Create the Batiment, which fails.


        restBatimentMockMvc.perform(post("/api/batiments").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(batiment)))
            .andExpect(status().isBadRequest());

        List<Batiment> batimentList = batimentRepository.findAll();
        assertThat(batimentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBatiments() throws Exception {
        // Initialize the database
        batimentRepository.saveAndFlush(batiment);

        // Get all the batimentList
        restBatimentMockMvc.perform(get("/api/batiments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(batiment.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
    
    @Test
    @Transactional
    public void getBatiment() throws Exception {
        // Initialize the database
        batimentRepository.saveAndFlush(batiment);

        // Get the batiment
        restBatimentMockMvc.perform(get("/api/batiments/{id}", batiment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(batiment.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }
    @Test
    @Transactional
    public void getNonExistingBatiment() throws Exception {
        // Get the batiment
        restBatimentMockMvc.perform(get("/api/batiments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBatiment() throws Exception {
        // Initialize the database
        batimentRepository.saveAndFlush(batiment);

        int databaseSizeBeforeUpdate = batimentRepository.findAll().size();

        // Update the batiment
        Batiment updatedBatiment = batimentRepository.findById(batiment.getId()).get();
        // Disconnect from session so that the updates on updatedBatiment are not directly saved in db
        em.detach(updatedBatiment);
        updatedBatiment
            .name(UPDATED_NAME);

        restBatimentMockMvc.perform(put("/api/batiments").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedBatiment)))
            .andExpect(status().isOk());

        // Validate the Batiment in the database
        List<Batiment> batimentList = batimentRepository.findAll();
        assertThat(batimentList).hasSize(databaseSizeBeforeUpdate);
        Batiment testBatiment = batimentList.get(batimentList.size() - 1);
        assertThat(testBatiment.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingBatiment() throws Exception {
        int databaseSizeBeforeUpdate = batimentRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBatimentMockMvc.perform(put("/api/batiments").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(batiment)))
            .andExpect(status().isBadRequest());

        // Validate the Batiment in the database
        List<Batiment> batimentList = batimentRepository.findAll();
        assertThat(batimentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBatiment() throws Exception {
        // Initialize the database
        batimentRepository.saveAndFlush(batiment);

        int databaseSizeBeforeDelete = batimentRepository.findAll().size();

        // Delete the batiment
        restBatimentMockMvc.perform(delete("/api/batiments/{id}", batiment.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Batiment> batimentList = batimentRepository.findAll();
        assertThat(batimentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
