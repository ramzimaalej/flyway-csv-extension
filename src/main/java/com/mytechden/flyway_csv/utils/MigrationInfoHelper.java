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
package com.mytechden.flyway_csv.utils;

import com.mytechden.flyway_csv.api.migration.CSVMigrationMetadata;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.internal.resource.Resource;
import org.flywaydb.core.internal.util.Pair;

public class MigrationInfoHelper {
    public static CSVMigrationMetadata getCSVMigrationMetadata(Pair<MigrationVersion, String> migrationInfoPair, Resource res) {
        String rawDescription = migrationInfoPair.getRight();
        // ResourceNameParser has a built-in function that replaces __ to whitespace
        String[] descriptionParts = rawDescription.split("  ");
        MigrationVersion migrationVersion = migrationInfoPair.getLeft();
        if (descriptionParts.length > 1) {
            return new CSVMigrationMetadata(migrationVersion, descriptionParts[0], descriptionParts[1], res);
        }
        return new CSVMigrationMetadata(migrationVersion, null, rawDescription, res);
    }
}
