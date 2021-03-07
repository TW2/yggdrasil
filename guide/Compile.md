How to compile Yggdrasil (since version 1.2.3) :
-
mkdir Yggdrasil && cd Yggdrasil

git clone https://github.com/TW2/freectrl.git
cd freectrl
mvn compile
mvn install

cd ..

git clone https://github.com/TW2/TimeLibrary.git
cd TimeLibrary
mvn compile
mvn install

cd ..

git clone https://github.com/TW2/yggdrasil.git
cd yggdrasil
mvn compile
mvn package

java -jar target/ygg-1.2.3.jar
