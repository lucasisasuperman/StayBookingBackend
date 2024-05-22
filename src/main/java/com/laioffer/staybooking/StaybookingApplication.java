package com.laioffer.staybooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication //整个service通过main方式启动
public class StaybookingApplication { //启动file

    public static void main(String[] args) {

        SpringApplication.run(StaybookingApplication.class, args);
    }

}
