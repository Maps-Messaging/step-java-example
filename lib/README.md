# SDK Library Directory

## Purpose
This directory contains the Vodafone V2X Java SDK jar file (v2xsdk4java) required for V2X communication with the STEP platform.

## Setup
Place the Vodafone V2X SDK jar file in this directory with the following name:

```
v2xsdk4java-3.1.0.jar
```

## Important
The SDK jar file is referenced in the `pom.xml` as a system-scoped dependency:

```xml
<dependency>
    <groupId>com.vodafone</groupId>
    <artifactId>v2xsdk4java</artifactId>
    <version>3.1.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/v2xsdk4java-3.1.0.jar</systemPath>
</dependency>
```

## SDK Documentation
The SDK user guide is available in the `doc/` directory:
- `doc/Java V2X SDK - User Guide 3.1.0.pdf`

## Package
The SDK provides the following main package:
- `com.vodafone.v2xsdk4javav2.facade` - Main SDK facade classes

## Note
The SDK jar file must be present for the application to compile and run. Without it, Maven will fail during the build process.

To obtain the SDK jar file, contact Vodafone or download it from the official STEP platform resources.
