# Cisco phone users
This is a microservice which user can register themselves and manage their phones. 
Please note currently the microservices uses H2 database as storeage. We can easily migrate to MySQL using proerties.

# Architecture
This microservice has three services:

1. API gateway service which takes care of security to internal APIs. The API gateway is hosted on port 8082 and is 
responsible for routing to internal microservices. It uses Path Predicate and Rewrite filter to forward all requests
to the URL with the pattern /api/v1/users to user-service by rewriting the URLs like and rewrite all requests to 
/ap1/v1/users to /users.

2. User Service which takes care of User registration and the management of Phones. This connects to H2 database for persisting the objects.
3. Discovery service which manages the IP addresses and availability of the microservices


|--------------         |----------------------|        |-------------------
|             |         |                      |        |                  |
| API Gateway |-------->|  Discovery Service   |  ----->| User Service     |
|             |         |                      |        |                  |
|--------------         |-----------------------        |--------------------


# How to start the application
## Pre-requisites
1. Docker installed and runnable by the current user
2. docker-compose installed
3. Maven to Build the artifacts

Step 1: Compile and build all artifacts
```shell
mvn clean install
```

Step 2: User docker-compose bring up the docker instances

```shell
docker-compose up --build
```

Step 3: When the services are up

Follow the below steps to register and use the application:
Step 1: Hit ```http://localhost:8761/``` to check eureka portal to see two services - user-service and api-gateway are up

Step 2: Execute POST command to create a user:

```shell
curl --location --request POST 'http://localhost:8082/api/v1/users/register' \
--header 'Content-Type: application/json' \
--data-raw '{
    "userName": "shivasr",
    "password": "Welcome!23",
    "emailAddress": "shivasr@gmail.com"
}'
```

Sample Response
```shell
{
    "userId": "2e988e67-006b-4bb4-96e3-e3c87c843a10",
    "userName": "shivasr",
    "password": "$2a$10$I5iPoa.STKWQASb4IZeLAurTPchTSM6vncKgwSKo9gXRWxZnmM3oi",
    "emailAddress": "shivasr@gmail.com",
    "preferredPhoneNumber": null
}
```

Step 3: Using your credentials,  hit GET to see if the user is created:
```shell
curl --location --request GET 'http://localhost:8082/api/v1/users' \
--header 'Authorization: Basic c2hpdmFzcjpXZWxjb21lITIz' 
```
Sample Response
```shell
[
    {
        "userId": "2e988e67-006b-4bb4-96e3-e3c87c843a10",
        "userName": "shivasr",
        "password": "$2a$10$I5iPoa.STKWQASb4IZeLAurTPchTSM6vncKgwSKo9gXRWxZnmM3oi",
        "emailAddress": "shivasr@gmail.com",
        "preferredPhoneNumber": null
    }
]
```

Step 4: Add a phone to the above user

```shell
curl --location --request POST 'http://localhost:8082/api/v1/users/2e988e67-006b-4bb4-96e3-e3c87c843a10/phoneNumbers' \
--header 'Authorization: Basic c2hpdmFzcjpXZWxjb21lITIz' \
--header 'Content-Type: application/json' \ 
--data-raw '{
    "phoneName": "Shivakunar iPhone",
    "phoneModel": "iPhone",
    "phoneNumber": "+919980697299"

}'
```

Sample Response:

```shell
{{
    "phoneId": "221bcff9-4a70-49ff-b124-c52f35bd1ee3",
    "phoneName": "Shivakunar iPhone",
    "phoneModel": "IPHONE",
    "phoneNumber": "+919980697299"
}
```

Step 5: List all phones assigned to a user


```shell
curl --location --request GET 'http://localhost:8082/api/v1/users/2e988e67-006b-4bb4-96e3-e3c87c843a10/phoneNumbers' \
--header 'Authorization: Basic c2hpdmFzcjpXZWxjb21lITIz' \
--header 'Cookie: JSESSIONID=22C454B388DFFAC7EB36F411858173D9'
```
Sample Response:
```shell
[
    {
        "phoneId": "221bcff9-4a70-49ff-b124-c52f35bd1ee3",
        "phoneName": "Shivakunar iPhone",
        "phoneModel": "IPHONE",
        "phoneNumber": "+919980697299"
    }
]
```

Step 6: Assign a preferred phone number to the user

```shell
curl --location --request PATCH 'http://localhost:8082/api/v1/users/ee275671-7a36-4445-85d3-53e7b1f6b21f' \
--header 'Authorization: Basic c2hpdmFzcjpXZWxjb21lITIz' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=FAF1AAA19D0945C0690A9B01A1C434C8' \
--data-raw '{
    "preferredPhoneNumber": "+919980697299"
}'
```

Sample Response:
```shell
{
    "userId": "ee275671-7a36-4445-85d3-53e7b1f6b21f",
    "userName": "shivasr",
    "password": "$2a$10$mt2b2D5aJ0dCNV5gnWFBHuIKJpcd1W6ag/D/RGsvAeH//Efy/3ylS",
    "emailAddress": "shivasr@gmail.com",
    "preferredPhoneNumber": "+919980697299"
}
```
