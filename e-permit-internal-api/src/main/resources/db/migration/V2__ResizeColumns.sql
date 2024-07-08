alter table epermit_authorities
    alter column public_api_uri type varchar(1000) using public_api_uri::varchar(1000);

alter table epermit_authority_keys
    alter column jwk type varchar(4000) using jwk::varchar(4000);

alter table epermit_created_events
    alter column error type varchar(1000) using error::varchar(1000);

alter table epermit_keys
    alter column jwk type varchar(4000) using jwk::varchar(4000);

alter table epermit_ledger_permit_acts
    alter column activity_details type varchar(1000) using activity_details::varchar(1000);

alter table epermit_ledger_permits
    alter column company_id type varchar(255) using company_id::varchar(255);

alter table epermit_ledger_permits
    alter column company_name type varchar(500) using company_name::varchar(500);

alter table epermit_ledger_permits
    alter column other_claims type varchar(5000) using other_claims::varchar(5000);

alter table epermit_ledger_quotas
    alter column events type varchar(1000) using events::varchar(1000);

