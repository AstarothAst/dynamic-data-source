package com.example.demo.services;

import com.example.demo.dto.DatabasePingResultDto;
import com.example.demo.dto.DatasourceDto;
import com.example.demo.dto.PingAllJobDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataSourceServiceImpl implements DataSourceService {

    Map<String, JdbcTemplate> datasourceMap = new ConcurrentHashMap<>();
    private boolean jobEnabled = false;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    public void job() {
        if (jobEnabled && !datasourceMap.isEmpty()) {
            runPingAllJob();
        }
    }

    @Override
    public void addDataSources(Collection<DatasourceDto> datasourceList) {
        datasourceList.forEach(this::addDataSource);
    }

    @Override
    public void addDataSource(DatasourceDto datasource) {
        String key = createKey(datasource);

        if (datasourceMap.containsKey(key)) {
            log.warn("Duplicate key '{}' already exist", key);
        } else {
            DataSource dataSource = createDataSource(datasource);
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

    @Override
    public void pingAllJob(PingAllJobDto dto) {
        this.jobEnabled = dto.isPingEnable();
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

    private void runPingAllJob() {
        List<String> unavailableDbList = pingAll().stream()
                .filter(dto -> !dto.isResult())
                .map(DatabasePingResultDto::getDatabaseName)
                .toList();

        if (!unavailableDbList.isEmpty()) {
            String msg = unavailableDbList.stream().collect(Collectors.joining(", ", "Unavailable DB:", ""));
            log.warn(msg);
        }
    }
}
