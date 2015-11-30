#|/bin/bash
pid=`pidof dataServer`
if [ -z "$pid" ]; then
	cd /home/ubuntu/dataServer
	exec -a dataServer java -Xmx1g -jar /home/ubuntu/dataServer/dataServer.jar > /dev/null &
fi
