import numpy 
import sys

graphSize = int(sys.argv[1])

array = numpy.random.random_integers(1, 40, (graphSize,graphSize))

for i in range(0,graphSize):
    array[i][i] = 0
    for j in range(i,graphSize):
        array[i][j] = array[j][i]

output = "{"
for i in range(0,graphSize):
    output += "{"
    for j in range(0,graphSize-1):
        output+= str(array[i][j]) + ","
    output+= str(array[i][graphSize-1])
    output += "},\n"
output += "};"

print output
