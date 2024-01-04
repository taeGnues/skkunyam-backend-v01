package com.skkudteam3.skkusirenorder.src.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.skkudteam3.skkusirenorder.src.entity.WeekDays;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CafeteriaGetResDTO {
    private Long cafeteriaId;
    private String name;
    private String location;
    private String description;
    private String contact;
    @JsonProperty("weekdays")
    private WeekDays weekDays;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime openTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime closeTime;


    public CafeteriaGetResDTO(Long cafeteriaId, String name, String location, String description, String contact, WeekDays weekDays, LocalDateTime openTime, LocalDateTime closeTime) {
        this.cafeteriaId = cafeteriaId;
        this.name = name;
        this.location = location;
        this.description = description;
        this.contact = contact;
        this.weekDays = weekDays;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }
}
