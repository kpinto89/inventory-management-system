# Inventory Management System

A Spring Boot 3 inventory management application with a Thymeleaf UI, Spring Security login/logout, Spring Data JPA, and an in-memory H2 database.

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Spring MVC
- Thymeleaf
- Spring Security
- Spring Data JPA
- H2 Database
- Gradle
- Bootstrap 5

## Features

- Secure login and logout flow
- Dashboard with:
  - total product count
  - low-stock count
  - total inventory value
  - recent products
  - low-stock alerts
- Product management:
  - list all products
  - search by name or SKU
  - filter by category
  - add a product
  - edit a product
  - view product details
  - delete a product
- Low-stock screen for products at or below threshold
- H2 database console enabled for local inspection
- Seeded sample users and products on startup

## Default Login Credentials

These users are seeded at application startup in `src/main/java/com/demo/config/DataInitializer.java`.

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| User | `user` | `user123` |

> Note: both roles can currently log into the application. The current security configuration requires authentication for application pages, but it does not define role-specific page restrictions.

## Runtime Configuration

Current application settings are defined in `src/main/resources/application.properties`.

- Application port: `8081`
- H2 console path: `/h2-console`
- JDBC URL: `jdbc:h2:mem:inventorydb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- Hibernate DDL mode: `create-drop`

This means the database is in-memory and recreated on startup.

## Important JDK Note

The project currently includes `gradle.properties` with this setting:

```properties
org.gradle.java.home=C:\Users\t_kevinpin\.jdks\ms-17.0.18
```

That works on the current machine, but on another computer you may need to either:

- update `gradle.properties` to your local Java 17 path, or
- remove that line and rely on your `JAVA_HOME`

## Project Structure

```text
inventory-management-system/
├── build.gradle
├── gradle.properties
├── settings.gradle
├── gradlew
├── gradlew.bat
└── src/
    ├── main/
    │   ├── java/com/demo/
    │   │   ├── InventoryManagementApplication.java
    │   │   ├── config/
    │   │   ├── controller/
    │   │   ├── model/
    │   │   ├── repository/
    │   │   └── service/
    │   └── resources/
    │       ├── application.properties
    │       └── templates/
    │           ├── auth/
    │           ├── dashboard.html
    │           ├── fragments/
    │           └── products/
    └── test/
        ├── java/
        └── resources/
```

## How to Run

### Windows PowerShell

From the project root:

```powershell
cd "C:\Users\t_kevinpin\IdeaProjects\inventory-management-system"
.\gradlew.bat bootRun
```

### Build the project

```powershell
cd "C:\Users\t_kevinpin\IdeaProjects\inventory-management-system"
.\gradlew.bat build
```

### Clean and rebuild

```powershell
cd "C:\Users\t_kevinpin\IdeaProjects\inventory-management-system"
.\gradlew.bat clean build
```

### Run the packaged JAR

Build first, then run:

```powershell
cd "C:\Users\t_kevinpin\IdeaProjects\inventory-management-system"
.\gradlew.bat bootJar
java -jar build\libs\inventory-management-system-1.0-SNAPSHOT.jar
```

## IntelliJ IDEA

You can also run the app from IntelliJ by starting `InventoryManagementApplication`.

## Application URLs

After startup, open:

- App home: [http://localhost:8081](http://localhost:8081)
- Product list: [http://localhost:8081/products](http://localhost:8081/products)
- Low stock: [http://localhost:8081/products/low-stock](http://localhost:8081/products/low-stock)
- H2 console: [http://localhost:8081/h2-console](http://localhost:8081/h2-console)

## H2 Console Settings

Use these values in the H2 console:

- **JDBC URL:** `jdbc:h2:mem:inventorydb`
- **User Name:** `sa`
- **Password:** *(leave blank)*

## Common Issues

### Port 8081 already in use

If the app fails because port `8081` is occupied, stop the existing process or run this in PowerShell:

```powershell
$pid = (netstat -ano | findstr ":8081" | findstr "LISTENING" | ForEach-Object { ($_ -split '\s+')[-1] } | Select-Object -First 1)
Stop-Process -Id $pid -Force
```

### Java version problems

This project is configured for Java 17. Check your Java version with:

```powershell
java -version
```

If Gradle still picks the wrong JDK, update `gradle.properties` or set `JAVA_HOME` to a Java 17 installation.

## Notes

- Product and user seed data are inserted during startup.
- Because H2 is in-memory and Hibernate uses `create-drop`, data does not persist after shutdown.
- The `src/test` directories exist, but there are currently no automated tests in the project.

