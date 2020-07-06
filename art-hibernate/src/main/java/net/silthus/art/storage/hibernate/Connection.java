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

package net.silthus.art.storage.hibernate;

import com.netflix.governator.annotations.Configuration;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.inject.Singleton;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Getter
@Singleton
public class Connection {

    @Configuration(value = "hibernate_settings")
    private final Map<String, String> hibernateSettings = new HashMap<>();
    private final Set<String> classNames = new HashSet<>();

    private SessionFactory sessionFactory;

    private final Logger logger = Logger.getLogger("ART-Hibernate");

    private void constructSessionFactory() {
        getLogger().info("Reconstructing session factory.");
        StandardServiceRegistryBuilder standardRegistryBuilder = new StandardServiceRegistryBuilder();
        standardRegistryBuilder.applySettings(getHibernateSettings());

        getLogger().info("Using hibernate settings: " + getHibernateSettings());

        StandardServiceRegistry standardRegistry = standardRegistryBuilder.build();
        MetadataSources sources = new MetadataSources(standardRegistry);
        classNames.forEach(sources::addAnnotatedClassName);

        MetadataBuilder metadataBuilder = sources.getMetadataBuilder();
        metadataBuilder.applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE);
        //metadataBuilder.applyImplicitSchemaName("rake");
        Metadata metadata = metadataBuilder.build();
        SessionFactoryBuilder sessionFactoryBuilder = metadata.getSessionFactoryBuilder();
        sessionFactory = sessionFactoryBuilder.build();
    }

    public Session getEntityManager() {
        return getSessionFactory().openSession();
    }

    public void withEntityManager(Consumer<Session> consumer) {
        try (Session en = getEntityManager()) {
            consumer.accept(en);
        }
    }

    public <T> T withEntityManager(Function<Session, T> consumer) {
        try (Session en = getEntityManager()) {
            return consumer.apply(en);
        }
    }

    public void addClassesByName(String... classNames) {
        addClassesByName(Arrays.asList(classNames));
    }

    public void addClassesByName(Collection<String> classNames) {
        this.classNames.addAll(classNames);
        constructSessionFactory();
    }

    public void addClasses(Class<?>... classes) {
        addClasses(Arrays.asList(classes));
    }

    public void addClasses(Collection<Class<?>> classes) {
        addClassesByName(classes.stream().map(Class::getName).collect(Collectors.toList()));
    }
}
