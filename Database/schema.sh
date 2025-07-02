#!/bin/bash

set -e

# Create database and user
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
  CREATE DATABASE $AUTH_DB;
  CREATE USER $AUTH_DB_USER WITH ENCRYPTED PASSWORD '$AUTH_DB_PASSWORD';
  GRANT CONNECT ON DATABASE $AUTH_DB TO $AUTH_DB_USER;
EOSQL

# Grant schema and table privileges for Hibernate DDL
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname="$AUTH_DB" <<-EOSQL
  -- Required for Hibernate to create tables and sequences
  GRANT USAGE, CREATE ON SCHEMA public TO $AUTH_DB_USER;

  -- Grant full access to existing tables and sequences
  GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $AUTH_DB_USER;
  GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO $AUTH_DB_USER;

  -- Ensure future objects are accessible too
  ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $AUTH_DB_USER;
  ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $AUTH_DB_USER;
EOSQL

# Create database and user
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
  CREATE DATABASE $PRODUCT_DB;
  CREATE USER $PRODUCT_DB_USER WITH ENCRYPTED PASSWORD '$PRODUCT_DB_PASSWORD';
  GRANT CONNECT ON DATABASE $PRODUCT_DB TO $PRODUCT_DB_USER;
EOSQL

# Grant schema and table privileges for Hibernate DDL
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname="$PRODUCT_DB" <<-EOSQL
  -- Required for Hibernate to create tables and sequences
  GRANT USAGE, CREATE ON SCHEMA public TO $PRODUCT_DB_USER;

  -- Grant full access to existing tables and sequences
  GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $PRODUCT_DB_USER;
  GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO $PRODUCT_DB_USER;

  -- Ensure future objects are accessible too
  ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $PRODUCT_DB_USER;
  ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $PRODUCT_DB_USER;
EOSQL
