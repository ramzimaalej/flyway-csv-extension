/*
 * Copyright (C) MyTechDen.com 2021-2022
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mytechden.flyway_csv.api.migration;

import com.mytechden.flyway_csv.impl.executor.CSVMigrationExecutor;
import com.mytechden.flyway_csv.utils.MigrationInfoHelper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationType;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.executor.MigrationExecutor;
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.internal.resolver.ChecksumCalculator;
import org.flywaydb.core.internal.resource.LoadableResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.util.stream.Collectors.joining;

public class CSVResolvedMigration implements CSVMigration {
    private static final Logger logger = LoggerFactory.getLogger(CSVResolvedMigration.class);
    public static final String SEPARATOR = "__";
    private final CSVMigrationMetadata csvMigrationMetadata;
    private final CSVMigrationExecutor executor;
    private final Integer checksum;

    /**
     * Creates a new instance of a CSV-based migration following Flyway's default naming convention.
     */
    public CSVResolvedMigration(CSVMigrationMetadata csvMigrationMetadata) {
        this.csvMigrationMetadata = csvMigrationMetadata;
        this.executor = new CSVMigrationExecutor(this);
        this.checksum = ChecksumCalculator.calculate((LoadableResource) this.csvMigrationMetadata.getResource());
    }

    public MigrationVersion getVersion() {
        return this.csvMigrationMetadata.getMigrationVersion();
    }

    public String getDescription() {
        return this.csvMigrationMetadata.getDescription();
    }

    public String getTableName() {
        return this.csvMigrationMetadata.getTableName();
    }

    @Override
    public String getPhysicalLocation() {
        return this.csvMigrationMetadata.getResource().getAbsolutePathOnDisk();
    }

    @Override
    public String getScript() {
        return this.csvMigrationMetadata.getResource().getRelativePath();
    }

    @Override
    public MigrationExecutor getExecutor() {
        return this.executor;
    }

    public Integer getChecksum() {
        return this.checksum;
    }

    @Override
    public MigrationType getType() {
        return MigrationType.JDBC;
    }

    public boolean isUndo() {
        return false;
    }

    public boolean isBaselineMigration() {
        return false;
    }

    public boolean canExecuteInTransaction() {
        return true;
    }

    @Override
    public void migrate(Context context) {
        try {
            Connection connection = context.getConnection();
            ResultSetMetaData columnsMetadata = this.getColumnsMetadata(connection, this.getTableName());
            final CSVParser parser = this.getCSVParser();
            final List<String> headers = parser.getHeaderNames();
            List<CSVRecord> records = parser.getRecords();
            final Map<String, Integer> columnTypes = this.getColumnTypes(columnsMetadata);
            logger.trace("CSV file: {}", this.csvMigrationMetadata.getResource().getFilename());
            logger.trace("CSV file headers: {}", String.join(", ", headers));
            logger.trace("CSV file total rows: {}", records.size());
            final Statement statement = this.getStatement(connection, headers, this.getTableName(), records, columnTypes);
            statement.executeBatch();
            logger.trace("Finished import CSV file");
        } catch (Exception e) {
            throw new FlywayException("Failed to run migration: " + this.getDescription(), e);
        }
    }

    @Override
    public boolean checksumMatches(Integer checksum) {
        return Objects.equals(checksum, this.checksum);
    }

    @Override
    public boolean checksumMatchesWithoutBeingIdentical(Integer checksum) {
        return Objects.equals(checksum, null) && !Objects.equals(checksum, this.checksum);
    }

    private ResultSetMetaData getColumnsMetadata(Connection connection, String tableName) {
        String sql = "SELECT * FROM " + tableName + " WHERE 1 = 2";
        logger.trace("Going to get columns metadata: {}", sql);
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            logger.trace("Finished getting metadata");
            return resultSet.getMetaData();
        } catch (SQLException e) {
            throw new FlywayException("Could not get the columns metadata", e);
        }
    }

    private CSVParser getCSVParser() {
        try {
            LoadableResource loadableResource = (LoadableResource) this.csvMigrationMetadata.getResource();
            return CSVFormat.RFC4180.builder().setHeader().setNullString("null").setSkipHeaderRecord(true).setIgnoreEmptyLines(true).build().parse(loadableResource.read());
        } catch (FileNotFoundException e) {
            throw new FlywayException("File not found: " + this.csvMigrationMetadata.getResource().getRelativePath(), e);
        } catch (IOException e) {
            throw new FlywayException("An error has occurred while reading a file: " + this.csvMigrationMetadata.getResource().getRelativePath(), e);
        }
    }

    private Map<String, Integer> getColumnTypes(ResultSetMetaData columnsMetadata) {
        try {
            Map<String, Integer> columnTypes = new HashMap<>();
            for (int index = 1; index <= columnsMetadata.getColumnCount(); index++) {
                columnTypes.put(columnsMetadata.getColumnName(index), columnsMetadata.getColumnType(index));
            }
            return columnTypes;
        } catch (SQLException e) {
            throw new FlywayException("An error has occurred while getting the column types", e);
        }
    }

    private Statement getStatement(Connection connection, List<String> columns, String tableName, List<CSVRecord> records, Map<String, Integer> getColumnTypes) {
        logger.trace("Columns metadata: {}", getColumnTypes);
        final String insertSQL = columns.stream()
                .map(String::trim)
                .collect(joining(", ", "INSERT INTO " + tableName + "(", ")"));
        final String valuesSQL = IntStream.range(0, columns.size())
                .mapToObj(n -> "?")
                .collect(joining(", ", "VALUES(", ")"));
        try {
            final PreparedStatement statement = connection.prepareStatement(insertSQL + valuesSQL, RETURN_GENERATED_KEYS);
            for (CSVRecord csvRecord : records) {
                for (int index = 0; index < columns.size(); index++) {
                    String columnValue = csvRecord.get(index);
                    if (columnValue != null) {
                        Integer columnType = getColumnTypes.get(columns.get(index));
                        logger.trace("Processing column: {}, value: {}, type: {}", columns.get(index), columnValue, columnType);
                        if (MigrationInfoHelper.isValidUUID(columnValue) && (Types.BINARY == columnType || Types.LONGVARBINARY == columnType || Types.VARBINARY == columnType)) {
                            statement.setObject(index + 1, MigrationInfoHelper.uuidToBytes(UUID.fromString(columnValue)), columnType);
                        } else {
                            statement.setObject(index + 1, columnValue, columnType);
                        }
                    } else {
                        logger.trace("Processing column: {}, value: {}, type: {}", columns.get(index), null, Types.NULL);
                        statement.setNull(index + 1, Types.NULL);
                    }
                }
                statement.addBatch();
            }
            return statement;
        } catch (SQLException e) {
            throw new FlywayException("An error has occurred while building the sql statement", e);
        }
    }
}
