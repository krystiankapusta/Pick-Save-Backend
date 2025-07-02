#!/bin/bash
pg_isready -U "$AUTH_DB_USER" -d "$AUTH_DB" || exit 1
pg_isready -U "$PRODUCT_DB_USER" -d "$PRODUCT_DB" || exit 1