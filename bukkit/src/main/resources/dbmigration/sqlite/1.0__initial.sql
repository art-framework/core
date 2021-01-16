-- apply changes
create table art_metadata_store (
  id                            varchar(40) not null,
  context                       varchar(255),
  cache_key                     varchar(255),
  target                        varchar(255),
  meta_key                      varchar(255),
  meta_value                    varchar(255),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_art_metadata_store primary key (id)
);

create index ix_art_metadata_store_context on art_metadata_store (context);
create index ix_art_metadata_store_cache_key on art_metadata_store (cache_key);
create index ix_art_metadata_store_target on art_metadata_store (target);
create index ix_art_metadata_store_meta_key on art_metadata_store (meta_key);
