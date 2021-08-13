package com.walt.model;

import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Driver extends NamedEntity {

    @ManyToOne
    City city;

    public Driver(){}

    public Driver(String name, City city){
        super(name);
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Driver driver = (Driver) o;

        return Objects.equals(getId(), driver.getId());
    }

    @Override
    public int hashCode() {
        return 1858918620;
    }
}
