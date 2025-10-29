# SDK Library Directory

## Purpose
This directory contains the Vodafone STEP Java SDK jar file required for V2X communication.

## Setup
Place the Vodafone STEP SDK jar file in this directory with the following name:

```
vodafone-step-sdk.jar
```

## Important
The SDK jar file is referenced in the `pom.xml` as a system-scoped dependency:

```xml
<dependency>
    <groupId>com.vodafone</groupId>
    <artifactId>step-sdk</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/vodafone-step-sdk.jar</systemPath>
</dependency>
```

## Note
If the SDK jar file is not present, the project will still compile, but certain runtime
functionality will need to be implemented once the SDK is available.

For development and testing purposes, a mock implementation is used until the actual SDK
is integrated.
