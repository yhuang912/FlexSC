#!/usr/bin/python
import sys
import subprocess

for logN in range(5, 30) :
   workingpath = ""
   c = list();
   c.append("java -cp bin/:lib/* compiledlib.priority_queue.TestPriorityQueue #;")
#   c.append("java -cp bin/:lib/* compiledlib.stack.TestOramStack #;")
   print logN, "\t",
   for item in c :
      com =  item.replace("#", str(logN));
      res = subprocess.check_output(com, shell=True)
      if "java" in com:
         print res,
   print "\n",
