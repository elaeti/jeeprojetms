package com.construction.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Batiment.
 */
@Entity
@Table(name = "batiment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Batiment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "batiment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Etage> etages = new HashSet<>();

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

    public Batiment name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Etage> getEtages() {
        return etages;
    }

    public Batiment etages(Set<Etage> etages) {
        this.etages = etages;
        return this;
    }

    public Batiment addEtage(Etage etage) {
        this.etages.add(etage);
        etage.setBatiment(this);
        return this;
    }

    public Batiment removeEtage(Etage etage) {
        this.etages.remove(etage);
        etage.setBatiment(null);
        return this;
    }

    public void setEtages(Set<Etage> etages) {
        this.etages = etages;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Batiment)) {
            return false;
        }
        return id != null && id.equals(((Batiment) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Batiment{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
