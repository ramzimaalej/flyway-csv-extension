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
import org.flywaydb.core.api.resource.LoadableResource;
import org.flywaydb.core.internal.util.Pair;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import java.util.regex.Pattern;

public class MigrationInfoHelper {
    private final static Pattern UUID_REGEX_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    public static CSVMigrationMetadata getCSVMigrationMetadata(Pair<MigrationVersion, String> migrationInfoPair, LoadableResource res) {
        String rawDescription = migrationInfoPair.getRight();
        // ResourceNameParser has a built-in function that replaces __ to whitespace
        String[] descriptionParts = rawDescription.split("  ");
        MigrationVersion migrationVersion = migrationInfoPair.getLeft();
        if (descriptionParts.length > 1) {
            return new CSVMigrationMetadata(migrationVersion, descriptionParts[0].replace(" ", "_"), descriptionParts[1], res);
        }
        return new CSVMigrationMetadata(migrationVersion, null, rawDescription, res);
    }

    public static boolean isValidUUID(String str) {
        if (null == str) {
            return false;
        }
        return UUID_REGEX_PATTERN.matcher(str).matches();
    }

    public static byte[] uuidToBytes(UUID value) {
        byte[] uuidBytes = new byte[16];
        ByteBuffer.wrap(uuidBytes)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(value.getMostSignificantBits())
                .putLong(value.getLeastSignificantBits());
        return uuidBytes;
    }

}
