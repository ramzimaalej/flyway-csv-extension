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
import org.flywaydb.core.api.migration.Context;
import org.flywaydb.core.api.resolver.ResolvedMigration;

public interface CSVMigration extends ResolvedMigration {
    /**
     * @return The version of the schema after the migration is complete. {@code null} for repeatable migrations.
     */
    MigrationVersion getVersion();

    /**
     * @return The description of this migration for the migration history. Never {@code null}.
     */
    String getDescription();

    /**
     * @return The table name of this migration for the migration history. Never {@code null}.
     */
    String getTableName();

    /**
     * @return The table name of this migration for the migration history. Never {@code null}.
     */
    String getPhysicalLocation();

    /**
     * @return The checksum of this migration.
     */
    Integer getChecksum();

    /**
     * Whether this is an undo migration for a previously applied versioned migration.
     *
     * @return {@code true} if it is, {@code false} if not. Always {@code false} for repeatable migrations.
     */
    boolean isUndo();

    /**
     * Whether this is a baseline migration.
     *
     * @return {@code true} if it is, {@code false} if not.
     */
    boolean isBaselineMigration();

    /**
     * Whether the execution should take place inside a transaction. Almost all implementations should return {@code true}.
     * This however makes it possible to execute certain migrations outside a transaction. This is useful for databases
     * like PostgreSQL and SQL Server where certain statement can only execute outside a transaction.
     *
     * @return {@code true} if a transaction should be used (highly recommended), or {@code false} if not.
     */
    boolean canExecuteInTransaction();

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it and the canExecuteInTransaction returns {@code true}.
     *
     * @param context The context relevant for this migration, containing things like the JDBC connection to use and the
     * current Flyway configuration.
     * @throws Exception when the migration failed.
     */
    void migrate(Context context) throws Exception;
}
