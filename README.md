# Spring PetClinic with Custom Feature Flag System

This project is a forked and enhanced version of the official Spring PetClinic application, extended with a custom-built, production-ready Feature Flag system as part of a technical assessment.

The feature flag system is implemented from scratch (no external libraries like FF4J or Togglz) and allows runtime control of core application features without redeploying the application.

---

## Key Highlights

- Built on top of the official Spring PetClinic sample
- Custom Feature Flag engine (no external dependencies)
- Supports multiple rollout strategies
- Safe defaults and production-grade fallbacks
- Cache-aware, transactional, and resilient to failures
- Bonus: Annotation-based feature toggle using AOP

---

## Features Controlled by Feature Flags

| Flag Name | Controlled Feature | Supported Types | Code Location | Default (If Missing) |
|---------|-------------------|-----------------|---------------|----------------------|
| add_new_pet | Add new pet (UI + submit) | BOOLEAN, PERCENTAGE, WHITELIST, BLACKLIST | PetController.processCreationForm() | Enabled |
| add_visit | Add visit for existing pet | BOOLEAN, PERCENTAGE, WHITELIST, BLACKLIST | VisitController.processNewVisitForm() | Enabled |
| owner_search | Search owners by last name | BOOLEAN (recommended), PERCENTAGE (random) | OwnerController.processFindForm() | Enabled |

---

## Feature Flag Types

- BOOLEAN: Global ON / OFF toggle
- PERCENTAGE: Gradual rollout (deterministic per owner for pet/visit, random per request for search)
- WHITELIST: Allow only specific owners (format: owner-<id>)
- BLACKLIST: Block specific owners

---

## Edge Cases & Safety Handling

- Missing flag → feature enabled
- Evaluation or DB error → feature enabled
- Invalid configuration → validation + rollback
- Null context handling:
  - Whitelist → deny
  - Percentage → random
- Cache consistency ensured using explicit eviction
- Concurrent updates handled transactionally

---

## Design Decisions & Assumptions

- Database: MySQL for persistence
- No external feature-flag libraries used
- No authentication on flag management APIs (as per assessment instructions)
- Fail-safe defaults: missing flag or exception → enabled
- Spring Cache used with explicit eviction on update/delete
- Context-aware evaluation:
  - Owner-specific actions → deterministic
  - Owner search → global / random
- Bonus: @FeatureToggle annotation using AOP (applied to owner_search)

---

## How to Run the Application Locally

### Prerequisites

- Java 17+ (JDK 17 or 21 recommended)
- Maven 3.8+ (or use mvnw)
- MySQL 8+
- MySQL Workbench / DBeaver / HeidiSQL
- Git
- IDE: IntelliJ IDEA (recommended) or Eclipse
- Browser (Chrome / Firefox)

---

### Step 1: Create Database

```sql
CREATE DATABASE petclinic;

### Step 2: Clone Repository

git clone https://github.com/YOUR-USERNAME/spring-petclinic.git
cd spring-petclinic


Note : Replace YOUR-USERNAME with your GitHub username.

Step 3: Configure MySQL

Edit file:

src/main/resources/application-mysql.properties

spring.datasource.url=jdbc:mysql://localhost:3306/petclinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD


Step 4: Run Application

Using IDE: Run Spring Boot application.

Using terminal:

mvn clean spring-boot:run -Dspring.profiles.active=mysql

Now Access application:

http://localhost:8080

(Optional) Change port:

server.port=9090



Step 5 : Feature Flag Testing (Postman)

Postman collection is provided with all APIs pre-configured.

View online:
https://documenter.getpostman.com/view/16470094/2sBXc8qj5J

Import into Postman:
https://www.postman.com/altimetry-geologist-78699286/java-assesment/collection/16470094-260bb195-a7a7-4185-be6b-a719b45b22f4



Example Feature Flag Flow


Initially, Boolean  flags exist in the database. All features work normally due to safe defaults.

Step 1: Disable Any Feature at Runtime

Example: Disable add_new_pet

Flag → add_new_pet → BOOLEAN → PUT – Disable (false)

Try adding a new pet in the browser.

Result: Feature is blocked.

Step 2: Re-Enable Feature Instantly

Flag → add_new_pet → BOOLEAN → PUT – Enable (true)

Refresh browser and try again.

Result: Pet is added successfully.

Step 3: Apply Same Flow to Other Features

Repeat the same steps for:

add_visit

owner_search

You can also test PERCENTAGE, WHITELIST, and BLACKLIST modes.



Important Notes

No application restart required

Changes take effect immediately

Cache is evicted automatically on updates

Missing or deleted flags default to enabled

