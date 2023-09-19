package com.example.demo.services;

import com.example.demo.dto.DatabasePingResultDto;
import com.example.demo.dto.DatasourceDto;

import java.util.Collection;
import java.util.List;

public interface DataSourceService {

    void addDatasources(Collection<DatasourceDto> datasourceList);
    void addDatasource(DatasourceDto datasourceDto);

    Collection<DatabasePingResultDto> pingAll();
}
