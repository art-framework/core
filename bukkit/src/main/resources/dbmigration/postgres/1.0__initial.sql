-- apply changes
create table art_metadata_store (
  key                           varchar(255) not null,
  value                         varchar(255),
  constraint pk_art_metadata_store primary key (key)
);

