#!/usr/bin/env python
# -*- encoding: utf8 -*-
from sys import stdin


lines = stdin.read().splitlines();

x = 1

for line in lines:
    if len(line) == 0:
        print x if x > 0 else 'fail'
        x = 1
    elif line == 'fail':
        x = 0
    else:
        x *= int(line)



