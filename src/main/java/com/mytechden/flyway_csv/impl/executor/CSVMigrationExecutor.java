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
package com.mytechden.flyway_csv.impl.executor;

import com.mytechden.flyway_csv.api.migration.CSVMigration;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.executor.Context;
import org.flywaydb.core.api.executor.MigrationExecutor;
import org.flywaydb.core.internal.database.DatabaseExecutionStrategy;
import org.flywaydb.core.internal.database.DatabaseType;
import org.flywaydb.core.internal.database.DatabaseTypeRegister;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Adapter for executing migrations implementing CSV Migration.
 */
public class CSVMigrationExecutor implements MigrationExecutor {
    /**
     * The CSV Migration to execute.
     */
    private final CSVMigration csvMigration;

    public CSVMigrationExecutor(CSVMigration csvMigration) {
        this.csvMigration = csvMigration;
    }

    @Override
    public void execute(final Context context) throws SQLException {
        DatabaseType databaseType = DatabaseTypeRegister.getDatabaseTypeForConnection(context.getConnection());
        DatabaseExecutionStrategy strategy = databaseType.createExecutionStrategy(context.getConnection());
        strategy.execute(() -> {
            this.executeOnce(context);
            return true;
        });
    }

    private void executeOnce(final Context context) throws SQLException {
        try {
            csvMigration.migrate(new org.flywaydb.core.api.migration.Context() {
                @Override
                public Configuration getConfiguration() {
                    return context.getConfiguration();
                }
                @Override
                public Connection getConnection() {
                    return context.getConnection();
                }
            });
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new FlywayException("Migration failed!", e);
        }
    }

    @Override
    public boolean canExecuteInTransaction() {
        return csvMigration.canExecuteInTransaction();
    }

    @Override
    public boolean shouldExecute() {
        return csvMigration.shouldExecute();
    }
}
