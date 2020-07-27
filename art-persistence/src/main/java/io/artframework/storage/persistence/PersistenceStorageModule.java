/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.artframework.storage.persistence;

import io.artframework.Configurable;
import io.artframework.Configuration;
import io.artframework.Storage;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.datasource.DataSourceConfig;

import java.util.HashMap;
import java.util.Map;

public class PersistenceStorageModule implements Configurable<DatabaseConfig> {

    private static final Map<String, String> DRIVER_MAP = new HashMap<>();

    static {
        DRIVER_MAP.put("h2", "org.h2.Driver");
        DRIVER_MAP.put("mysql", "com.mysql.jdbc.Driver");
        DRIVER_MAP.put("postgres", "org.postgresql.Driver");
        DRIVER_MAP.put("mariadb", "org.mariadb.jdbc.Driver");
        DRIVER_MAP.put("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    private DatabaseConfig databaseConfig;
    private Database database;

    public void onEnable(Configuration configuration) {
        if (databaseConfig == null) return;

        database = createDatabase(databaseConfig);
        configuration.storage(new PersistenceStorage(configuration, database));
    }

    public void onDisable(Configuration configuration) {
        configuration.storage(Storage.of(configuration));
        database = null;
    }

    @Override
    public void load(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    private Database createDatabase(DatabaseConfig config) {

        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        io.ebean.config.DatabaseConfig serverConfig = new io.ebean.config.DatabaseConfig();
        serverConfig.setName("art");
        // load configuration from ebean.properties
        serverConfig.loadFromProperties();
        serverConfig.setDefaultServer(true);
        serverConfig.setRegister(true);

        if (config != null && DRIVER_MAP.get(config.getPlatform()) != null) {
            DataSourceConfig dataSourceConfig = new DataSourceConfig();
            dataSourceConfig.setUsername(config.getUsername());
            dataSourceConfig.setPassword(config.getPassword());
            dataSourceConfig.setUrl(config.getUrl());
            dataSourceConfig.setDriver(DRIVER_MAP.get(config.getPlatform()));

            serverConfig.setDataSourceConfig(dataSourceConfig);
        }

        Database database = DatabaseFactory.create(serverConfig);

        Thread.currentThread().setContextClassLoader(originalContextClassLoader);

        return database;
    }
}
