# DAI-Practical-work-2

```
    _   __      __                       
   / | / /___  / /_____  ____ ___  ____  ____ 
  /  |/ / __ \/ //_/ _ \/ __ `__ \/ __ \/ __ \
 / /|  / /_/ / ,< /  __/ / / / / / /_/ / / / /
/_/ |_/\____/_/|_|\___/_/ /_/ /_/\____/_/ /_/

```
                                              

## Table of contents
- [Authors](#authors)
- [Overview](#overview)
- [Usage](#usage)
- [Contribute](#contribute)

## Authors

- [Fabien Léger](https://github.com/Schokiiiiiiii)
- [Samuel Dos Santos](https://github.com/Samurai-05)

## Overview

This repository contains the second practical work. The goal is to create a client-server application. We chose to create a small turn based CLI game called ***Nokemon***. The game is composed of a single `.jar` file that can be lauched either as a server or a player client.

## Usage

### Clone the repository

- `git clone https://github.com/Schokiiiiiiii/DAI-Practical-work-2.git`

### Package the application

#### IntelliJ IDEA

In IntelliJ IDEA, you can open the project and use run button with the `Package application as JAR file` to package the application.

#### Otherwise

You can also use the following command to package the application at the root of the project:
```Bash
./mvnw clean package
```

### Run the application

#### Server

Default port and host (localhost:7270)

```Bash
java -jar target/java-tcp-programming-1.0-SNAPSHOT.jar server
```

Specify port

```Bash
java -jar target/java-tcp-programming-1.0-SNAPSHOT.jar server -p=<port>
```

#### Client

Default host (localhost:7270)

```Bash
java -jar target/java-tcp-programming-1.0-SNAPSHOT.jar client
```

Specify host and port

```Bash
java -jar target/java-tcp-programming-1.0-SNAPSHOT.jar client -H=<host> -p=<port>
```

## Docker version

Download the docker image from [...](...)

## Contribute

Feel free to contribute to this project. </br>
Please open an issue before submitting a pull request.</br>
Only signed commits will be accepted, more information [here](https://docs.github.com/en/authentication/managing-commit-signature-verification/signing-commits).
