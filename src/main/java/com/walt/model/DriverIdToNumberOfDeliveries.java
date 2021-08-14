package com.walt.model;

import org.springframework.beans.factory.annotation.Value;

public interface DriverIdToNumberOfDeliveries {
    Long getId();
    @Value("#{target.deliveriesCount}")
    Long getDeliveriesCount();
}
