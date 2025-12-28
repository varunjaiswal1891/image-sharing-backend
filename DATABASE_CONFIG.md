# Database Configuration Summary

## Profiles Overview

### 1. Dev Profile (Default)
- **Database**: H2 In-Memory
- **Credentials**: Hardcoded in `application-dev.yml`
- **Use Case**: Local development and automated testing

### 2. Test Profile
- **Database**: AWS RDS MySQL
- **Credentials**: Retrieved from AWS Secrets Manager
- **Use Case**: Testing environment with real database

## AWS Secrets Manager Integration

### How It Works

When the application runs with the **test** profile:

1. **SecretsManagerConfig** is activated (conditional on test profile)
2. It connects to AWS Secrets Manager in `ap-south-1` region
3. Retrieves the secret named `image-share/DB`
4. Parses the JSON to extract:
   - `username` (required)
   - `password` (required)
   - `url` (optional - uses default from config if not provided)
5. **DataSourceConfig** uses these credentials to configure the HikariCP datasource

### Secret Structure in AWS

The secret `image-share/DB` must contain:

```json
{
  "username": "admin",
  "password": "your_secure_password"
}
```

Or with optional URL:

```json
{
  "username": "admin",
  "password": "your_secure_password",
  "url": "jdbc:mysql://your-rds-endpoint:3306/dbname?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
}
```

## Quick Start

### Create the Secret in AWS

```bash
aws secretsmanager create-secret \
  --name image-share/DB \
  --description "Database credentials for ImageShare application" \
  --secret-string '{"username":"admin","password":"your_secure_password"}' \
  --region ap-south-1
```

### Run with Test Profile

```bash
# Make sure AWS credentials are configured
aws configure

# Run the application
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

## Files Modified

1. **pom.xml**
   - Added `software.amazon.awssdk:secretsmanager` dependency
   - Added `jackson-databind` for JSON parsing

2. **SecretsManagerConfig.java** (NEW)
   - Retrieves credentials from AWS Secrets Manager
   - Only active when test profile is used
   - Returns DatabaseCredentials bean

3. **DataSourceConfig.java** (NEW)
   - Configures HikariCP datasource
   - Overrides credentials with values from Secrets Manager
   - Only active when test profile is used

4. **application-test.yml**
   - Removed hardcoded `username` and `password`
   - Keeps default `url` (can be overridden by secret)

5. **application.yaml**
   - Added AWS Secrets Manager configuration properties

## IAM Permissions Required

The application needs these permissions:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "secretsmanager:GetSecretValue",
        "secretsmanager:DescribeSecret"
      ],
      "Resource": "arn:aws:secretsmanager:ap-south-1:*:secret:image-share/DB-*"
    }
  ]
}
```

## Benefits

✅ **Security**: Credentials never stored in code or config files  
✅ **Centralized**: Single source of truth for database credentials  
✅ **Rotation**: Easy to rotate passwords via AWS console  
✅ **Audit**: CloudTrail logs all secret access  
✅ **Flexibility**: Different credentials per environment  

## Troubleshooting

See [AWS_SECRETS_MANAGER_SETUP.md](AWS_SECRETS_MANAGER_SETUP.md) for detailed troubleshooting steps.
