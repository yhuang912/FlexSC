#!/usr/bin/python
import subprocess
import os.path
import time
inputLength = 256
subprocess.call("./clear_ports.sh", shell=True)
out_dir = 'pr_var_12'
it = 100
if not os.path.exists('out/' + out_dir):
    os.makedirs('out/' + out_dir)
while (inputLength <= 32768):
  for i in range(2, 5):
    subprocess.call(["./start_process.sh ../lib " + str(1 << i) + " " + str(inputLength) + " " + out_dir + " PageRank 12 REAL 100 " + str(it)], shell=True)
    while(not os.path.isfile('mutex.txt')):
       time.sleep(60)
    os.remove('mutex.txt')
  inputLength = inputLength * 2
