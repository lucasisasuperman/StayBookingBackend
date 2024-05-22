package com.laioffer.staybooking.service;
import com.laioffer.staybooking.exception.StayDeleteException;
import com.laioffer.staybooking.exception.StayNotExistException;
import com.laioffer.staybooking.repository.ReservationRepository;
import com.laioffer.staybooking.repository.StayReservationDateRepository;
import org.springframework.stereotype.Service;
import com.laioffer.staybooking.repository.StayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.laioffer.staybooking.model.*;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.laioffer.staybooking.repository.LocationRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StayService {
    private StayRepository stayRepository;
    private ImageStorageService imageStorageService;
    private LocationRepository locationRepository;
    private GeoCodingService geoCodingService;
    private ReservationRepository reservationRepository;
    private StayReservationDateRepository stayReservationDateRepository;




    @Autowired
    public StayService(StayRepository stayRepository, ImageStorageService imageStorageService,
            LocationRepository locationRepository, GeoCodingService geoCodingService,
                       ReservationRepository reservationRepository, StayReservationDateRepository stayReservationDateRepository) {

        this.stayRepository = stayRepository;
        this.imageStorageService = imageStorageService;
        this.locationRepository = locationRepository;
        this.geoCodingService = geoCodingService;
        this.reservationRepository = reservationRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;

    }

    public List<Stay> listByUser(String username) {
        return stayRepository.findByHost(new User.Builder().setUsername(username).build());
    }

    public Stay findByIdAndHost(Long stayId, String username) throws StayNotExistException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }
        return stay;
    }
    //两个table一起操作，上传失败不会触发另一个，transactional对应mysql，transactional不是用来rollback
    @Transactional(isolation = Isolation.SERIALIZABLE) //对两个表进行操作，上传stay相关信息，并且上传image信息
    public void add(Stay stay, MultipartFile[] images) {
        List<String> mediaLinks = Arrays.stream(images).parallel().map(image -> imageStorageService.save(image)).collect(Collectors.toList());
        List<StayImage> stayImages = new ArrayList<>();
        for (String mediaLink : mediaLinks) {
            stayImages.add(new StayImage(mediaLink, stay));
        }
        stay.setImages(stayImages);
        stayRepository.save(stay);

        Location location = geoCodingService.getLatLng(stay.getId(), stay.getAddress()); //after saving, will return the id to the state field
        locationRepository.save(location); //save users location to 经纬度 save location info to elastic search

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long stayId, String username) throws StayNotExistException, StayDeleteException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }
        List<Reservation> reservations = reservationRepository.findByStayAndCheckoutDateAfter(stay, LocalDate.now());
        if (reservations != null && reservations.size() > 0) {
            throw new StayDeleteException("Cannot delete stay with active reservation");
        }
        //stayReservationDateRepository.deleteAllById(Arrays.asList(stayId));
        List<StayReservedDate> stayReservedDates = stayReservationDateRepository.findByStay(stay);

        for(StayReservedDate date : stayReservedDates) {
            stayReservationDateRepository.deleteById(date.getId());
        }
        stayRepository.deleteById(stayId);
    }
    //Delete image stay location in GCS:
    //可以先删除stay，对于没用的image写一个定期的清理的程序，不需要每时每刻的清理
    //useless location相同操作
}

//在一个新的project里写一个定期清理的程序会定期清理没用的url图片等
//loop all files check if the url in stay_image table, if no, delete
//google cloud可以用来创建cron jobs