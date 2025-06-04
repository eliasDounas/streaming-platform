# AWS IVS Thumbnail Integration - Minimal Setup

This service integrates with AWS IVS to automatically fetch real thumbnail URLs for streams.

## Quick Setup

### 1. Update application.properties
The configuration is already set for your IVS setup:
```properties
aws.ivs.region=eu-west-1
aws.s3.region=eu-west-1
aws.s3.thumbnail-bucket=ivs-streams-archives
```

### 2. AWS Credentials
Set your AWS credentials using environment variables:
```bash
export AWS_ACCESS_KEY_ID=your_access_key
export AWS_SECRET_ACCESS_KEY=your_secret_key
export AWS_DEFAULT_REGION=eu-west-1
```

Or create `~/.aws/credentials`:
```
[default]
aws_access_key_id = YOUR_ACCESS_KEY
aws_secret_access_key = YOUR_SECRET_KEY
```

### 3. Required AWS Permissions
Your AWS credentials need these permissions:
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:GetObject",
                "s3:HeadObject"
            ],
            "Resource": [
                "arn:aws:s3:::ivs-streams-archives/*"
            ]
        }
    ]
}
```

## How It Works

### S3 Thumbnail Structure
Based on your IVS recording configuration:
- Recording configuration: `hhJNCFp8leuE`
- Thumbnail interval: 60 seconds
- Storage: "Store only the latest thumbnail"

Thumbnails are stored as:
```
s3://ivs-streams-archives/
├── {channelId1}/latest.jpg
├── {channelId2}/latest.jpg
└── {channelId3}/latest.jpg
```

### Stream Creation
When you call `createStream(channelArn)`:
1. Extracts channel ID from ARN: `arn:aws:ivs:eu-west-1:971528320784:channel/ABC123` → `ABC123`
2. Checks if `s3://ivs-streams-archives/ABC123/latest.jpg` exists
3. If exists: uses real thumbnail URL
4. If not exists: uses default URL pattern (for new channels)

### Example
```java
// Input ARN
"arn:aws:ivs:eu-west-1:971528320784:channel/ABC123"

// Resulting thumbnail URL
"https://ivs-streams-archives.s3.eu-west-1.amazonaws.com/ABC123/latest.jpg"
```

## Testing
To test if everything works, try creating a stream:
```bash
POST /api/streams
{
    "arn": "arn:aws:ivs:eu-west-1:971528320784:channel/YOUR_CHANNEL_ID"
}
```

Check the response - `thumbnailUrl` should be a real S3 URL instead of "GENERIC_THUMBNAIL_URL".

## Troubleshooting
- **Access Denied**: Check AWS credentials and permissions
- **Still getting "GENERIC_THUMBNAIL_URL"**: Channel might be new, start streaming to generate thumbnails
When a new stream is created via `createStream(String arn)`:
- The service extracts the channel ID from the IVS channel ARN
- It looks for the latest thumbnail in S3 under `thumbnails/{channelId}/`
- If found, it uses the real thumbnail URL
- If not found, it falls back to a default thumbnail URL pattern

### 2. Thumbnail Updates
You can update thumbnails for existing streams using these endpoints:

```bash
# Update thumbnail for specific stream
PUT /api/streams/{streamId}/thumbnail?channelArn=arn:aws:ivs:...

# Update thumbnail for live stream by channel
PUT /api/streams/channels/{channelId}/thumbnail?channelArn=arn:aws:ivs:...
```

### 3. Automatic Thumbnail Selection
The service automatically selects the most recent thumbnail based on the `lastModified` timestamp in S3.

## API Examples

### Create Stream (with real thumbnail)
```bash
POST /api/streams
Content-Type: application/json

{
    "arn": "arn:aws:ivs:us-east-1:123456789012:channel/AbCdEfGhIjKl"
}
```

Response will include the real thumbnail URL:
```json
{
    "id": 123,
    "channelId": "AbCdEfGhIjKl",
    "title": "My Stream",
    "thumbnailUrl": "https://your-bucket.s3.us-east-1.amazonaws.com/thumbnails/AbCdEfGhIjKl/2025-06-04T10-30-00.jpg",
    "isLive": true,
    // ... other fields
}
```

### Update Thumbnail
```bash
PUT /api/streams/123/thumbnail?channelArn=arn:aws:ivs:us-east-1:123456789012:channel/AbCdEfGhIjKl
```

## Troubleshooting

### Common Issues

1. **"Access Denied" errors**: Check your AWS credentials and permissions
2. **"Bucket not found"**: Verify the bucket name in `application.properties`
3. **"No thumbnails found"**: Ensure your IVS channel is generating thumbnails to S3
4. **Wrong region**: Make sure `aws.s3.region` matches your bucket's region

### Logs
The service logs errors to the console. Look for messages like:
- "Error fetching thumbnail URL: ..."
- "Error fetching channel info: ..."

### Testing
You can test the AWS integration by checking if your credentials work:
```bash
aws ivs get-channel --arn "your-channel-arn"
aws s3 ls s3://your-thumbnail-bucket/thumbnails/
```

## Production Considerations

1. **Caching**: Consider adding Redis caching for thumbnail URLs to reduce S3 API calls
2. **Error Handling**: The service gracefully falls back to default URLs if AWS is unavailable
3. **Rate Limiting**: AWS APIs have rate limits; consider implementing exponential backoff
4. **Monitoring**: Set up CloudWatch alarms for failed API calls
5. **Security**: Use IAM roles instead of access keys in production
