package com.laioffer.staybooking.controller;

import org.springframework.web.bind.annotation.RestController;
import com.laioffer.staybooking.service.StayService;
import org.springframework.beans.factory.annotation.Autowired;
import com.laioffer.staybooking.model.Stay;
import org.springframework.web.bind.annotation.*;
import com.laioffer.staybooking.model.User;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.List;
import com.laioffer.staybooking.model.Reservation;
import com.laioffer.staybooking.service.ReservationService;

@RestController
public class StayController {
    private StayService stayService;
    private ReservationService reservationService;

    @Autowired
    public StayController(StayService stayService, ReservationService reservationService) {

        this.stayService = stayService;
        this.reservationService = reservationService;
    }

    @GetMapping(value = "/stays")
    public List<Stay> listStays(Principal principal) {

        return stayService.listByUser(principal.getName());
    }

    @GetMapping(value = "/stays/{stayId}")
    public Stay getStay(@PathVariable Long stayId, Principal principal) {
        return stayService.findByIdAndHost(stayId, principal.getName());
    }


    @PostMapping("/stays")
    public void addStay(
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("description") String description,
            @RequestParam("guest_number") int guestNumber,
            @RequestParam("images") MultipartFile[] images,
            Principal principal) {

        Stay stay = new Stay.Builder()
                .setName(name)
                .setAddress(address)
                .setDescription(description)
                .setGuestNumber(guestNumber)
                .setHost(new User.Builder().setUsername(principal.getName()).build())
                .build();
        stayService.add(stay, images);
    }

    @DeleteMapping("/stays/{stayId}")
    public void deleteStay(@PathVariable Long stayId, Principal principal) {
        stayService.delete(stayId, principal.getName());
    }

    @GetMapping(value = "/stays/reservations/{stayId}")
    public List<Reservation> listReservations(@PathVariable Long stayId) {
        return reservationService.listByStay(stayId);
    }

}
