#!/bin/sh

set -e  # Exit on any command failure

apk add --no-cache curl sed

mkdir -p /dashboards

# List of dashboard IDs
DASHBOARD_IDS="9964 4701 11378 16459 1860 8321 17642"

# Loop through each dashboard ID
for DASHBOARD_ID in $DASHBOARD_IDS; do
  echo "Fetching dashboard ID: $DASHBOARD_ID"
  curl -s https://grafana.com/api/dashboards/$DASHBOARD_ID/revisions/1/download -o /dashboards/$DASHBOARD_ID.json

  echo "Updating datasource to 'Prometheus' in $DASHBOARD_ID.json"
  sed -i 's/"datasource": "[^"]*"/"datasource": "Prometheus"/g' /dashboards/$DASHBOARD_ID.json
done

echo "✅ All dashboards fetched and updated."
