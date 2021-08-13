package com.walt.model;

import org.hibernate.Hibernate;

import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class City extends NamedEntity{

    public City(){}

    public City(String name){
        super(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        City city = (City) o;

        return Objects.equals(getId(), city.getId());
    }

    @Override
    public int hashCode() {
        return 39525063;
    }
}
