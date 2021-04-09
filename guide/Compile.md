How to compile yggdrasil
-
First, you must have Maven and git installed in your system.<br />
Then you have to open a terminal (cmd on Windows) and type the following commands.

Make a new folder in a chosen location :
```
mkdir Yggdrasil && cd Yggdrasil
```
Then clone the freectrl dependency, compile and install it :
```
git clone https://github.com/TW2/freectrl.git
cd freectrl
mvn compile
mvn install
```

Go out of this first dependency folder (go out freectrl) :
```
cd ..
```

Then clone the TimeLibrary dependency, compile and install it :
```
git clone https://github.com/TW2/TimeLibrary.git
cd TimeLibrary
mvn compile
mvn install
```

Go out of this second dependency folder (go out TimeLibrary) :
```
cd ..
```
Copy the YggLock JAR from Yggdrasil github and if you have maven 2.5 or above do :
```
mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=<path-to-file>
```
where ```<path-to-file>``` is your download location for ```ygglock-1.0.jar```.

If you don't have maven 2.5 or above or if you don't want to use this method, please do :
```
mvn install:install-file -Dfile=<path-to-file> -DpomFile=<path-to-pomfile>
```
where ```<path-to-file>``` is your download location for ```ygglock-1.0.jar``` and ```<path-to-pomfile>``` is a ```pom.xml``` file containing the following text :
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.wingate</groupId>
    <artifactId>ygglock</artifactId>
    <version>1.0</version>
    <packaging>jar</packaging>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>15</maven.compiler.source>
        <maven.compiler.target>15</maven.compiler.target>
    </properties>
    <build>
        <plugins>
            <plugin>
                <!-- Build an executable JAR -->
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <classpathPrefix>lib/</classpathPrefix>
                            <mainClass>org.wingate.ygglock.YggLock</mainClass>
                        </manifest>                        
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

Then clone yggdrasil, compile it and make the jar package :
```
git clone https://github.com/TW2/yggdrasil.git
cd yggdrasil
mvn compile
mvn package
```

Then you can run it.<br />
Light mode :
```
java -jar target/ygg-<version>.jar
```
Dark mode :
```
java -jar target/ygg-<version>.jar dark
```
Ensure ```<version>``` is the working version (see it in target).
