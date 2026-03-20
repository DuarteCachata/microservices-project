# VARIABLES
REGION="eu-central-1"
VPC_ID="vpc-080e32385e62a609b"
PUBLIC_SUBNET_ID="subnet-06749ae672368b8a2"

# Get latest Amazon Linux 2 AMI
AMI_ID=$(aws ec2 describe-images \
 --owners amazon \
 --filters "Name=name,Values=amzn2-ami-hvm-*-x86_64-gp2" \
 "Name=state,Values=available" \
 --query 'Images | sort_by(@, &CreationDate) | [-1].ImageId' \
 --output text \
 --region $REGION)

# Create a security group that allows SSH from your current public IP
MY_IP=$(curl -s https://checkip.amazonaws.com)/32
SG_ID=$(aws ec2 create-security-group \
  --group-name week5-ssh-sg \
  --description "Week 5: allow SSH from my IP" \
  --vpc-id $VPC_ID \
  --query 'GroupId' \
  --output text \
  --region $REGION)

aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 22 \
  --cidr $MY_IP \
  --region $REGION

# Launch instance in public subnet
INSTANCE_ID=$(aws ec2 run-instances \
 --image-id $AMI_ID \
 --instance-type t3.micro \
 --subnet-id $PUBLIC_SUBNET_ID \
 --associate-public-ip-address \
 --key-name cloud-key-new \
 --security-group-ids $SG_ID \
 --query 'Instances[0].InstanceId' \
 --output text \
 --region $REGION)

echo "Launched instance: $INSTANCE_ID"

# Wait until the instance is running (so the public IP shows up)
aws ec2 wait instance-running --instance-ids $INSTANCE_ID --region $REGION

# Get public IP
PUBLIC_IP=$(aws ec2 describe-instances \
 --instance-ids $INSTANCE_ID \
 --query 'Reservations[0].Instances[0].PublicIpAddress' \
 --output text \
 --region $REGION)

echo "Public IP: $PUBLIC_IP"