#!/bin/sh

# chmod 700 ./runAsyncServer.sh

javac -d bin src/UDPEchoServer.java
javac -d bin src/UDPEchoClient.java


x-terminal-emulator -e java -cp bin UDPEchoServer
x-terminal-emulator -e java -cp bin UDPEchoClient
