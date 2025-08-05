package com.umitakbulut.reference_manager.service;

import com.umitakbulut.reference_manager.entity.Flight;
import com.umitakbulut.reference_manager.entity.FlightArchive;

import java.util.List;

public interface FlightArchiveService {

    void saveFromFlight(Flight flight);
    
    List<FlightArchive> getAllArchivedFlights();
    
    void updateArchivedAtForExistingRecords();
    
    void deleteArchivedFlight(Long id);
    
    void deleteAllArchivedFlights();
}
