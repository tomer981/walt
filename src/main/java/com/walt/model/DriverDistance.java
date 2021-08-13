package com.walt.model;

import org.springframework.beans.factory.annotation.Value;

public interface DriverDistance {
    Driver getDriver();
    @Value("#{target.distance}")
    Long getTotalDistance();
}
