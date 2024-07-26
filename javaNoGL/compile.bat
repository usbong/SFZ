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
REM @date updated: 20240726; from 20240725
REM
REM Note: faster to put necessary files inside "src" folder to solve location errors
REM go to ./src
REM javac -cp .;jinput-2.0.10.jar;jinput-2.0.10-natives-all.jar; *.java
REM OK; but jinput uses the later JDK version 17 
REM

javac "./src/"*.java
