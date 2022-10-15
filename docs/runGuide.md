# Running The Server

Guide to running the Space Game Server.

## Setup

### Prerequisites

Requires Java 11 to run.

### Setup Steps
1. Obtain server SSL password
2. Save password to a file named "ssl-server-password.txt" under the "rsrc" directory

## Building

Simply running `gradlew build` in the root directory will build the game server.

## Run Configurations

There are a few different ways to run the game. Each require a certain set of Configuration Parameters that are listed below.

### Gradle

Simply running `./gradlew run --args="[config params]"` with chosen Configuration Parameters.

The included Shell Script `debugrun.sh` uses this command and puts the server in Debug Mode and uses a Static Authentication Method.

### IntelliJ

Adding an Application run configuration in IntelliJ can also be used to run the server. The class that the run configuration needs to run is the following:
`com.andronikus.gameserver.app.AppStart`

## Configuration Parameters

The game accepts Java style arguments as configuration parameters.

The run parameters are meant to be Unix style however, meaning a parameter name is denoted by a `-` and the next word being the value of the parameter.

An example would be `-debugMode true`, which sets the parameter name of `debugMode` to the value of `true`. The following are the allowed configuration parameters.

| Parameter Name | Possible Values               | Description                                                    |
|----------------|-------------------------------|----------------------------------------------------------------|
| authMethod     | static, mysql (unimplemented) | The datasource the server uses for authenticating new clients. |
| debugMode      | true, *                       | Whether the server should start in Debug Mode.                 |
