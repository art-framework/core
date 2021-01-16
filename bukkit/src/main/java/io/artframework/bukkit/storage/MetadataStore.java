package io.artframework.bukkit.storage;

import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "art_metadata_store")
public class MetadataStore {

    public static final Finder<String, MetadataStore> find = new Finder<>(MetadataStore.class);

    @Id
    private String key;

    private String value;

    public MetadataStore(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
