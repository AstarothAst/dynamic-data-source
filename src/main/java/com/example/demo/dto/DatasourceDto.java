package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DatasourceDto {

    private final static String URL_TEMPLATE = "jdbc:postgresql://%s:%s/%s";

    private final String host;
    private final Integer port;
    private final String database;
    private final String user;
    private final String password;

    public String getUrl() {
        return String.format(URL_TEMPLATE, host, port, database);
    }
}
