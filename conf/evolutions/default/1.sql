# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table abstracted_action_event (
  id                        bigint auto_increment not null,
  user_uuid                 varchar(255),
  client_event_id           bigint,
  date                      datetime(6),
  event_json                TEXT,
  logical_category_list_id  bigint,
  regex_matcher_id          bigint,
  message_statistics_id     bigint,
  constraint pk_abstracted_action_event primary key (id))
;

create table category_base_list (
  baselist_id               bigint auto_increment not null,
  baselist_name             varchar(255),
  description               varchar(255),
  uploaded_filename         varchar(255),
  constraint pk_category_base_list primary key (baselist_id))
;

create table config_model (
  id                        bigint auto_increment not null,
  data                      TEXT,
  constraint pk_config_model primary key (id))
;

create table event (
  id                        bigint auto_increment not null,
  type                      varchar(255),
  timestamp                 bigint,
  user_uuid                 varchar(255),
  keyboard_state_uuid       varchar(255),
  hand_posture              varchar(255),
  input_mode                varchar(255),
  field_package_name        varchar(255),
  field_id                  integer,
  field_hint_text           varchar(255),
  anonymized                tinyint(1) default 0,
  auto_correct_before       varchar(255),
  auto_correct_after        varchar(255),
  auto_correct_before_length integer,
  auto_correct_after_length integer,
  auto_correct_offset       integer,
  auto_correct_levenshtein_distance integer,
  content                   varchar(255),
  content_length            integer,
  suggestion                varchar(255),
  suggestion_length         varchar(255),
  private_mode              tinyint(1) default 0,
  cause                     varchar(255),
  code                      varchar(255),
  x                         integer,
  y                         integer,
  pressure                  double,
  size                      double,
  constraint pk_event primary key (id))
;

create table keyboard_definition (
  id                        bigint auto_increment not null,
  layout_layout_id          bigint,
  locale                    varchar(255),
  keyboard_rows             TEXT,
  definition_type           varchar(255),
  constraint pk_keyboard_definition primary key (id))
;

create table keyboard_layout (
  layout_id                 bigint auto_increment not null,
  name                      varchar(255),
  is_tracking_enabled       tinyint(1) default 0,
  show_hand_prompt          tinyint(1) default 0,
  info_title                varchar(255),
  info_message              varchar(255),
  constraint pk_keyboard_layout primary key (layout_id))
;

create table keyboard_state (
  id                        bigint auto_increment not null,
  uuid                      varchar(255),
  timestamp                 bigint,
  orientation               varchar(255),
  locale                    varchar(255),
  height                    integer,
  width                     integer,
  layout_id                 varchar(255),
  constraint pk_keyboard_state primary key (id))
;

create table keyboard_user_config (
  id                        bigint auto_increment not null,
  user_uuid                 varchar(190),
  start_date                bigint,
  has_sent_layout_message   tinyint(1) default 0,
  layout_layout_id          bigint,
  constraint pk_keyboard_user_config primary key (id))
;

create table logical_category_list (
  logicallist_id            bigint auto_increment not null,
  logicallist_name          varchar(255),
  description               varchar(255),
  preappy_lemma_extraction  tinyint(1) default 0,
  active                    tinyint(1) default 0,
  constraint pk_logical_category_list primary key (logicallist_id))
;

create table logical_word_list (
  logicallist_id            bigint auto_increment not null,
  logicallist_name          varchar(255),
  description               varchar(255),
  preappy_lemma_extraction  tinyint(1) default 0,
  active                    tinyint(1) default 0,
  constraint pk_logical_word_list primary key (logicallist_id))
;

create table message (
  id                        bigint auto_increment not null,
  title                     TEXT,
  message                   TEXT,
  timestamp                 bigint,
  is_user_message           tinyint(1) default 0,
  user_id                   varchar(255),
  is_new                    tinyint(1) default 0,
  constraint pk_message primary key (id))
;

create table message_statistics (
  id                        bigint auto_increment not null,
  client_event_id           bigint,
  user_uuid                 varchar(255),
  character_count_added     integer,
  character_count_altered   integer,
  character_count_submitted integer,
  input_target_app          varchar(255),
  timestamp_type_start      bigint,
  timestamp_type_end        bigint,
  field_hint_text           varchar(1024),
  constraint pk_message_statistics primary key (id))
;

create table pattern_matcher_config (
  regex_matcher_id          bigint auto_increment not null,
  regex_matcher_name        varchar(255),
  log_raw_content           tinyint(1) default 0,
  regex                     varchar(255),
  is_active                 tinyint(1) default 0,
  constraint pk_pattern_matcher_config primary key (regex_matcher_id))
;

create table sensor (
  id                        bigint auto_increment not null,
  event_id                  bigint,
  type                      integer,
  value0                    float,
  value1                    float,
  value2                    float,
  value3                    float,
  value4                    float,
  value5                    float,
  constraint pk_sensor primary key (id))
;

create table user (
  uuid                      varchar(190) not null,
  gender                    varchar(255),
  age                       integer,
  registration_timestamp    bigint,
  device_manufacturer       varchar(255),
  device_model              varchar(255),
  os_version                varchar(255),
  device_screen_width       integer,
  device_screen_height      integer,
  push_token                TEXT,
  constraint pk_user primary key (uuid))
;

create table word (
  id                        bigint auto_increment not null,
  word                      varchar(255),
  word_base_list_baselist_id bigint,
  constraint pk_word primary key (id))
;

create table word2category_mapping (
  id                        bigint auto_increment not null,
  word                      varchar(255),
  category                  varchar(255),
  category_base_list_baselist_id bigint,
  constraint pk_word2category_mapping primary key (id))
;

create table word_base_list (
  baselist_id               bigint auto_increment not null,
  baselist_name             varchar(255),
  description               varchar(255),
  uploaded_filename         varchar(255),
  constraint pk_word_base_list primary key (baselist_id))
;

create table word_frequency (
  id                        bigint auto_increment not null,
  user_uuid                 varchar(190),
  word                      varchar(190),
  count                     integer,
  logical_word_list_id      bigint,
  constraint uq_word_frequency_1 unique (word,user_uuid,logical_word_list_id),
  constraint pk_word_frequency primary key (id))
;


create table logical_category_list_category_base_list (
  logical_category_list_logicallist_id bigint not null,
  category_base_list_baselist_id bigint not null,
  constraint pk_logical_category_list_category_base_list primary key (logical_category_list_logicallist_id, category_base_list_baselist_id))
;

create table logical_word_list_word_base_list (
  logical_word_list_logicallist_id bigint not null,
  word_base_list_baselist_id     bigint not null,
  constraint pk_logical_word_list_word_base_list primary key (logical_word_list_logicallist_id, word_base_list_baselist_id))
;
alter table keyboard_definition add constraint fk_keyboard_definition_layout_1 foreign key (layout_layout_id) references keyboard_layout (layout_id) on delete restrict on update restrict;
create index ix_keyboard_definition_layout_1 on keyboard_definition (layout_layout_id);
alter table keyboard_user_config add constraint fk_keyboard_user_config_user_2 foreign key (user_uuid) references user (uuid) on delete restrict on update restrict;
create index ix_keyboard_user_config_user_2 on keyboard_user_config (user_uuid);
alter table keyboard_user_config add constraint fk_keyboard_user_config_layout_3 foreign key (layout_layout_id) references keyboard_layout (layout_id) on delete restrict on update restrict;
create index ix_keyboard_user_config_layout_3 on keyboard_user_config (layout_layout_id);
alter table sensor add constraint fk_sensor_event_4 foreign key (event_id) references event (id) on delete restrict on update restrict;
create index ix_sensor_event_4 on sensor (event_id);
alter table word add constraint fk_word_wordBaseList_5 foreign key (word_base_list_baselist_id) references word_base_list (baselist_id) on delete restrict on update restrict;
create index ix_word_wordBaseList_5 on word (word_base_list_baselist_id);
alter table word2category_mapping add constraint fk_word2category_mapping_categoryBaseList_6 foreign key (category_base_list_baselist_id) references category_base_list (baselist_id) on delete restrict on update restrict;
create index ix_word2category_mapping_categoryBaseList_6 on word2category_mapping (category_base_list_baselist_id);



alter table logical_category_list_category_base_list add constraint fk_logical_category_list_category_base_list_logical_category__01 foreign key (logical_category_list_logicallist_id) references logical_category_list (logicallist_id) on delete restrict on update restrict;

alter table logical_category_list_category_base_list add constraint fk_logical_category_list_category_base_list_category_base_lis_02 foreign key (category_base_list_baselist_id) references category_base_list (baselist_id) on delete restrict on update restrict;

alter table logical_word_list_word_base_list add constraint fk_logical_word_list_word_base_list_logical_word_list_01 foreign key (logical_word_list_logicallist_id) references logical_word_list (logicallist_id) on delete restrict on update restrict;

alter table logical_word_list_word_base_list add constraint fk_logical_word_list_word_base_list_word_base_list_02 foreign key (word_base_list_baselist_id) references word_base_list (baselist_id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table abstracted_action_event;

drop table category_base_list;

drop table config_model;

drop table event;

drop table keyboard_definition;

drop table keyboard_layout;

drop table keyboard_state;

drop table keyboard_user_config;

drop table logical_category_list;

drop table logical_category_list_category_base_list;

drop table logical_word_list;

drop table logical_word_list_word_base_list;

drop table message;

drop table message_statistics;

drop table pattern_matcher_config;

drop table sensor;

drop table user;

drop table word;

drop table word2category_mapping;

drop table word_base_list;

drop table word_frequency;

SET FOREIGN_KEY_CHECKS=1;

