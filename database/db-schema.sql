CREATE TABLE public.location_event
(
    client_id bigint NOT NULL,
    device_id bigint NOT NULL,
    model character varying(100) COLLATE pg_catalog."default",
    dt timestamp without time zone NOT NULL,
    location_element point,
    CONSTRAINT location_event_pkey PRIMARY KEY (client_id, device_id, dt)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;