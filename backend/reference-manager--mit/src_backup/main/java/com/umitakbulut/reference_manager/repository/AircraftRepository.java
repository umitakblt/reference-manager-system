package com.umitakbulut.reference_manager.repository;

import com.umitakbulut.reference_manager.entity.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AircraftRepository  extends JpaRepository<Aircraft, Long> {
}
