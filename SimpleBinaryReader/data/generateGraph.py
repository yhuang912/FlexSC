import numpy 
import sys

output = "{\n"
for graphSize in range(3,20):

  array = numpy.random.random_integers(1, 40, (graphSize,graphSize))

  for i in range(0,graphSize):
    array[i][i] = 0
    for j in range(i,graphSize):
        array[i][j] = array[j][i]

  if graphSize == 3:
    output += "#if MAX == 3\n"
  else :
    output += "#elif MAX == " + str(graphSize) + "\n"
  for i in range(0,graphSize):
    output += "{"
    for j in range(0,graphSize-1):
        output+= str(array[i][j]) + ","
    output+= str(array[i][graphSize-1])
    output += "},\n"

output += "else\n\tWTF\n#endif\n"
output += "};"

print output
