## restapi

#### setup

* create database schema (assuming postgres) by running datbase/db-schema.sql
* copy conf/appliction.example.conf to conf/application.conf
* edit conf/application.conf and update the username:password in the db.default.url
* run the app with sbt run

#### Example usage

* submit a batch of new visitor events
~~~~
 curl --header "client_id: 100"   --header "Content-Type: application/json"   --request POST   --data '{ "deviceId": 321331333, "model": "Mozilla/5.0 (Linux; Android 8.0.0; SM-G960F Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko)", "locationElements": [{ "dt": "2019-09-02T23:28:56.782Z", "lat": 51.678361, "lon": -2.176256}]}'   http://localhost:9000/api/v1/locations
~~~~

* get a list of events using a startdate and boundary
~~~~
http://localhost:9000/api/v1/locations?startDate=2019-09-01T23:28:56.782Z&limit=10&boundary=51.727327,-2.274689,51.678361,-2.176256
~~~~
