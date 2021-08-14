package com.walt.model;

import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames={"driver","deliveryTime"}))
public class Delivery {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "driver")
    Driver driver;

    @ManyToOne
    @JoinColumn(name = "restaurant")
    Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "customer")
    Customer customer;


    @Column(name = "deliveryTime")
    Date deliveryTime;

    @Column(name = "distance")
    double distance;

    public Delivery() {
    }

    public Delivery(Driver driver, Restaurant restaurant, Customer customer, Date deliveryTime) {
        this.driver = driver;
        this.restaurant = restaurant;
        this.customer = customer;
        this.deliveryTime = deliveryTime;
        this.distance = new Random().nextDouble() * 20;
    }

    public Long getId() {
        return id;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Delivery delivery = (Delivery) o;

        return Objects.equals(id, delivery.id);
    }

    @Override
    public int hashCode() {
        return 18880271;
    }
}
