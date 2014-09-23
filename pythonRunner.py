import subprocess
import os.path
import time
inputLength = 32
while (inputLength < 262144):
  for i in range(1, 2):
    subprocess.call(["./start_process.sh /home/kartik/code/flexsc_final_workspace/first/bin " + str(1 << i) + " " + str(inputLength)], shell=True)
    while(not os.path.isfile('mutex.txt')):
      time.sleep(60)
    os.remove('mutex.txt')
  inputLength = inputLength * 2
