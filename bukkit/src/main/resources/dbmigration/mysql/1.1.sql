-- apply changes
alter table art_metadata_store add column context_type varchar(255);

create index ix_art_metadata_store_context_type on art_metadata_store (context_type);
