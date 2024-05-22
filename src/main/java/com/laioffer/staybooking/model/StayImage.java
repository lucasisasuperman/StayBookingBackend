package com.laioffer.staybooking.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

@Entity
@Table(name = "stay_image")
public class StayImage implements Serializable {
    private static final long serialVersionUID = 1L; //版本序列号

    @Id
    private String url;

    @ManyToOne
    @JoinColumn(name = "stay_id") // create a column called stay id
    @JsonIgnore //返回image的时候只需要存url，不需要再返回state
    private Stay stay;

    public StayImage() { //save for hibernate

    }

    public StayImage(String url, Stay stay) {
        this.url = url;
        this.stay = stay;
    }

    public String getUrl() {
        return url;
    }

    public StayImage setUrl(String url) {
        this.url = url;
        return this;
    }

    public Stay getStay() {
        return stay;
    }

    public StayImage setStay(Stay stay) {
        this.stay = stay;
        return this;
    }

}
