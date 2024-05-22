package com.laioffer.staybooking.repository;
//spring代码量减少，省略重复的逻辑，但是不容易debug，作为和数据库交互的framework
//user and authority tables
//不需要json的serialize
import com.laioffer.staybooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
//extend jparepository会自动实现增删改查
//user某个table的object，string: id primary key类型
@Repository
public interface UserRepository extends JpaRepository<User, String> {

}

