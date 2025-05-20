#!/bin/bash
pg_isready -U "$AUTH_DB_USER" -d "$AUTH_DB" || exit 1
