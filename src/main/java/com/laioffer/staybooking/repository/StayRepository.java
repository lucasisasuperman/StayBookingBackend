package com.laioffer.staybooking.repository;
import com.laioffer.staybooking.model.Stay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.laioffer.staybooking.model.User;
import java.util.List;


@Repository
public interface StayRepository extends JpaRepository<Stay, Long> {
//对stay这个class进行增删改查，类型是long
    List<Stay> findByHost(User user);
    Stay findByIdAndHost(Long id, User host);
    List<Stay> findByIdInAndGuestNumberGreaterThanEqual(List<Long> ids, int guestNumber);
}

