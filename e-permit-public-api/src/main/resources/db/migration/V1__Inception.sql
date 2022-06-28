CREATE TABLE public.epermit_authorities (
    id uuid NOT NULL,
    api_secret character varying(255),
    api_uri character varying(255) NOT NULL,
    authentication_type character varying(255) NOT NULL,
    code character varying(255) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    name character varying(255) NOT NULL,
    updated_at timestamp without time zone NOT NULL
);

CREATE TABLE public.epermit_created_events (
    id uuid NOT NULL,
    created_at timestamp without time zone NOT NULL,
    event_id character varying(255) NOT NULL,
    result character varying(255),
    sended boolean NOT NULL
);

CREATE TABLE public.epermit_keys (
    id uuid NOT NULL,
    created_at timestamp without time zone NOT NULL,
    deleted boolean NOT NULL,
    enabled boolean NOT NULL,
    key_id character varying(255) NOT NULL,
    private_jwk character varying(4000) NOT NULL,
    salt character varying(255) NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


CREATE TABLE public.epermit_ledger_events (
    id uuid NOT NULL,
    consumer character varying(255) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    event_content character varying(10000) NOT NULL,
    event_id character varying(255) NOT NULL,
    event_timestamp bigint NOT NULL,
    event_type character varying(255) NOT NULL,
    previous_event_id character varying(255) NOT NULL,
    producer character varying(255) NOT NULL,
    proof character varying(1000) NOT NULL
);


CREATE TABLE public.epermit_ledger_permit_acts (
    id uuid NOT NULL,
    activity_details character varying(255),
    activity_timestamp bigint NOT NULL,
    activity_type character varying(255) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    ledger_permit_id uuid
);

CREATE TABLE public.epermit_ledger_permits (
    id uuid NOT NULL,
    company_id character varying(100) NOT NULL,
    company_name character varying(200) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    deleted boolean NOT NULL,
    expire_at character varying(255) NOT NULL,
    issued_at character varying(255) NOT NULL,
    issued_for character varying(255) NOT NULL,
    issuer character varying(255) NOT NULL,
    other_claims character varying(255),
    permit_id character varying(255) NOT NULL,
    permit_type character varying(255) NOT NULL,
    permit_year integer NOT NULL,
    plate_number character varying(255) NOT NULL,
    serial_number integer NOT NULL,
    used boolean NOT NULL
);


CREATE TABLE public.epermit_ledger_public_keys (
    id uuid NOT NULL,
    authority_code character varying(255) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    jwk character varying(5000) NOT NULL,
    key_id character varying(255) NOT NULL,
    revoked boolean NOT NULL,
    revoked_at bigint
);


CREATE TABLE public.epermit_ledger_quotas (
    id uuid NOT NULL,
    active boolean NOT NULL,
    created_at timestamp without time zone NOT NULL,
    end_number integer NOT NULL,
    permit_issued_for character varying(255) NOT NULL,
    permit_issuer character varying(255) NOT NULL,
    permit_type character varying(255) NOT NULL,
    permit_year integer NOT NULL,
    start_number integer NOT NULL
);


CREATE TABLE public.epermit_serial_numbers (
    id uuid NOT NULL,
    authority_code character varying(255) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    permit_type character varying(255) NOT NULL,
    permit_year integer NOT NULL,
    serial_number integer NOT NULL,
    state character varying(255) NOT NULL
);

ALTER TABLE ONLY public.epermit_authorities
    ADD CONSTRAINT epermit_authorities_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.epermit_created_events
    ADD CONSTRAINT epermit_created_events_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.epermit_keys
    ADD CONSTRAINT epermit_keys_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.epermit_ledger_events
    ADD CONSTRAINT epermit_ledger_events_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.epermit_ledger_permit_acts
    ADD CONSTRAINT epermit_ledger_permit_acts_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.epermit_ledger_permits
    ADD CONSTRAINT epermit_ledger_permits_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.epermit_ledger_public_keys
    ADD CONSTRAINT epermit_ledger_public_keys_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.epermit_ledger_quotas
    ADD CONSTRAINT epermit_ledger_quotas_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.epermit_serial_numbers
    ADD CONSTRAINT epermit_serial_numbers_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.epermit_ledger_permit_acts
    ADD CONSTRAINT fkk6245uf9t2hvkpx2ckvq6hx04 FOREIGN KEY (ledger_permit_id) REFERENCES public.epermit_ledger_permits(id);

