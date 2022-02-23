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
package com.mytechden.impl.resolver;

import com.mytechden.api.migration.CSVMigrationMetadata;
import com.mytechden.api.migration.CSVResolvedMigration;
import com.mytechden.utils.MigrationInfoHelper;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.resolver.Context;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import org.flywaydb.core.api.resource.LoadableResource;
import org.flywaydb.core.internal.resolver.ResolvedMigrationComparator;
import org.flywaydb.core.internal.resource.ResourceName;
import org.flywaydb.core.internal.resource.ResourceNameParser;
import org.flywaydb.core.internal.scanner.LocationScannerCache;
import org.flywaydb.core.internal.scanner.ResourceNameCache;
import org.flywaydb.core.internal.scanner.Scanner;
import org.flywaydb.core.internal.util.Pair;

import java.util.*;

import static java.util.Arrays.asList;

public class CSVMigrationResolver implements MigrationResolver {
    static private final String[] SUFFIXES = { ".csv", ".CSV" };
    protected final Map<String, List<String>> references = new HashMap<>();
    private final ResourceNameCache resourceNameCache;
    private final LocationScannerCache locationScannerCache;

    public CSVMigrationResolver() {
        this.resourceNameCache = new ResourceNameCache();
        this.locationScannerCache = new LocationScannerCache();
    }

    public List<ResolvedMigration> resolveMigrations(Context context) {
        Configuration configuration = context.getConfiguration();
        List<ResolvedMigration> migrations = new ArrayList<>();
        addMigrations(migrations, configuration, configuration.getSqlMigrationPrefix());
        migrations.sort(new ResolvedMigrationComparator());
        return migrations;
    }

    private void addMigrations(List<ResolvedMigration> migrations, Configuration configuration, String prefix) {
        ResourceNameParser resourceNameParser = new ResourceNameParser(configuration);
        for (LoadableResource resource : this.getResources(configuration, CSVMigrationResolver.SUFFIXES)) {
            String filename = resource.getFilename();
            ResourceName result = resourceNameParser.parse(filename);
            if (!result.isValid() || !prefix.equals(result.getPrefix())) {
                continue;
            }
            CSVMigrationMetadata csvMigrationMetadata = MigrationInfoHelper.getCSVMigrationMetadata(Pair.of(result.getVersion(), result.getDescription()), configuration.getSqlMigrationSeparator(), resource);
            migrations.add(new CSVResolvedMigration(csvMigrationMetadata));
        }
    }

    protected Collection<LoadableResource> getResources(Configuration cf, String... suffixes) {
        final Scanner scn = new Scanner<>(
                Void.class,
                asList(cf.getLocations()),
                cf.getClassLoader(),
                cf.getEncoding(),
                cf.isDetectEncoding(),
                false,
                resourceNameCache,
                locationScannerCache,
                cf.isFailOnMissingLocations());
        return scn.getResources(cf.getSqlMigrationPrefix(), suffixes);
    }
}
