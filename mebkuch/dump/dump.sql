--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: test; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA test;

--
-- Name: user_roles; Type: TYPE; Schema: test; Owner: postgres
--

CREATE TYPE test.user_roles AS ENUM (
    'SUPERADMIN',
    'ADMIN',
    'CLIENT'
);

--
-- Name: user_status; Type: TYPE; Schema: test; Owner: postgres
--

CREATE TYPE test.user_status AS ENUM (
    'ACTIVE',
    'BANNED',
    'SOFT_DELETED'
);

--
-- Name: fill_data_category_tree(); Type: PROCEDURE; Schema: test; Owner: postgres
--

CREATE PROCEDURE test.fill_data_category_tree()
    LANGUAGE plpgsql
    AS $$

BEGIN



WITH RECURSIVE tree AS (



    -- 1. каждая категория сама себе предок

    SELECT

        id AS ancestor_id,

        id AS descendant_id,

        0 AS depth,

        parent_id

    FROM category



    UNION ALL



    -- 2. поднимаемся вверх по дереву (к родителям)

    SELECT

        t.ancestor_id,

        c.id AS descendant_id,

        t.depth + 1,

        c.parent_id

    FROM tree t

             JOIN category c ON t.parent_id = c.id



)



INSERT INTO category_tree (ancestor_id, descendant_id, depth)

SELECT ancestor_id, descendant_id, depth

FROM tree

    ON CONFLICT (ancestor_id, descendant_id) DO NOTHING;



END;

$$;

--
-- Name: fill_data_product(integer, integer); Type: PROCEDURE; Schema: test; Owner: postgres
--

CREATE PROCEDURE test.fill_data_product(IN beg_ integer, IN end_ integer)
    LANGUAGE plpgsql
    AS $$

declare

category_id_rand int;

			product_type_id_rand int;

			section_id_rand int;

			status_id_rand int;

			style_id_rand int;

begin

for i in beg_..end_ loop

				category_id_rand = generate_rand_num('category');

				product_type_id_rand = generate_rand_num('product_type');

				status_id_rand = generate_rand_num('product_status');

				style_id_rand = generate_rand_num('product_style');



insert into product("name", category_id, product_type_id, status_id, style_id, description, min_price, discount, created_at)

values('product_name' || i::text, category_id_rand, product_type_id_rand, status_id_rand, style_id_rand, 'description' || i::text,  random(10000, 1000000), random(0, 30), CURRENT_TIMESTAMP - (random(1, 12) * INTERVAL '1 month') - (random(1, 30) * INTERVAL '1 day') );

end loop;

end;

	$$;

--
-- Name: fill_product_component_realistic(integer); Type: PROCEDURE; Schema: test; Owner: postgres
--

CREATE PROCEDURE test.fill_product_component_realistic(IN limit_rows integer DEFAULT 10000)
    LANGUAGE plpgsql
    AS $$

DECLARE

inserted_count INT := 0;

    affected INT;

    p RECORD;

    comp_count INT;

BEGIN



FOR p IN

SELECT id FROM product ORDER BY RANDOM()

    LOOP



        EXIT WHEN inserted_count >= limit_rows;



IF RANDOM() > 0.7 THEN

            CONTINUE;

END IF;



        comp_count := (RANDOM() * 4 + 1)::INT;



WITH selected_components AS (

    SELECT id

    FROM component

    ORDER BY RANDOM()

    LIMIT comp_count

    )

INSERT INTO product_component (product_id, component_id, quantity)

SELECT

    p.id,

    c.id,

    (RANDOM() * 5 + 1)::BIGINT

FROM selected_components c

    ON CONFLICT DO NOTHING;



GET DIAGNOSTICS affected = ROW_COUNT;

inserted_count := inserted_count + affected;



END LOOP;



END;

$$;

--
-- Name: generate_rand_num(text); Type: FUNCTION; Schema: test; Owner: postgres
--

CREATE FUNCTION test.generate_rand_num(table_name text) RETURNS bigint
    LANGUAGE plpgsql
    AS $$

declare

result_id bigint;

    row_count bigint;

    rnd_offset bigint;

begin

execute format('select count(*) from %I', table_name)

    into row_count;



rnd_offset := floor(random() * row_count);



execute format(

        'select id from %I offset %s limit 1',

        table_name, rnd_offset

        )

    into result_id;



return result_id;

end;

$$;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: category; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.category (
                               id bigint NOT NULL,
                               name character varying(150) NOT NULL,
                               parent_id bigint
);

--
-- Name: get_all_child_categories(bigint); Type: FUNCTION; Schema: test; Owner: postgres
--

CREATE FUNCTION test.get_all_child_categories(id_ bigint) RETURNS SETOF test.category
    LANGUAGE plpgsql
    AS $$

BEGIN



RETURN QUERY

SELECT c.*

FROM category_tree ct

         JOIN category c ON c.id = ct.descendant_id

WHERE ct.ancestor_id = id_

  AND ct.depth > 0; -- исключаем саму категорию



END;

$$;

--
-- Name: to_fill_data_category(integer, integer); Type: PROCEDURE; Schema: test; Owner: postgres
--

CREATE PROCEDURE test.to_fill_data_category(IN beg_ integer, IN end_ integer)
    LANGUAGE plpgsql
    AS $$

begin

for i in beg_..end_ loop

					insert into category("name", parent_id)

					values('category_name' || i::text, random(beg_, i));

end loop;

end;

		$$;

--
-- Name: to_fill_data_category(integer, integer, integer); Type: PROCEDURE; Schema: test; Owner: postgres
--

CREATE PROCEDURE test.to_fill_data_category(IN beg_ integer, IN end_ integer, IN after_first_circle integer)
    LANGUAGE plpgsql
    AS $$

DECLARE

parent_id_var BIGINT;

BEGIN



    -- создаём корневые категории

FOR i IN beg_..(after_first_circle-1) LOOP

        INSERT INTO category(name)

        VALUES ('category_name' || i);

END LOOP;



    -- создаём дочерние

FOR i IN after_first_circle..end_ LOOP



SELECT id INTO parent_id_var

FROM category

ORDER BY random()

    LIMIT 1;



INSERT INTO category(name, parent_id)

VALUES ('category_name' || i, parent_id_var);



END LOOP;



END;

$$;

--
-- Name: to_fill_data_component(integer, integer); Type: PROCEDURE; Schema: test; Owner: postgres
--

CREATE PROCEDURE test.to_fill_data_component(IN beg_ integer, IN end_ integer)
    LANGUAGE plpgsql
    AS $$

declare

name_test varchar(100);

	material_test varchar(100);

	country_test varchar(100);

	category_id_test bigint;

	MIN_category_id bigint;

	MAX_category_id bigint;

	cost_test NUMERIC(10,0);

begin

	MIN_category_id := (SELECT min(id) from component_category);

	MAX_category_id := (SELECT max(id) from component_category);



	MIN_category_id := MIN_category_id + MAX_category_id/2 - MAX_category_id/3;

	MAX_category_id := MAX_category_id/2;



for i in beg_..end_ loop

        -- Правильное присваивание и форматирование

        name_test := format('component_name_test%s', i::text);

		material_test := format('component_material_test%s', random(1, 15));

		country_test := format('component_country_test%s', random(1, 10));



		category_id_test = random(MIN_category_id, MAX_category_id);



		cost_test = random(1000, 10000);



insert into component("name", category_id, material, country, "cost")

values(name_test, category_id_test, material_test, country_test, cost_test);

end loop;

end;

$$;

--
-- Name: to_fill_data_component_category(integer, integer); Type: PROCEDURE; Schema: test; Owner: postgres
--

CREATE PROCEDURE test.to_fill_data_component_category(IN beg_ integer, IN end_ integer)
    LANGUAGE plpgsql
    AS $$

declare

name_test varchar(100);

begin

for i in beg_..end_ loop

        -- Правильное присваивание и форматирование

        name_test := format('name_test%s', i::text);



insert into component_category("name")

values(name_test);

end loop;

end;

$$;

--
-- Name: to_fill_data_product_section(integer, integer); Type: PROCEDURE; Schema: test; Owner: postgres
--

CREATE PROCEDURE test.to_fill_data_product_section(IN beg_ integer, IN end_ integer)
    LANGUAGE plpgsql
    AS $$

begin

insert into product_section("name")

select

    format('test_data_section%s', s)

from generate_series(beg_, end_) s;



end;



		$$;

--
-- Name: to_fill_data_product_status(integer, integer); Type: PROCEDURE; Schema: test; Owner: postgres
--

CREATE PROCEDURE test.to_fill_data_product_status(IN beg_ integer, IN end_ integer)
    LANGUAGE plpgsql
    AS $$

begin

insert into product_status("name")

select

    format('product_status_name%s', s)

from generate_series(beg_, end_) s;

end;

		$$;

--
-- Name: to_fill_data_product_style(integer, integer); Type: PROCEDURE; Schema: test; Owner: postgres
--

CREATE PROCEDURE test.to_fill_data_product_style(IN beg_ integer, IN end_ integer)
    LANGUAGE plpgsql
    AS $$

begin

insert into product_style("name")

select

    format('product_style_name%s', s)

from generate_series(beg_, end_) s;

end;

		$$;

--
-- Name: to_fill_data_product_type(integer, integer); Type: PROCEDURE; Schema: test; Owner: postgres
--

CREATE PROCEDURE test.to_fill_data_product_type(IN beg_ integer, IN end_ integer)
    LANGUAGE plpgsql
    AS $$

begin

insert into product_type("name", has_components)

select

    format('test_data_product_type%s', s),

    random() < 0.5

from generate_series(beg_, end_) s;



end;



		$$;

--
-- Name: attribute; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.attribute (
                                id bigint NOT NULL,
                                name character varying(100) NOT NULL,
                                data_type character varying(20) NOT NULL
);

--
-- Name: attribute_id_seq; Type: SEQUENCE; Schema: test; Owner: postgres
--

CREATE SEQUENCE test.attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: attribute_id_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: postgres
--

ALTER SEQUENCE test.attribute_id_seq OWNED BY test.attribute.id;

--
-- Name: attribute_value; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.attribute_value (
                                      id bigint NOT NULL,
                                      attribute_id bigint NOT NULL,
                                      value_text character varying(255),
                                      value_number numeric,
                                      value_boolean boolean,
                                      CONSTRAINT chk_one_value CHECK ((((((value_text IS NOT NULL))::integer + ((value_number IS NOT NULL))::integer) + ((value_boolean IS NOT NULL))::integer) = 1))
);

--
-- Name: attribute_value_id_seq; Type: SEQUENCE; Schema: test; Owner: postgres
--

CREATE SEQUENCE test.attribute_value_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: attribute_value_id_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: postgres
--

ALTER SEQUENCE test.attribute_value_id_seq OWNED BY test.attribute_value.id;

--
-- Name: category_id_seq; Type: SEQUENCE; Schema: test; Owner: postgres
--

CREATE SEQUENCE test.category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: category_id_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: postgres
--

ALTER SEQUENCE test.category_id_seq OWNED BY test.category.id;

--
-- Name: category_tree; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.category_tree (
                                    ancestor_id bigint NOT NULL,
                                    descendant_id bigint NOT NULL,
                                    depth integer NOT NULL
);

--
-- Name: component; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.component (
                                id bigint NOT NULL,
                                name character varying(200) NOT NULL,
                                category_id bigint NOT NULL,
                                material character varying(100),
                                country character varying(100),
                                cost numeric(10,0) DEFAULT 0
);

--
-- Name: component_category; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.component_category (
                                         id bigint NOT NULL,
                                         name character varying(100) NOT NULL
);

--
-- Name: component_category_id_seq; Type: SEQUENCE; Schema: test; Owner: postgres
--

CREATE SEQUENCE test.component_category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: component_category_id_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: postgres
--

ALTER SEQUENCE test.component_category_id_seq OWNED BY test.component_category.id;

--
-- Name: component_id_seq; Type: SEQUENCE; Schema: test; Owner: postgres
--

CREATE SEQUENCE test.component_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: component_id_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: postgres
--

ALTER SEQUENCE test.component_id_seq OWNED BY test.component.id;

--
-- Name: favourites; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.favourites (
                                 product_id bigint NOT NULL,
                                 user_id bigint NOT NULL,
                                 added_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

--
-- Name: order_details; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.order_details (
                                    id bigint NOT NULL,
                                    product_id bigint NOT NULL,
                                    order_id bigint NOT NULL,
                                    quantity integer DEFAULT 1 NOT NULL
);

--
-- Name: order_details_id_seq; Type: SEQUENCE; Schema: test; Owner: postgres
--

CREATE SEQUENCE test.order_details_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: order_details_id_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: postgres
--

ALTER SEQUENCE test.order_details_id_seq OWNED BY test.order_details.id;

--
-- Name: orders; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.orders (
                             id bigint NOT NULL,
                             user_id bigint NOT NULL,
                             order_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);

--
-- Name: orders_id_seq; Type: SEQUENCE; Schema: test; Owner: postgres
--

CREATE SEQUENCE test.orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: orders_id_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: postgres
--

ALTER SEQUENCE test.orders_id_seq OWNED BY test.orders.id;

--
-- Name: product; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.product (
                              id bigint NOT NULL,
                              name character varying(150) NOT NULL,
                              category_id bigint NOT NULL,
                              product_type_id bigint NOT NULL,
                              status_id bigint NOT NULL,
                              style_id bigint,
                              description character varying(255),
                              min_price numeric(10,0) DEFAULT 0 NOT NULL,
                              discount numeric DEFAULT 0,
                              created_at date DEFAULT now()
);

--
-- Name: product_attribute_value; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.product_attribute_value (
                                              product_id bigint NOT NULL,
                                              attribute_value_id bigint NOT NULL
);

--
-- Name: product_component; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.product_component (
                                        product_id bigint NOT NULL,
                                        component_id bigint NOT NULL,
                                        quantity bigint DEFAULT 1 NOT NULL
);

--
-- Name: product_filter_index; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.product_filter_index (
                                           product_id bigint NOT NULL,
                                           category_id bigint NOT NULL,
                                           attribute_id bigint NOT NULL,
                                           attribute_value_id bigint NOT NULL
);

--
-- Name: product_id_seq; Type: SEQUENCE; Schema: test; Owner: postgres
--

CREATE SEQUENCE test.product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: product_id_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: postgres
--

ALTER SEQUENCE test.product_id_seq OWNED BY test.product.id;

--
-- Name: product_image; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.product_image (
                                    id bigint NOT NULL,
                                    product_id bigint NOT NULL,
                                    image_path character varying(500) NOT NULL,
                                    is_main boolean DEFAULT false,
                                    sort_order integer DEFAULT 0
);

--
-- Name: product_image_id_seq; Type: SEQUENCE; Schema: test; Owner: postgres
--

CREATE SEQUENCE test.product_image_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: product_image_id_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: postgres
--

ALTER SEQUENCE test.product_image_id_seq OWNED BY test.product_image.id;

--
-- Name: product_section; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.product_section (
                                      id bigint NOT NULL,
                                      name character varying(100) NOT NULL,
                                      image_url character varying(255)
);

--
-- Name: product_section_id_seq; Type: SEQUENCE; Schema: test; Owner: postgres
--

CREATE SEQUENCE test.product_section_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: product_section_id_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: postgres
--

ALTER SEQUENCE test.product_section_id_seq OWNED BY test.product_section.id;

--
-- Name: product_status; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.product_status (
                                     id bigint NOT NULL,
                                     name character varying(100) NOT NULL
);

--
-- Name: product_status_id_seq; Type: SEQUENCE; Schema: test; Owner: postgres
--

CREATE SEQUENCE test.product_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: product_status_id_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: postgres
--

ALTER SEQUENCE test.product_status_id_seq OWNED BY test.product_status.id;

--
-- Name: product_style; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.product_style (
                                    id bigint NOT NULL,
                                    name character varying(50),
                                    image_url character varying(255)
);

--
-- Name: product_style_id_seq; Type: SEQUENCE; Schema: test; Owner: postgres
--

CREATE SEQUENCE test.product_style_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: product_style_id_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: postgres
--

ALTER SEQUENCE test.product_style_id_seq OWNED BY test.product_style.id;

--
-- Name: product_sub_product; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.product_sub_product (
                                          id_product bigint NOT NULL,
                                          id_sub_product bigint NOT NULL
);

--
-- Name: product_to_section; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.product_to_section (
                                         product_id bigint NOT NULL,
                                         section_id bigint NOT NULL
);

--
-- Name: product_type; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.product_type (
                                   id bigint NOT NULL,
                                   name character varying(100) NOT NULL,
                                   has_components boolean DEFAULT false NOT NULL
);

--
-- Name: product_type_id_seq; Type: SEQUENCE; Schema: test; Owner: postgres
--

CREATE SEQUENCE test.product_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: product_type_id_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: postgres
--

ALTER SEQUENCE test.product_type_id_seq OWNED BY test.product_type.id;

--
-- Name: users; Type: TABLE; Schema: test; Owner: postgres
--

CREATE TABLE test.users (
                            id bigint NOT NULL,
                            mail character varying(50) NOT NULL,
                            telephone_number character varying(15),
                            role_system test.user_roles DEFAULT 'CLIENT'::test.user_roles NOT NULL,
                            status test.user_status DEFAULT 'ACTIVE'::test.user_status NOT NULL,
                            password character varying(100),
                            fullname character varying(150) NOT NULL
);

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: test; Owner: postgres
--

CREATE SEQUENCE test.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: test; Owner: postgres
--

ALTER SEQUENCE test.users_id_seq OWNED BY test.users.id;

--
-- Name: attribute id; Type: DEFAULT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.attribute ALTER COLUMN id SET DEFAULT nextval('test.attribute_id_seq'::regclass);

--
-- Name: attribute_value id; Type: DEFAULT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.attribute_value ALTER COLUMN id SET DEFAULT nextval('test.attribute_value_id_seq'::regclass);

--
-- Name: category id; Type: DEFAULT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.category ALTER COLUMN id SET DEFAULT nextval('test.category_id_seq'::regclass);

--
-- Name: component id; Type: DEFAULT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.component ALTER COLUMN id SET DEFAULT nextval('test.component_id_seq'::regclass);

--
-- Name: component_category id; Type: DEFAULT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.component_category ALTER COLUMN id SET DEFAULT nextval('test.component_category_id_seq'::regclass);

--
-- Name: order_details id; Type: DEFAULT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.order_details ALTER COLUMN id SET DEFAULT nextval('test.order_details_id_seq'::regclass);

--
-- Name: orders id; Type: DEFAULT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.orders ALTER COLUMN id SET DEFAULT nextval('test.orders_id_seq'::regclass);

--
-- Name: product id; Type: DEFAULT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product ALTER COLUMN id SET DEFAULT nextval('test.product_id_seq'::regclass);

--
-- Name: product_image id; Type: DEFAULT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_image ALTER COLUMN id SET DEFAULT nextval('test.product_image_id_seq'::regclass);

--
-- Name: product_section id; Type: DEFAULT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_section ALTER COLUMN id SET DEFAULT nextval('test.product_section_id_seq'::regclass);

--
-- Name: product_status id; Type: DEFAULT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_status ALTER COLUMN id SET DEFAULT nextval('test.product_status_id_seq'::regclass);

--
-- Name: product_style id; Type: DEFAULT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_style ALTER COLUMN id SET DEFAULT nextval('test.product_style_id_seq'::regclass);

--
-- Name: product_type id; Type: DEFAULT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_type ALTER COLUMN id SET DEFAULT nextval('test.product_type_id_seq'::regclass);

--
-- Name: users id; Type: DEFAULT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.users ALTER COLUMN id SET DEFAULT nextval('test.users_id_seq'::regclass);

--
-- Name: attribute attribute_name_key; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.attribute
    ADD CONSTRAINT attribute_name_key UNIQUE (name);

--
-- Name: attribute attribute_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.attribute
    ADD CONSTRAINT attribute_pkey PRIMARY KEY (id);

--
-- Name: attribute_value attribute_value_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.attribute_value
    ADD CONSTRAINT attribute_value_pkey PRIMARY KEY (id);

--
-- Name: category category_name_key; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.category
    ADD CONSTRAINT category_name_key UNIQUE (name);

--
-- Name: category category_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);

--
-- Name: category_tree category_tree_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.category_tree
    ADD CONSTRAINT category_tree_pkey PRIMARY KEY (ancestor_id, descendant_id);

--
-- Name: component_category component_category_name_key; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.component_category
    ADD CONSTRAINT component_category_name_key UNIQUE (name);

--
-- Name: component_category component_category_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.component_category
    ADD CONSTRAINT component_category_pkey PRIMARY KEY (id);

--
-- Name: component component_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.component
    ADD CONSTRAINT component_pkey PRIMARY KEY (id);

--
-- Name: favourites favourites_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.favourites
    ADD CONSTRAINT favourites_pkey PRIMARY KEY (product_id, user_id);

--
-- Name: order_details order_details_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.order_details
    ADD CONSTRAINT order_details_pkey PRIMARY KEY (id);

--
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);

--
-- Name: product_attribute_value product_attribute_value_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_attribute_value
    ADD CONSTRAINT product_attribute_value_pkey PRIMARY KEY (product_id, attribute_value_id);

--
-- Name: product_component product_component_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_component
    ADD CONSTRAINT product_component_pkey PRIMARY KEY (product_id, component_id);

--
-- Name: product_filter_index product_filter_index_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_filter_index
    ADD CONSTRAINT product_filter_index_pkey PRIMARY KEY (product_id, attribute_value_id);

--
-- Name: product_image product_image_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_image
    ADD CONSTRAINT product_image_pkey PRIMARY KEY (id);

--
-- Name: product product_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (id);

--
-- Name: product_section product_section_name_key; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_section
    ADD CONSTRAINT product_section_name_key UNIQUE (name);

--
-- Name: product_section product_section_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_section
    ADD CONSTRAINT product_section_pkey PRIMARY KEY (id);

--
-- Name: product_status product_status_name_key; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_status
    ADD CONSTRAINT product_status_name_key UNIQUE (name);

--
-- Name: product_status product_status_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_status
    ADD CONSTRAINT product_status_pkey PRIMARY KEY (id);

--
-- Name: product_style product_style_name_key; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_style
    ADD CONSTRAINT product_style_name_key UNIQUE (name);

--
-- Name: product_style product_style_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_style
    ADD CONSTRAINT product_style_pkey PRIMARY KEY (id);

--
-- Name: product_sub_product product_sub_product_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_sub_product
    ADD CONSTRAINT product_sub_product_pkey PRIMARY KEY (id_product, id_sub_product);

--
-- Name: product_to_section product_to_section_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_to_section
    ADD CONSTRAINT product_to_section_pkey PRIMARY KEY (product_id, section_id);

--
-- Name: product_type product_type_name_key; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_type
    ADD CONSTRAINT product_type_name_key UNIQUE (name);

--
-- Name: product_type product_type_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_type
    ADD CONSTRAINT product_type_pkey PRIMARY KEY (id);

--
-- Name: users unique_mail; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.users
    ADD CONSTRAINT unique_mail UNIQUE (mail);

--
-- Name: users unique_phone; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.users
    ADD CONSTRAINT unique_phone UNIQUE (telephone_number);

--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

--
-- Name: idx_attribute_value_attribute; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_attribute_value_attribute ON test.attribute_value USING btree (attribute_id);

--
-- Name: idx_av_boolean; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_av_boolean ON test.attribute_value USING btree (value_boolean);

--
-- Name: idx_av_number; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_av_number ON test.attribute_value USING btree (value_number);

--
-- Name: idx_av_text; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_av_text ON test.attribute_value USING btree (value_text);

--
-- Name: idx_category_tree_ancestor; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_category_tree_ancestor ON test.category_tree USING btree (ancestor_id);

--
-- Name: idx_category_tree_descendant; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_category_tree_descendant ON test.category_tree USING btree (descendant_id);

--
-- Name: idx_component_category; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_component_category ON test.component USING btree (category_id);

--
-- Name: idx_component_material; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_component_material ON test.component USING btree (material);

--
-- Name: idx_component_name; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_component_name ON test.component USING btree (name);

--
-- Name: idx_filter_attribute; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_filter_attribute ON test.product_filter_index USING btree (attribute_id);

--
-- Name: idx_filter_category; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_filter_category ON test.product_filter_index USING btree (category_id);

--
-- Name: idx_filter_fast; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_filter_fast ON test.product_filter_index USING btree (category_id, attribute_id, attribute_value_id);

--
-- Name: idx_filter_product; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_filter_product ON test.product_filter_index USING btree (product_id);

--
-- Name: idx_filter_value; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_filter_value ON test.product_filter_index USING btree (attribute_value_id);

--
-- Name: idx_order_details_order; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_order_details_order ON test.order_details USING btree (order_id);

--
-- Name: idx_order_details_product; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_order_details_product ON test.order_details USING btree (product_id);

--
-- Name: idx_orders_user; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_orders_user ON test.orders USING btree (user_id);

--
-- Name: idx_pav_product; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_pav_product ON test.product_attribute_value USING btree (product_id);

--
-- Name: idx_pav_product_value; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_pav_product_value ON test.product_attribute_value USING btree (product_id, attribute_value_id);

--
-- Name: idx_pav_value; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_pav_value ON test.product_attribute_value USING btree (attribute_value_id);

--
-- Name: idx_product_component_component; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_product_component_component ON test.product_component USING btree (component_id);

--
-- Name: idx_product_component_product; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_product_component_product ON test.product_component USING btree (product_id);

--
-- Name: idx_product_image_product; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_product_image_product ON test.product_image USING btree (product_id);

--
-- Name: idx_sub_product_child; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_sub_product_child ON test.product_sub_product USING btree (id_sub_product);

--
-- Name: idx_sub_product_main; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX idx_sub_product_main ON test.product_sub_product USING btree (id_product);

--
-- Name: users_mail; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX users_mail ON test.users USING btree (mail);

--
-- Name: users_mail_unique; Type: INDEX; Schema: test; Owner: postgres
--

CREATE UNIQUE INDEX users_mail_unique ON test.users USING btree (mail);

--
-- Name: users_telephone_number; Type: INDEX; Schema: test; Owner: postgres
--

CREATE INDEX users_telephone_number ON test.users USING btree (telephone_number);

--
-- Name: attribute_value attribute_value_attribute_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.attribute_value
    ADD CONSTRAINT attribute_value_attribute_id_fkey FOREIGN KEY (attribute_id) REFERENCES test.attribute(id) ON DELETE CASCADE;

--
-- Name: component component_category_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.component
    ADD CONSTRAINT component_category_id_fkey FOREIGN KEY (category_id) REFERENCES test.component_category(id);

--
-- Name: favourites favourites_user_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.favourites
    ADD CONSTRAINT favourites_user_id_fkey FOREIGN KEY (user_id) REFERENCES test.users(id);

--
-- Name: category fk_category_parent; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.category
    ADD CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES test.category(id) ON DELETE CASCADE;

--
-- Name: category_tree fk_ct_ancestor; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.category_tree
    ADD CONSTRAINT fk_ct_ancestor FOREIGN KEY (ancestor_id) REFERENCES test.category(id) ON DELETE CASCADE;

--
-- Name: category_tree fk_ct_descendant; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.category_tree
    ADD CONSTRAINT fk_ct_descendant FOREIGN KEY (descendant_id) REFERENCES test.category(id) ON DELETE CASCADE;

--
-- Name: order_details order_details_order_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.order_details
    ADD CONSTRAINT order_details_order_id_fkey FOREIGN KEY (order_id) REFERENCES test.orders(id);

--
-- Name: orders orders_user_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.orders
    ADD CONSTRAINT orders_user_id_fkey FOREIGN KEY (user_id) REFERENCES test.users(id);

--
-- Name: product_attribute_value product_attribute_value_attribute_value_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_attribute_value
    ADD CONSTRAINT product_attribute_value_attribute_value_id_fkey FOREIGN KEY (attribute_value_id) REFERENCES test.attribute_value(id) ON DELETE CASCADE;

--
-- Name: product product_category_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product
    ADD CONSTRAINT product_category_id_fkey FOREIGN KEY (category_id) REFERENCES test.category(id);

--
-- Name: product_component product_component_component_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_component
    ADD CONSTRAINT product_component_component_id_fkey FOREIGN KEY (component_id) REFERENCES test.component(id);

--
-- Name: product_filter_index product_filter_index_attribute_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_filter_index
    ADD CONSTRAINT product_filter_index_attribute_id_fkey FOREIGN KEY (attribute_id) REFERENCES test.attribute(id);

--
-- Name: product_filter_index product_filter_index_attribute_value_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_filter_index
    ADD CONSTRAINT product_filter_index_attribute_value_id_fkey FOREIGN KEY (attribute_value_id) REFERENCES test.attribute_value(id);

--
-- Name: product product_product_type_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product
    ADD CONSTRAINT product_product_type_id_fkey FOREIGN KEY (product_type_id) REFERENCES test.product_type(id);

--
-- Name: product product_status_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product
    ADD CONSTRAINT product_status_id_fkey FOREIGN KEY (status_id) REFERENCES test.product_status(id);

--
-- Name: product product_style_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product
    ADD CONSTRAINT product_style_id_fkey FOREIGN KEY (style_id) REFERENCES test.product_style(id);

--
-- Name: product_to_section product_to_section_product_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_to_section
    ADD CONSTRAINT product_to_section_product_id_fkey FOREIGN KEY (product_id) REFERENCES test.product(id) ON DELETE CASCADE;

--
-- Name: product_to_section product_to_section_section_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: postgres
--

ALTER TABLE ONLY test.product_to_section
    ADD CONSTRAINT product_to_section_section_id_fkey FOREIGN KEY (section_id) REFERENCES test.product_section(id) ON DELETE CASCADE;

--
-- PostgreSQL database dump complete
--