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
package com.mytechden.api.migration;

import com.mytechden.impl.executor.CSVMigrationExecutor;
import org.flywaydb.core.api.MigrationType;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.executor.MigrationExecutor;
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.api.resource.LoadableResource;
import org.flywaydb.core.internal.resolver.ChecksumCalculator;

import java.util.Objects;

public class CSVResolvedMigration implements CSVMigration {
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
    public void migrate(Context context) throws Exception {

    }

    @Override
    public boolean checksumMatches(Integer checksum) {
        return Objects.equals(checksum, this.checksum);
    }

    @Override
    public boolean checksumMatchesWithoutBeingIdentical(Integer checksum) {
        return Objects.equals(checksum, null) && !Objects.equals(checksum, this.checksum);
    }
}
