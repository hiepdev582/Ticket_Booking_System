package com.ticketbooking.search.repository;

import com.ticketbooking.search.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip, String>, JpaSpecificationExecutor<Trip> {
}
