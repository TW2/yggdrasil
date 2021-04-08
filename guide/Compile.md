How to compile yggdrasil (since version 1.2.3)
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
