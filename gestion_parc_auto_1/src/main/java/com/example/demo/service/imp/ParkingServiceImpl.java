package com.example.demo.service.imp;



import com.example.demo.Repository.ParkingRepository;
import com.example.demo.model.Parking;
import com.example.demo.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingServiceImpl implements ParkingService {

    @Autowired
    private ParkingRepository parkingRepository;

    @Override
    public Parking saveParking(Parking parking) {
        return parkingRepository.save(parking);
    }

    @Override
    public Parking getParkingById(Long id) {
        return parkingRepository.findById(id).orElse(null);
    }

    @Override
    public List<Parking> getAllParkings() {
        return parkingRepository.findAll();
    }

    @Override
    public Parking updateParking(Long id, Parking parking) {
        if (parkingRepository.existsById(id)) {
            parking.setId(id);
            return parkingRepository.save(parking);
        }
        return null;
    }

    @Override
    public void deleteParking(Long id) {
        parkingRepository.deleteById(id);
    }
}
