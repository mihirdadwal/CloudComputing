# UserAuth Project

A secure REST API for user authentication and profile management built with Spring Boot.

## Features

- User account management
- Profile picture handling with S3
- Email verification via SNS/Lambda
- Health monitoring
- Auto-scaling and load balancing

## API Endpoints

### Health Check
```
GET /healthz
```

### User Management
```
POST /v1/user           # Create user
GET /v1/user/self       # Get profile
PUT /v1/user/self       # Update profile
```

### Profile Picture
```
POST /v1/user/self/pic    # Upload picture
GET /v1/user/self/pic     # Get picture
DELETE /v1/user/self/pic  # Delete picture
```

### Email Verification
```
GET /v1/verify?email=<email>&token=<token>
```

## Technical Stack

- Java 17 & Spring Boot
- AWS Services (RDS, S3, SNS, Lambda, CloudWatch)
- Maven
- MySQL Database

## Security

- BCrypt password encryption
- Basic authentication
- Input validation
- File upload restrictions (10MB max, JPEG/PNG only)
- Email verification required

## Setup Requirements

1. Java 17
2. Maven
3. AWS account
4. MySQL/RDS database
5. Configured AWS services:
    - S3 bucket
    - SNS topic
    - Lambda function
    - CloudWatch

## Error Codes

- 200/201/204: Success
- 400: Bad Request
- 401: Unauthorized
- 404: Not Found
- 503: Service Unavailable