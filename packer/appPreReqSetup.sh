#!/bin/bash
set -e

# Remove git
sudo DEBIAN_FRONTEND=noninteractive apt remove git -y

sudo DEBIAN_FRONTEND=noninteractive apt purge --auto-remove git -y

sudo DEBIAN_FRONTEND=noninteractive apt autoremove -y

# Install Java
sudo DEBIAN_FRONTEND=noninteractive apt install openjdk-17-jdk -y

# Install Maven
sudo DEBIAN_FRONTEND=noninteractive apt install maven -y

# Install AWS CLI v2
sudo apt-get install -y unzip curl
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
rm -rf aws awscliv2.zip

# Download and install CloudWatch agent
sudo apt-get install -y wget
wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb
sudo dpkg -i -E ./amazon-cloudwatch-agent.deb
rm amazon-cloudwatch-agent.deb

