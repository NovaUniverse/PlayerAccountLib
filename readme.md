# PlayerAccountLib
A plugin that allows players to create account to be used with [NovaAPILib](https://github.com/NovaUniverse/NovaAPILib)

## Maven
To add this to your maven project add the following to your pom.xml file in the `<repositories>` section
```xml
<repository>
	<id>novauniverse</id>
	<url>https://nexus2.novauniverse.net/repository/novauniverse/</url>
</repository>
```
and the following to the `<dependencies>` section
```xml
<dependency>
	<groupId>net.novauniverse</groupId>
	<artifactId>PlayerAccountLib</artifactId>
	<version>1.0.0-SNAPSHOT</version>
	<scope>provided</scope>
</dependency>
```

## Plugin download
Plugin can be downloaded from [our jenkins server](https://jenkins.novauniverse.net/job/PlayerAccountLib/lastBuild/)

You can also compile the plugin yourself by cloning this repo and running `mvn clean package`

## Demo
A demo of how to use this can be found [here](https://github.com/NovaUniverse/PlayerAccountLibDemo)