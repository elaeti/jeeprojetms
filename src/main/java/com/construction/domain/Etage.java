package com.construction.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Etage.
 */
@Entity
@Table(name = "etage")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Etage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "numeroetage", nullable = false)
    private Integer numeroetage;

    @OneToMany(mappedBy = "etage")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Appartement> appartements = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "etages", allowSetters = true)
    private Batiment batiment;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroetage() {
        return numeroetage;
    }

    public Etage numeroetage(Integer numeroetage) {
        this.numeroetage = numeroetage;
        return this;
    }

    public void setNumeroetage(Integer numeroetage) {
        this.numeroetage = numeroetage;
    }

    public Set<Appartement> getAppartements() {
        return appartements;
    }

    public Etage appartements(Set<Appartement> appartements) {
        this.appartements = appartements;
        return this;
    }

    public Etage addAppartement(Appartement appartement) {
        this.appartements.add(appartement);
        appartement.setEtage(this);
        return this;
    }

    public Etage removeAppartement(Appartement appartement) {
        this.appartements.remove(appartement);
        appartement.setEtage(null);
        return this;
    }

    public void setAppartements(Set<Appartement> appartements) {
        this.appartements = appartements;
    }

    public Batiment getBatiment() {
        return batiment;
    }

    public Etage batiment(Batiment batiment) {
        this.batiment = batiment;
        return this;
    }

    public void setBatiment(Batiment batiment) {
        this.batiment = batiment;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Etage)) {
            return false;
        }
        return id != null && id.equals(((Etage) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Etage{" +
            "id=" + getId() +
            ", numeroetage=" + getNumeroetage() +
            "}";
    }
}
