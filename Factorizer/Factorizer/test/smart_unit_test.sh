#!/bin/zsh
timeout=15
ulimit -St $timeout
rm y
while read line; do
	(TIMEFORMAT="%U"; time echo $line | ./main ) 2>> y >> y
	#{ time echo $line | ./main; } 2> y
	#result=$(echo $line | ./main || echo fail, $timeout)
	#echo $result | tee -a y
done < z

diff z y
