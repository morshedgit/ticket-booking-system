#!/bin/bash

# URL of your endpoint
URL="http://localhost:8080/seats"

# Loop for 10 rows
for row in {1..10}
do
    # Loop for 10 columns in each row
    for col in {1..10}
    do
        # Curl command to create a seat
        curl -X POST -H "content-type:application/json" -d "{\"row\":$row,\"col\":$col}" $URL
        echo ""  # New line for readability
    done
done
