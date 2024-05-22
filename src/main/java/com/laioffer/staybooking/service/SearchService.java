package com.laioffer.staybooking.service;
import com.laioffer.staybooking.repository.LocationRepository;
import com.laioffer.staybooking.repository.StayReservationDateRepository;
import com.laioffer.staybooking.repository.StayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.laioffer.staybooking.model.Stay;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
public class SearchService {
    private StayRepository stayRepository;
    private StayReservationDateRepository stayReservationDateRepository;
    private LocationRepository locationRepository;

    @Autowired
    public SearchService(StayRepository stayRepository, StayReservationDateRepository stayReservationDateRepository, LocationRepository locationRepository) {
        this.stayRepository = stayRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
        this.locationRepository = locationRepository;
    }

    public List<Stay> search(int guestNumber, LocalDate checkinDate, LocalDate checkoutDate, double lat, double lon, String distance) {

        List<Long> stayIds = locationRepository.searchByDistance(lat, lon, distance);
        if (stayIds == null || stayIds.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> reservedStayIds = stayReservationDateRepository.findByIdInAndDateBetween(stayIds, checkinDate, checkoutDate.minusDays(1));
        //checkout那一天就可以新的checkin

        List<Long> filteredStayIds = new ArrayList<>();
        for (Long stayId : stayIds) { //不存在set里就加进去然后返回
            if (!reservedStayIds.contains(stayId)) {
                filteredStayIds.add(stayId);
            }
        }
        return stayRepository.findByIdInAndGuestNumberGreaterThanEqual(filteredStayIds, guestNumber);
    }

}
