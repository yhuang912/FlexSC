import numpy 
import sys

arraySize =int(sys.argv[1])
array1 = sorted( numpy.random.random_integers(1, arraySize*3, (arraySize)))
array2 = sorted( numpy.random.random_integers(1, arraySize*3, (arraySize)))
output1 = "{"
output2 = "{"
for i in range(arraySize-1):
  output1 += str(array1[i]) + ","
  output2 += str(array2[i]) + ","
output1 += str(array1[arraySize-1])
output2 += str(array2[arraySize-1])
print output1 + "};"
print output2 + "};"
