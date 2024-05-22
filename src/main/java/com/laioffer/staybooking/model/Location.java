package com.laioffer.staybooking.model;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

import java.io.Serializable;

//存在elastic search数据库里面，不需要autowired，不在mysql里面
//通过elastic search找到id，在MySQL中找到完整的信息
@Document(indexName = "loc")
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @GeoPointField
    private GeoPoint geoPoint;

    public Location(Long id, GeoPoint geoPoint) {
        this.id = id; //数据库state表的id
        this.geoPoint = geoPoint;
    }

    public Long getId() {
        return id;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }
}
