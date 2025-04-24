#!/bin/bash
set -e

# Set the application directory
DIR="/opt/userauth/"

# Create the application directory
sudo mkdir -p "${DIR}"

# Create a system user for running the application
sudo useradd --system -s /usr/sbin/nologin csye6225

# Copy the JAR and config file from /tmp to the application directory
sudo cp /tmp/*.jar "${DIR}/"
sudo cp /tmp/*.properties "${DIR}/"

# Copy the systemd file and start it 
sudo cp /tmp/*.service "/etc/systemd/system/"
sudo systemctl daemon-reload
sudo systemctl enable csye6225

# Create CloudWatch Agent configuration file
sudo cat << EOF > /tmp/amazon-cloudwatch-agent.json
{
  "agent": {
    "metrics_collection_interval": 10,
    "logfile": "/var/logs/amazon-cloudwatch-agent.log"
  },
  "logs": {
    "logs_collected": {
      "files": {
        "collect_list": [
          {
            "file_path": "/opt/userauth/application.log",
            "log_group_name": "userauth-application-logs",
            "log_stream_name": "userauth-app"
          }
        ]
      }
    }
  },
  "metrics": {
    "namespace": "CustomMetrics",
    "metrics_collected": {
      "statsd": {
        "service_address": ":8125",
        "metrics_collection_interval": 15,
        "metrics_aggregation_interval": 60
      }
    }
  }
}
EOF
sudo mv /tmp/amazon-cloudwatch-agent.json /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json

# Start the CloudWatch agent
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
    -a fetch-config \
    -m ec2 \
    -s \
    -c file:/opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json

# Ensure CloudWatch agent starts on boot
sudo systemctl enable amazon-cloudwatch-agent

# Set appropriate permissions
sudo chown -R csye6225:csye6225 "${DIR}"
sudo chmod 755 "${DIR}"