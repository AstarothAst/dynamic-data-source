package com.example.demo.services;

import com.example.demo.dto.DatabasePingResultDto;
import com.example.demo.dto.DatasourceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class DataSourceServiceImpl implements DataSourceService {

    Map<String, JdbcTemplate> datasourceMap = new ConcurrentHashMap<>();

    @Override
    public void addDatasources(Collection<DatasourceDto> datasourceList) {
        datasourceList.forEach(this::addDatasource);
    }

    @Override
    public void addDatasource(DatasourceDto dto) {
        String key = createKey(dto);

        if (datasourceMap.containsKey(key)) {
            log.warn("Duplicate key '{}' already exist", key);
        } else {
            DataSource dataSource = createDataSource(dto);
            datasourceMap.put(key, new JdbcTemplate(dataSource));
        }
    }

    @Override
    public List<DatabasePingResultDto> pingAll() {
        return datasourceMap.entrySet().stream()
                .map(entry -> {
                    String databaseName = entry.getKey();
                    boolean pingResult = pingDatabase(entry.getValue());

                    return DatabasePingResultDto.builder()
                            .databaseName(databaseName)
                            .result(pingResult)
                            .build();
                }).toList();
    }

    private String createKey(DatasourceDto dto) {
        return String.format("%s:%s", dto.getHost(), dto.getPort());
    }

    private DataSource createDataSource(DatasourceDto dto) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(dto.getUrl());
        dataSource.setUsername(dto.getUser());
        dataSource.setPassword(dto.getPassword());
        return dataSource;
    }

    private boolean pingDatabase(JdbcTemplate jdbcTemplate) {
        try {
            jdbcTemplate.execute("select 1");
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }
}
