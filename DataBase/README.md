# DataBase

The DataBase module is the implementation of the IDL definition of this 
component.

## Interactions

* Connects to a postgresql database.

## Usage

* Remember to install and create a postgresql server.

`yum -y install postgresql postgresql-jdbc postgresql-server`
/usr/bin/postgresql-setup --initdb
service postgresql start

sudo -u postgres createuser -W acs


java -classpath lib/hsqldb.jar org.hsqldb.server.Server
java -classpath lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:hsqldb/hemrajdb --dbname.0 testdb


