#!/bin/bash

echo "Setting execute permissions for database initialization scripts..."

chmod +x ./Database/schema.sh
chmod +x ./Database/check-db.sh

echo "Permissions set successful."
echo "Now run: docker-compose down -v && docker-compose up --build"