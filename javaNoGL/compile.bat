@echo off
REM
REM Copyright 2024 USBONG
REM 
REM Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You ' may obtain a copy of the License at
REM
REM http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, ' WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing ' permissions and limitations under the License.
REM
REM @company: USBONG
REM @author: SYSON, MICHAEL B.
REM @date created: 20240719
REM @date updated: 20240725; from 20240724
REM
REM Note: faster to put necessary files inside "src" folder to solve location errors
REM

REM javac "./src/"*.java

REM javac -cp ./lib;jinput-2.0.10.jar;jinput-2.0.10-natives-all.jar; "./src/"*.java

REM javac -cp .;/net/java/games/input/jinput-2.0.10.jar;/net/java/games/input/jinput-2.0.10-natives-all.jar; "./src/"*.java

REM javac -cp ./src;jinput-2.0.10.jar;jinput-2.0.10-natives-all.jar; "./src/"*.java

rem javac -cp .;/src/net.java.games.input.jar; "./src/"*.java

REM cd src
REM "class file has wrong version 61.0, should be 52.0;
REM verifying recompiling using OpenJDK 1.8
REM javac -cp .;jinput.jar; *.java

REM compile jinput
REM javac "./src/net/java/games/input/"*.java

REM compiled with warnings; deprecation details
REM javac -Xlint:deprecation -Xlint:unchecked "./src/net/java/games/util/plugins/"*.java
REM javac "./src/net/java/games/util/plugins/"*.java

REM compiled jinput and jutils; plugins and input
REM javac "./src/net/java/games/util/plugins/"*.java "./src/net/java/games/input/"*.java

REM javac "./src/net/java/games/util/plugins/"*.java "./src/net/java/games/input/"*.java "./src/"*.java
 
REM note: transferred files from "jinput-master/plugins/windows" folder to "./src/net/java/games/input/"
REM code shall still need to be recompiled based on the OS to use jinput
javac "./src/net/java/games/util/plugins/"*.java "./src/net/java/games/input/"*.java "./src/"*.java


REM javac -cp .;jinput.jar "./src/"*.java

REM go to ./src
REM javac -cp .;jinput-2.0.10.jar;jinput-2.0.10-natives-all.jar; *.java
REM OK; but jinput uses the later JDK version 17 
