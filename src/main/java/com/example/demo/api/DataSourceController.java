package com.example.demo.api;

import com.example.demo.dto.DatabasePingResultDto;
import com.example.demo.dto.DatasourceDto;
import com.example.demo.services.DataSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DataSourceController {

    private final DataSourceService dataSourceService;

    @PostMapping("/add-datasource")
    public void addDataSource(@RequestBody DatasourceDto dto) {
        dataSourceService.addDatasource(dto);
    }

    @PostMapping("/add-datasources")
    public void addDataSources(@RequestBody List<DatasourceDto> dtos) {
        dataSourceService.addDatasources(dtos);
    }

    @PostMapping("/ping-all")
    public Collection<DatabasePingResultDto> pingAll() {
        return dataSourceService.pingAll();
    }
}
