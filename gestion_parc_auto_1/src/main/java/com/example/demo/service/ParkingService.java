package com.example.demo.service;

import com.example.demo.model.Parking;

import java.util.List;

public interface ParkingService {

    Parking saveParking(Parking parking);
    Parking getParkingById(Long id);
    List<Parking> getAllParkings();
    Parking updateParking(Long id, Parking parking);
    void deleteParking(Long id);
}
