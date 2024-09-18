#!/bin/bash

echo "Running S3 bucket creation script..."
awslocal s3 mb s3://digimon-meta-dev-bucket
echo "Bucket creation complete."

echo "Applying CORS policy to S3 bucket..."

awslocal s3api put-bucket-cors --bucket digimon-meta-dev-bucket --cors-configuration '{
    "CORSRules": [
        {
            "AllowedOrigins": ["*"],
            "AllowedMethods": ["GET", "PUT", "POST", "DELETE", "HEAD"],
            "AllowedHeaders": ["*"],
            "MaxAgeSeconds": 3000,
            "ExposeHeaders": ["ETag"]
        }
    ]
}'

echo "CORS policy applied successfully."
