# SFT6310 Task 7

Okay so let me explain. We needed to finally begin the usage of Spring Security module and implement NoSQL database
interaction specifically with MongoDB. In the previous task, I've already had a working algorithm for authentication and
authorization by using my own custom tokens and a typical table in PostgreSQL DB. But by the requirements the implementation
for JWT is required, so here I am, still using pretty much the same algorithm (as JWT works with it), but now instead of
a random string it is JWT which just contains a SessionId inside of it.

## Structure

The structure still was left pretty much untouched from the previous module, but now, in Session Repository it uses MongoDB
and Sessions are generated using JWT instead of my previous random 32 byte method with SHA-256 hashing.

## Warning

Warning also stays the same, because I still do not know how safe this truly is.

While a lot of methods for improving the overall security is in place, I cannot guarantee the same level of security as
in more advanced algorithms and structures.

## Installation

The application was tested and developed on Java 25 with Gradle and Windows 11, other versions were not tested.

Clone the repository on both servers from git clone: [https://github.com/ManulProgramming/SFT6310-Task-7](https://github.com/ManulProgramming/SFT6310-Task-7).

Go to the /SFT6310-Task-7 and build the Java application using IntelliJ Idea or manually by using build.gradle.

## Usage

Run the application and go to localhost:8080 to send various requests.

## Notes:

This application is created for educational purposes only. It should not be considered as a serious application,
but rather as a completed Laboratory work for this course.

## License:

[Apache2](https://github.com/ManulProgramming/SFT6310-Task-7/blob/main/LICENSE)