create table epermit_authorities (
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    id uuid not null,
    code varchar(255) not null unique,
    name varchar(255) not null,
    public_api_uri varchar(255) not null,
    primary key (id)
);

create table epermit_authority_keys (
    revoked boolean not null,
    created_at timestamp(6) not null,
    revoked_at bigint,
    authority_id uuid,
    id uuid not null,
    jwk varchar(5000) not null,
    key_id varchar(255) not null,
    primary key (id)
);

create table epermit_created_events (
    sent boolean not null,
    created_at timestamp(6) not null,
    id uuid not null,
    error varchar(255),
    event_id varchar(255) not null unique,
    primary key (id)
);

create table epermit_keys (
    revoked boolean not null,
    created_at timestamp(6) not null,
    revoked_at bigint,
    id uuid not null,
    private_jwk varchar(4000) not null,
    jwk varchar(5000) not null,
    key_id varchar(255) not null unique,
    salt varchar(255) not null,
    primary key (id)
);

create table epermit_ledger_events (
    created_at timestamp(6) not null,
    event_timestamp bigint not null,
    id uuid not null,
    proof varchar(1000) not null,
    event_content varchar(10000) not null,
    consumer varchar(255) not null,
    event_id varchar(255) not null unique,
    event_type varchar(255) not null check (
        event_type in (
            'PERMIT_CREATED',
            'PERMIT_USED',
            'PERMIT_REVOKED',
            'QUOTA_CREATED',
            'KEY_CREATED',
            'KEY_REVOKED'
        )
    ),
    previous_event_id varchar(255) not null,
    producer varchar(255) not null,
    primary key (id)
);

create table epermit_ledger_permit_acts (
    activity_timestamp bigint not null,
    created_at timestamp(6) not null,
    id uuid not null,
    ledger_permit_id uuid,
    activity_details varchar(255),
    activity_type varchar(255) not null check (activity_type in ('ENTRANCE', 'EXIT')),
    primary key (id)
);

create table epermit_ledger_permits (
    permit_type integer not null,
    permit_year integer not null,
    revoked boolean not null,
    used boolean not null,
    created_at timestamp(6) not null,
    revoked_at bigint,
    arrival_country varchar(10) not null,
    departure_country varchar(10) not null,
    id uuid not null,
    company_id varchar(100) not null,
    company_name varchar(200) not null,
    qr_code varchar(5000) not null,
    expires_at varchar(255) not null,
    issued_at varchar(255) not null,
    issued_for varchar(255) not null,
    issuer varchar(255) not null,
    other_claims varchar(255),
    permit_id varchar(255) not null unique,
    plate_number varchar(255),
    plate_number2 varchar(255),
    primary key (id)
);

create table epermit_ledger_quotas (
    permit_type integer not null,
    permit_year integer not null,
    balance bigint not null,
    issued_count bigint not null,
    revoked_count bigint not null,
    created_at timestamp(6) not null,
    id uuid not null,
    events varchar(255) not null,
    permit_issued_for varchar(255) not null,
    permit_issuer varchar(255) not null,
    primary key (id)
);

create table epermit_serial_numbers (
    permit_type integer not null,
    permit_year integer not null,
    created_at timestamp(6) not null,
    next_serial bigint not null,
    id uuid not null,
    permit_issued_for varchar(255) not null,
    permit_issuer varchar(255) not null,
    primary key (id)
);

alter table
    if exists epermit_authority_keys
add
    constraint FKg8khfcvvxkgbcceulq4ji92wp foreign key (authority_id) references epermit_authorities;

alter table
    if exists epermit_ledger_permit_acts
add
    constraint FKolfcj4yeybykvnbsh32pv3ovc foreign key (ledger_permit_id) references epermit_ledger_permits;