<img src="http://baczkowicz.pl/mqtt-spy/images/mqtt-spy-logo.png" align="left" hspace="30" vspace="5">

Welcome to the Eclipse Paho mqtt-spy source code repository.

## How to compile it under JDK25
- Install full jdk from https://bell-sw.com/pages/downloads/#jdk-25-lts
- Install maven
- Comipled by `mvn clean package -DskipTests`
- Run by `java -jar mqtt-spy-1.0.1-beta-b4-jar-with-dependencies.jar`

For more information on the available functionality, see the project's wiki at https://github.com/eclipse/paho.mqtt-spy/wiki.

The branching model aims to be as follows*:

  * master branch - where all the official releases live; tag for each release
  * development branch - where all the work is actually taking place
  * feature and release branches - used when required; for experimenting with new features and for major releases respectively

\* - this has been heavily influenced by http://nvie.com/posts/a-successful-git-branching-model/.
