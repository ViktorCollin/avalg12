#!/bin/bash

echo 'Running tests ...'

rm -f result.dat

for f in data/*; do
	for i in $(seq 3); do
		echo $(BENCHMARK=1 /usr/bin/time java -cp ../bin TspMainRoundTwo < $f 2>&1) >> result.dat
		clear
		./eval.py
	done
done

clear
./eval.py
