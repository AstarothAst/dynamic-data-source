package com.example.demo.services;

import com.example.demo.dto.DatabasePingResultDto;
import com.example.demo.dto.DatasourceDto;
import com.example.demo.dto.PingAllJobDto;

import java.util.Collection;

public interface DataSourceService {

    void addDataSources(Collection<DatasourceDto> datasourceList);
    void addDataSource(DatasourceDto datasource);

    Collection<DatabasePingResultDto> pingAll();

    void pingAllJob(PingAllJobDto dto);
}
