#!/bin/sh
if [ -z $1 ]
then
	uid=1
else
	uid=$1
fi
i=1
m=10
n=15
o=20
p=3000
max=300
server="localhost:8080/wenergy"
#server="www.wenergy-project.de"
echo "Time: to connect / start transfer / total time"
while [ $i -le $max ]
do
p1=$(expr $i \* $m)
p2=$(expr $i \* $n)
p3=$(expr $i \* $o)
b=$(expr $p - $i)
result=$(curl -g -o /dev/null -s -w %{time_connect}:%{time_starttransfer}:%{time_total} http://$server/api/consumption?json={\"id\":$uid\,\"p1\":$p1\,\"p2\":$p2\,\"p3\":$p3\,\"b\":$b})
stat=$(echo $result | gawk -F: '{ print $1" / "$2" / "$3}')
echo $stat
i=$(expr $i + 1)
done

