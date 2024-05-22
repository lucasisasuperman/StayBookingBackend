package com.laioffer.staybooking.repository;
//如果是个class需要自己define over-write method
import com.laioffer.staybooking.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
    Authority findAuthorityByUsername(String username);
    //取出来用户的authority便于以后验证用户数据
}
//JwtUtil验证token中相关信息

