# Pick&Save documentation

## Running the Application with Docker

### First time setup

Before running the application for the first time, you need to set the correct permissions for database initialization scripts.

Run this in your terminal:


```shell
./setup.sh
```
Then, to start all services with a clean database state, run:

```shell
docker-compose down -v
docker-compose up --build
``` 
This will:

* Set execution permissions on schema.sh and check-db.sh
* Remove old database volumes (if any)
* Rebuild and start all microservices and the PostgreSQL database

Generate JWT secret key:
```shell
openssl rand -base64 64
```