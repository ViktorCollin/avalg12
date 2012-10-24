#!/bin/bash

./number_generator.py | tee x | ./main | ./mul.py > y

diff x y
