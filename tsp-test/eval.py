#!/usr/bin/env python

class Result:
    def __init__(self, fields):
        fields = line.split(' ')
        self.score = float(fields[0])
        self.time = float(fields[1]) # Real
    
    def __repr__(self):
        return str(self.score)

# Read data:
results = []
for line in open('result.dat').read().splitlines():
    results.append(Result(line))


total_time = 0
total_score = 0

for r in results:
    total_time += r.time
    total_score += r.score

mean_time = total_time / len(results)
mean_score = total_score / len(results)

print 'Mean time: %f' % mean_time
print 'Mean score: %f' % mean_score
