#|/bin/bash
pid=`pidof dataServer`
if [ "$pid" ];then
	kill $pid
fi
