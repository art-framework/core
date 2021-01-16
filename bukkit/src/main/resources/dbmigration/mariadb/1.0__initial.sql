-- apply changes
create table art_metadata_store (
  meta_key                           varchar(255) not null,
  meta_value                         varchar(255),
  constraint pk_art_metadata_store primary key (meta_key)
);

