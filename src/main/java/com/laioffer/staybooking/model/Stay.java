package com.laioffer.staybooking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
@Entity
@Table(name = "stay")
@JsonDeserialize(builder = Stay.Builder.class)
public class Stay implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id //用id作为primary key
    @GeneratedValue(strategy = GenerationType.AUTO) //不会回头添加，回往后涨
    private Long id;
    private String name;
    private String description;
    private String address;
    @JsonProperty("guest_number")
    private int guestNumber;
    @ManyToOne
    @JoinColumn(name = "user_id") //自动创建一个额外的column作为外键，mapid不创建新的column
    private User host;

    //删房子信息的时候会删除对应的图片
    @OneToMany(mappedBy = "stay", cascade = CascadeType.ALL, fetch=FetchType.EAGER) //cascade上传删除时操作对应
    private List<StayImage> images;

    public Stay() {}

    private Stay(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.address = builder.address;
        this.guestNumber = builder.guestNumber;
        this.host = builder.host;
        this.images = builder.images;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public int getGuestNumber() {
        return guestNumber;
    }

    public User getHost() {
        return host;
    }

    public List<StayImage> getImages() {
        return images;
    }

    public Stay setImages(List<StayImage> images) {
        this.images = images;
        return this;
    }


    public static class Builder {

        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;

        @JsonProperty("address")
        private String address;

        @JsonProperty("guest_number")
        private int guestNumber;

        @JsonProperty("host")
        private User host;

        @JsonProperty("images")
        private List<StayImage> images;


        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setGuestNumber(int guestNumber) {
            this.guestNumber = guestNumber;
            return this;
        }

        public Builder setHost(User host) {
            this.host = host;
            return this;
        }

        public Stay build() {
            return new Stay(this);
        }

        public Builder setImages(List<StayImage> images) {
            this.images = images;
            return this;
        }

    }


}
