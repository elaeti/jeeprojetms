package com.construction.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A Appartement.
 */
@Entity
@Table(name = "appartement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Appartement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "numeroappart")
    private Integer numeroappart;

    @ManyToOne
    @JsonIgnoreProperties(value = "appartements", allowSetters = true)
    private Etage etage;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Appartement name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumeroappart() {
        return numeroappart;
    }

    public Appartement numeroappart(Integer numeroappart) {
        this.numeroappart = numeroappart;
        return this;
    }

    public void setNumeroappart(Integer numeroappart) {
        this.numeroappart = numeroappart;
    }

    public Etage getEtage() {
        return etage;
    }

    public Appartement etage(Etage etage) {
        this.etage = etage;
        return this;
    }

    public void setEtage(Etage etage) {
        this.etage = etage;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Appartement)) {
            return false;
        }
        return id != null && id.equals(((Appartement) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Appartement{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", numeroappart=" + getNumeroappart() +
            "}";
    }
}
