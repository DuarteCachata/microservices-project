#!/usr/bin/env python3
"""
AWS Permissions Validator
Validates IAM permissions for a user or role
"""

import boto3
from botocore.exceptions import ClientError


def validate_s3_access(s3_client, bucket_name):
    """Validate S3 bucket access"""
    try:
        s3_client.head_bucket(Bucket=bucket_name)
        print(f"Can access bucket: {bucket_name}")
        return True
    except ClientError as e:
        error_code = e.response['Error']['Code']
        if error_code == '403':
            print(f"Access denied to bucket: {bucket_name}")
        elif error_code == '404':
            print(f"Bucket not found: {bucket_name}")
        else:
            print(f"Error accessing bucket: {e}")
        return False


def validate_ec2_permissions(ec2_client):
    """Validate EC2 describe permissions"""
    try:
        response = ec2_client.describe_instances()
        instance_count = sum(
            len(reservation['Instances'])
            for reservation in response['Reservations']
        )
        print(f"Can describe EC2 instances (found {instance_count} instances)")
        return True
    except ClientError as e:
        print(f"Cannot describe EC2 instances: {e}")
        return False


def main():
    print("AWS Permissions Validator\n")

    s3 = boto3.client('s3', region_name='eu-central-1')
    ec2 = boto3.client('ec2', region_name='eu-central-1')

    bucket_name = 'week4-student-bucket-1773172483'

    print("Testing S3 access...")
    validate_s3_access(s3, bucket_name)

    print("\nTesting EC2 permissions...")
    validate_ec2_permissions(ec2)


if __name__ == '__main__':
    main()