# Weather
Weather service is used to store weather metrics from sensors. It also provides getting statistics of stored weather metrics.

## Prerequisites
    JDK 17
    Mongo 5.0 or later
    Mongo shell
    Postman

### Steps to run weather app
1. Please make sure you have all the software mentioned above is installed
2. Start the mongodb server (Use this [link](https://www.mongodb.com/docs/manual/installation/) to install if not exists)
3. Go to Mongodb bin folder(eg: C:\Program Files\MongoDB\Server\5.0\bin) and open a terminal here
   1. Type in ```mongo``` and press enter. It will open up mongo shell
   2. Copy and paste the below command to create the database and collection.
        ~~~
      use weather
       db.createCollection(
       "weather_metrics",
         {
          timeseries: {
           timeField: "timestamp",
           metaField: "sensor",
           granularity: "hours"
          }
        }
       )

      ~~~
4. You can change db name if you need. Default db name used is weather. If you are using a different name,please update ``` spring.data.mongodb.database``` value in 'application.properties' file and copy 'application.properties' file and build project again.
5. Use this command to create collection. You can use Mongo shell to run this command
'''
'''
6. Build project using command ```mvn clean install ```
7. 
8. Go to target folder, run command ```java -jar weather-0.0.1-SNAPSHOT.jar``` make sure you are running it from target folder or copy jar to current folder.
9. Run this shell command ```.\generate_test_data.sh 10``` where 10 is the number of entries you would like to save.
10. Or if you need to enter data with a different date , please see api details and use json format mentioned there.

### API Details

Use the below link to access Swagger UI 

[Swagger](http://localhost:8080/swagger-ui/)

#### How to insert new weather data 

POST http://localhost:8080/weather

~~~
{
    "sensor": "sensor2",
    "date": "2023-03-23T00:47:23.166Z",
    "temperature": 44.00,
    "humidity": 32.34,
    "windSpeed": 7.4
}
~~~

#### How to get aggregate metric

POST http://localhost:8080/weather/metric

Method Body

~~~
{
    "sensors": [
        "sensor1"
    ],
    "metrics": [
        "temperature",
        "humidity",
        "windSpeed"
    ],
    "statistics": "max",
    "fromDate": "2023-03-24T17:53:14.538342800Z",
    "toDate": "2023-03-25T17:53:14.538342800Z"
}
~~~