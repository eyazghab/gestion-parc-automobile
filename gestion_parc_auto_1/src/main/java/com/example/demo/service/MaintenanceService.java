package com.example.demo.service;


import com.example.demo.Dto.MaintenanceDTO;
import com.example.demo.model.Maintenance;

import java.util.List;
import java.util.Optional;

public interface MaintenanceService {
	 Maintenance saveMaintenance(MaintenanceDTO maintenance);

	 Maintenance updateMaintenance(Long id, MaintenanceDTO dto);

	    Maintenance changeStatut(Long id, String statut);

	    List<MaintenanceDTO> getAllMaintenancesDTO();

	    Maintenance getMaintenanceById(Long id);

	    void deleteMaintenance(Long id);
	    
	    Maintenance cloturerMaintenance(Long idMaintenance);
	    
	    MaintenanceDTO getMaintenanceDTOById(Long id);

	    
	    
}
