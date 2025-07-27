# Water Intake Tracker 

A simple Spring Boot application to help you track your daily water intake and stay hydrated!

# Table of Contents
Features

Technologies Used

Getting Started

Prerequisites

Installation

Running the Application

Usage

# Features

User Registration & Authentication: Securely create an account and log in.

Daily Water Tracking: Log your water intake throughout the day.

Dashboard View: See your progress and current intake.

CRUD Operations: Add, update, and delete water intake records.

Responsive UI: Basic web interface for easy interaction.

# Technologies Used
Spring Boot: Framework for building the application.

Spring Security: For authentication and authorization.

Spring Data JPA: For database interaction.

H2 Database (or similar): In-memory database for development (can be configured for others).

Maven: Dependency management and build tool.

JSP (JavaServer Pages): For server-side rendering of views.

HTML, CSS, JavaScript: For the frontend user interface.

Apache Tomcat Embedded: Default web server.

# Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

# Prerequisites

Java 17 (or higher): Ensure you have the Java Development Kit (JDK) installed.

Maven 3.6+: You can download it from the Apache Maven website.

Git: For cloning the repository.

# Installation

Clone the repository:

Bash

git clone https://github.com/YOUR_USERNAME/waterintake.git
cd waterintake
Build the project:
Navigate to the project root directory and build the application using Maven:

Bash

mvn clean install

# Running the Application

Run from Maven:

Bash

mvn spring-boot:run
The application will start on http://localhost:8080.

Run from JAR:
After building, you can find the executable JAR in the target/ directory:

Bash

java -jar target/waterintake-0.0.1-SNAPSHOT.jar
(Note: The version 0.0.1-SNAPSHOT might vary based on your pom.xml).

# Usage

Access the Application: Open your web browser and go to http://localhost:8080.

Register: Click on the "Sign Up" link to create a new user account.

Login: Use your newly created credentials to log in.

Track Water: On the dashboard, you can log your daily water intake.

View Progress: The dashboard will display your current water intake for the day.

