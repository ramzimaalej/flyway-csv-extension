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

import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.resource.Resource;

public class CSVMigrationMetadata {
    private final MigrationVersion migrationVersion;
    private final String tableName;
    private final String description;
    private final Resource res;

    public CSVMigrationMetadata(MigrationVersion migrationVersion,
                                String tableName,
                                String description,
                                Resource res) {
        this.migrationVersion = migrationVersion;
        this.tableName = tableName;
        this.description = description;
        this.res = res;
    }

    public MigrationVersion getMigrationVersion() {
        return migrationVersion;
    }

    public String getTableName() {
        return tableName;
    }

    public String getDescription() {
        return description;
    }

    public Resource getResource() {
        return this.res;
    }

    public Integer getEquivalentChecksum() {
        return null;
    }
}
