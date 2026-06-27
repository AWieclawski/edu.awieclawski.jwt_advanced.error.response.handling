A Spring Boot application using JWT authentication to secure the exposed REST API.

A Spring Boot + JWT + H2 JPA implementation for storing and retrieving user credentials. JWT tokens are cached as a ConcurrentHashMap. The cache is refreshed, and expired tokens are purged on a schedule.

Methods called by the ResourceController throw random exceptions, allowing for verification of their catching and persistence by the GlobalRestExceptionHandler.

We implemented secure storage of caught error messages in the database. Implementing this support reduces the redundancy of identical error messages and even prevents potential DDoS attacks. The goal of this implementation is to ensure that a client persistently repeating the same action, resulting in the same error, within a minute will receive the same message with the same UUID during the same session.