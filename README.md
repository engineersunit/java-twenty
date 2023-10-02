# java-twenty
Java 21 features implementation code.

## Where to get Java 21?
* Download Link 1: https://www.oracle.com/in/java/technologies/downloads/#java21
* Download Link 2: https://jdk.java.net/21/
* Read More here: https://openjdk.org/projects/jdk/21/

## How to build?

* Navigate to the code directory
```
cd D:\Java20\Java20\src\main\java (update the path)
```
* Compile the code with the two arguments — enable-preview and release
```
javac --enable-preview --release 21  -Xlint:preview org/sun/ghosh/MyFavoriteJavaSites.java
```
* The code needs a runtime argument of either "PLATFORM" or "VIRTUAL"
  * "PLATFORM" - Launches platform threads. 
  * "VIRTUAL" - Launches virtual threads.
* Run the code with enable-preview and "PLATFORM" argument to launch platform threads.
```
java --enable-preview org.sun.ghosh.MyFavoriteJavaSites PLATFORM
```
* Run the code with enable-preview and "VIRTUAL" argument to launch platform threads.
```
java --enable-preview org.sun.ghosh.MyFavoriteJavaSites VIRTUAL
```
