package io.artframework.bukkit.storage;

import io.artframework.ArtObjectContext;
import io.artframework.Target;
import io.ebean.Finder;
import io.ebean.annotation.Index;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@Entity
@Accessors(fluent = true)
@Table(name = "art_metadata_store")
public class MetadataStore extends BaseEntity {

    public static final Finder<UUID, MetadataStore> find = new Finder<>(MetadataStore.class);

    public static Optional<MetadataStore> find(String key) {

        return find.query().where()
                .isNull("context")
                .and().isNull("context_type")
                .and().isNull("cache_key")
                .and().isNull("target")
                .and().eq("meta_key", key)
                .findOneOrEmpty();
    }

    public static Optional<MetadataStore> find(Target<?> target, String key) {

        return find.query().where()
                .isNull("context")
                .and().isNull("context_type")
                .and().isNull("cache_key")
                .eq("target", target.uniqueId())
                .and().eq("meta_key", key)
                .findOneOrEmpty();
    }

    public static Optional<MetadataStore> find(ArtObjectContext<?> context, Target<?> target, String key) {

        return find.query().where()
                .eq("context", context.uniqueId())
                .and().eq("context_type", context.meta().artObjectClass().getCanonicalName())
                .and().eq("cache_key", context.storageKey())
                .and().eq("target", target.uniqueId())
                .and().eq("meta_key", key)
                .findOneOrEmpty();
    }

    @Index
    private String context;
    @Index
    private String contextType;
    @Index
    private String cacheKey;
    @Index
    private String target;
    @Index
    private String metaKey;
    private String metaValue;

    public MetadataStore(String metaKey, String value) {
        this.metaKey = metaKey;
        this.metaValue = value;
    }
}
