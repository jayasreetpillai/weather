#!/bin/bash

# set the range of values for temperature, humidity, and wind speed
temp_min=20.0
temp_max=50.0
humidity_min=10.0
humidity_max=90.0
wind_speed_min=0.0
wind_speed_max=20.0

# set the API endpoint
api_url="http://localhost:8080/weather"

# check if the number of records parameter was provided
if [ $# -eq 0 ]; then
  echo "Usage: $0 <num_records>"
  exit 1
fi

# get the number of records to generate from the command line parameter
num_records=$1

# generate the test data and submit it to the API
for ((i=1; i<=num_records; i++))
do
  sensor="sensor$((1 + RANDOM % 5))"
  date=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
  temperature=$(awk -v min="$temp_min" -v max="$temp_max" 'BEGIN{srand(); printf "%.2f\n", min+rand()*(max-min)}')
  humidity=$(awk -v min="$humidity_min" -v max="$humidity_max" 'BEGIN{srand(); printf "%.2f\n", min+rand()*(max-min)}')
  wind_speed=$(awk -v min="$wind_speed_min" -v max="$wind_speed_max" 'BEGIN{srand(); printf "%.1f\n", min+rand()*(max-min)}')

  # create the JSON payload
  payload="{\"sensor\":\"$sensor\",\"date\":\"$date\",\"temperature\":$temperature,\"humidity\":$humidity,\"windSpeed\":$wind_speed}"

  # submit the payload to the API using cURL
  curl --request POST \
       --header "Content-Type: application/json" \
       --data "$payload" \
       $api_url

  echo "Record submitted: $payload"
done