package com.example.demo.dto;

import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
public class PingAllJobDto {

    private  boolean enable;

    public Boolean isPingEnable() {
        return enable;
    }
}
