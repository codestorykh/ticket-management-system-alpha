#!/bin/bash

# Test script for API Gateway Token Verification
# Make sure both API Gateway (port 8080) and User Service (port 8081) are running

echo "=== API Gateway Token Verification Test ==="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Login to get a token
echo -e "\n${YELLOW}Test 1: Login to get JWT token${NC}"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/public/users/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}')

echo "Login Response: $LOGIN_RESPONSE"

# Extract token from response (assuming response contains "data" field with token)
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}Failed to extract token from login response${NC}"
    exit 1
fi

echo -e "${GREEN}Successfully obtained token: ${TOKEN:0:50}...${NC}"

# Test 2: Test protected endpoint with valid token
echo -e "\n${YELLOW}Test 2: Access protected endpoint with valid token${NC}"
PROTECTED_RESPONSE=$(curl -s -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN")

echo "Protected Endpoint Response: $PROTECTED_RESPONSE"

if [[ $PROTECTED_RESPONSE == *"error"* ]] || [[ $PROTECTED_RESPONSE == *"unauthorized"* ]]; then
    echo -e "${RED}Failed to access protected endpoint with valid token${NC}"
else
    echo -e "${GREEN}Successfully accessed protected endpoint with valid token${NC}"
fi

# Test 3: Test protected endpoint without token
echo -e "\n${YELLOW}Test 3: Access protected endpoint without token${NC}"
NO_TOKEN_RESPONSE=$(curl -s -X GET http://localhost:8080/api/users)

echo "No Token Response: $NO_TOKEN_RESPONSE"

if [[ $NO_TOKEN_RESPONSE == *"unauthorized"* ]] || [[ $NO_TOKEN_RESPONSE == *"UNAUTHORIZED"* ]]; then
    echo -e "${GREEN}Correctly rejected request without token${NC}"
else
    echo -e "${RED}Should have rejected request without token${NC}"
fi

# Test 4: Test protected endpoint with invalid token
echo -e "\n${YELLOW}Test 4: Access protected endpoint with invalid token${NC}"
INVALID_TOKEN_RESPONSE=$(curl -s -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer invalid.token.here")

echo "Invalid Token Response: $INVALID_TOKEN_RESPONSE"

if [[ $INVALID_TOKEN_RESPONSE == *"unauthorized"* ]] || [[ $INVALID_TOKEN_RESPONSE == *"UNAUTHORIZED"* ]]; then
    echo -e "${GREEN}Correctly rejected request with invalid token${NC}"
else
    echo -e "${RED}Should have rejected request with invalid token${NC}"
fi

# Test 5: Test public endpoint (should work without token)
echo -e "\n${YELLOW}Test 5: Access public endpoint without token${NC}"
PUBLIC_RESPONSE=$(curl -s -X POST http://localhost:8080/api/public/users/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "password": "test"}')

echo "Public Endpoint Response: $PUBLIC_RESPONSE"

if [[ $PUBLIC_RESPONSE == *"unauthorized"* ]] || [[ $PUBLIC_RESPONSE == *"UNAUTHORIZED"* ]]; then
    echo -e "${RED}Public endpoint should not require authentication${NC}"
else
    echo -e "${GREEN}Successfully accessed public endpoint without token${NC}"
fi

echo -e "\n${GREEN}=== Token Verification Test Complete ===${NC}" 