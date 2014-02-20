all: server client

server: server.java
	javac server.java

client:	client.java
	javac client.java

clean:
	rm -f Server.class Client.class ServerThread.class Browse.class
