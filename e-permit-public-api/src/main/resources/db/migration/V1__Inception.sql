create table epermit_authorities (
    id uuid not null,
    api_uri varchar(255) not null,
    code varchar(255) not null,
    created_at timestamp not null,
    name varchar(255) not null,
    updated_at timestamp not null,
    primary key (id)
);

create table epermit_authority_features (
    authority_id uuid not null,
    feature_id varchar(255) not null,
    primary key (authority_id, feature_id)
);

create table epermit_created_events (
    id uuid not null,
    created_at timestamp not null,
    event_id varchar(255) not null,
    result varchar(255),
    sended boolean not null,
    primary key (id)
);

create table epermit_features (
    id varchar(255) not null,
    created_at timestamp not null,
    description varchar(255) not null,
    title varchar(255) not null,
    primary key (id)
);

create table epermit_keys (
    id uuid not null,
    created_at timestamp not null,
    deleted boolean not null,
    enabled boolean not null,
    key_id varchar(255) not null,
    private_jwk varchar(4000) not null,
    salt varchar(255) not null,
    updated_at timestamp not null,
    primary key (id)
);

create table epermit_ledger_events (
    id uuid not null,
    consumer varchar(255) not null,
    created_at timestamp not null,
    event_content varchar(10000) not null,
    event_id varchar(255) not null,
    event_timestamp int8 not null,
    event_type varchar(255) not null,
    previous_event_id varchar(255) not null,
    producer varchar(255) not null,
    proof varchar(1000) not null,
    primary key (id)
);

create table epermit_ledger_permit_acts (
    id uuid not null,
    activity_details varchar(255),
    activity_timestamp int8 not null,
    activity_type varchar(255) not null,
    created_at timestamp not null,
    ledger_permit_id uuid,
    primary key (id)
);

create table epermit_ledger_permits (
    id uuid not null,
    company_id varchar(100) not null,
    company_name varchar(200) not null,
    created_at timestamp not null,
    deleted boolean not null,
    expire_at varchar(255) not null,
    issued_at varchar(255) not null,
    issued_for varchar(255) not null,
    issuer varchar(255) not null,
    other_claims varchar(255),
    permit_id varchar(255) not null,
    permit_type varchar(255) not null,
    permit_year int4 not null,
    plate_number varchar(255) not null,
    qr_code varchar(5000) not null,
    serial_number int4 not null,
    used boolean not null,
    primary key (id)
);

create table epermit_ledger_public_keys (
    id uuid not null,
    authority_code varchar(255) not null,
    created_at timestamp not null,
    jwk varchar(5000) not null,
    key_id varchar(255) not null,
    revoked boolean not null,
    revoked_at int8,
    primary key (id)
);

create table epermit_ledger_quotas (
    id uuid not null,
    active boolean not null,
    created_at timestamp not null,
    end_number int4 not null,
    permit_issued_for varchar(255) not null,
    permit_issuer varchar(255) not null,
    permit_type varchar(255) not null,
    permit_year int4 not null,
    start_number int4 not null,
    primary key (id)
);

create table epermit_serial_numbers (
    id uuid not null,
    authority_code varchar(255) not null,
    created_at timestamp not null,
    permit_type varchar(255) not null,
    permit_year int4 not null,
    serial_number int4 not null,
    state varchar(255) not null,
    primary key (id)
);

alter table
    if exists epermit_created_events
add
    constraint UK_8efluvxhvs8fwwtmsp2fb7h6e unique (event_id);

alter table
    if exists epermit_keys
add
    constraint UK_abebibyo1ntpll6liaobj18kd unique (key_id);

alter table
    if exists epermit_ledger_public_keys
add
    constraint UKkm69ocpvvqk5wi2gbalmam101 unique (authority_code, key_id);

alter table
    if exists epermit_authority_features
add
    constraint FKj85rljrwdfgpjnamsbubo7s8m foreign key (feature_id) references epermit_features;

alter table
    if exists epermit_authority_features
add
    constraint FKceg100m1fhrwsyt4088wka3x7 foreign key (authority_id) references epermit_authorities;

alter table
    if exists epermit_ledger_permit_acts
add
    constraint FKolfcj4yeybykvnbsh32pv3ovc foreign key (ledger_permit_id) references epermit_ledger_permits;