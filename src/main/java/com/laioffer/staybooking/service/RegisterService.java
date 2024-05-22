package com.laioffer.staybooking.service;
import com.laioffer.staybooking.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.laioffer.staybooking.repository.AuthorityRepository;
import com.laioffer.staybooking.repository.UserRepository;
import org.springframework.transaction.annotation.Isolation;
import com.laioffer.staybooking.model.Authority;
import com.laioffer.staybooking.model.User;
import org.springframework.transaction.annotation.Transactional;
import com.laioffer.staybooking.exception.UserAlreadyExistException;


//接受前端发送的user对象以及type
//annotation通过spring实现

@Service
public class RegisterService {
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private PasswordEncoder passwordEncoder;
    @Autowired
    //dependency injection 调用增删改查
    public RegisterService(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE) //如果有异常会rollback上一个状态
    //spring framework package,保证method里面对数据库的操作的原子性，保证每个操作都成功，如果有问题会自动rollback
    public void add(User user, UserRole role) throws UserAlreadyExistException{ //error出现抛异常或者自己能处理
        if (userRepository.existsById(user.getUsername())) {
            throw new UserAlreadyExistException("User already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);
        authorityRepository.save(new Authority(user.getUsername(), role.name()));//enum里自动生成.name
    }
}
