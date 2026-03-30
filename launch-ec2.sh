#!/bin/bash

# Variables (AJUSTA SE NECESSÁRIO)
AMI_ID="ami-07c37bd7efcc260da"
INSTANCE_TYPE="t3.micro"
KEY_NAME="week6-key"
SECURITY_GROUP="sg-0116851a835344b7e"
SUBNET_ID="subnet-06749ae672368b8a2"

echo "Launching EC2 instance..."

# Launch instance
INSTANCE_ID=$(aws ec2 run-instances \
  --image-id $AMI_ID \
  --instance-type $INSTANCE_TYPE \
  --key-name $KEY_NAME \
  --security-group-ids $SECURITY_GROUP \
  --subnet-id $SUBNET_ID \
  --associate-public-ip-address \
  --tag-specifications "ResourceType=instance,Tags=[{Key=Name,Value=week6-instance-cli}]" \
  --query "Instances[0].InstanceId" \
  --output text)

echo "Instance ID: $INSTANCE_ID"

# Wait for instance to be running
echo "Waiting for instance to be running..."
aws ec2 wait instance-running --instance-ids $INSTANCE_ID

# Get public IP
PUBLIC_IP=$(aws ec2 describe-instances \
  --instance-ids $INSTANCE_ID \
  --query "Reservations[0].Instances[0].PublicIpAddress" \
  --output text)

echo "Instance is running!"
echo "Public IP: $PUBLIC_IP"
echo "Connect with: ssh -i ${KEY_NAME}.pem ec2-user@${PUBLIC_IP}"