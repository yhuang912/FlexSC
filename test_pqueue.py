#!/usr/bin/python
import sys
import subprocess

workingpath = ""
c = list();
c.append("cp ./src/compiledlib/cpp/priority_queue.cpp ./tmppq.cpp");
c.append("sed -i -e \'s/32/#/g\' ./src/compiledlib/stable/priority_queue.cpp")
c.append("sed -i -e \'s/typedef key_t = ints_#;/typedef key_t = ints_32;/g\' ./src/compiledlib/stable/priority_queue.cpp")
c.append("/Users/wangxiao/compile.sh ./src/compiledlib/stable/priority_queue.cpp > tmpfile;")
c.append("sed -i -e \'s/return sum;/return sum+32;/g\' ./src/compiledlib/PriorityQueueNode.java")
c.append("sed -i -e \'s/this.root = new NodeId(env, lib);/this.root = new NodeId(env, lib);root.id=env.inputOfAlice(Utils.fromInt(1, #));/g\' ./src/compiledlib/PriorityQueue.java");

c.append("/Users/wangxiao/git/FlexSC_rc/compile.sh &> tmpfile;")
c.append("java -cp bin/:lib/* compiledlib.TestPriorityQueue #;")
c.append("java -cp bin/:lib/* compiledlib.TestORAMPriorityQueue #;")
#c.append("java -cp bin/:lib/* compiledlib.TestCUMStack #;")
#c.append("java -cp bin/:lib/* compiledlib.TestOramStack #")
c.append("cp ./tmppq.cpp ./src/compiledlib/stable/priority_queue.cpp")
c.append("rm tmpfile;")
logN = sys.argv[1];
print logN, "\t",
for item in c :
   com =  item.replace("#", logN);
   res = subprocess.check_output(com, shell=True)
   if "java" in com:
      print res,
print "\n",
