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

package net.silthus.art.storage.persistence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.config.ServerConfig;
import io.ebean.datasource.DataSourceConfig;

import java.util.HashMap;
import java.util.Map;

public class EbeanServerProvider implements Provider<EbeanServer> {

    private static final Map<String, String> DRIVER_MAP = new HashMap<>();

    static {
        DRIVER_MAP.put("h2", "org.h2.Driver");
        DRIVER_MAP.put("mysql", "com.mysql.jdbc.Driver");
        DRIVER_MAP.put("postgres", "org.postgresql.Driver");
        DRIVER_MAP.put("mariadb", "org.mariadb.jdbc.Driver");
        DRIVER_MAP.put("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    @Inject(optional = true)
    private ArtConfiguration config;
    @Inject
    @Named("SPIGOT_CLASSLOADER")
    private ClassLoader spigotClassLoader;

    @Override
    public EbeanServer get() {

        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(spigotClassLoader);

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setName("art");
        // load configuration from ebean.properties
        serverConfig.loadFromProperties();
        serverConfig.setDefaultServer(true);
        serverConfig.setRegister(true);

        if (config != null && DRIVER_MAP.get(config.getDatabase().getPlatform()) != null) {
            DataSourceConfig dataSourceConfig = new DataSourceConfig();
            dataSourceConfig.setUsername(config.getDatabase().getUsername());
            dataSourceConfig.setPassword(config.getDatabase().getPassword());
            dataSourceConfig.setUrl(config.getDatabase().getUrl());
            dataSourceConfig.setDriver(DRIVER_MAP.get(config.getDatabase().getPlatform()));

            serverConfig.setDataSourceConfig(dataSourceConfig);
        }

        EbeanServer ebeanServer = EbeanServerFactory.create(serverConfig);

        Thread.currentThread().setContextClassLoader(originalContextClassLoader);

        return ebeanServer;
    }
}