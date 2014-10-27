import subprocess
import os.path
import time
inputLength = 32
subprocess.call("./clear_ports.sh", shell=True)
subprocess.call("./kill_process.sh", shell=True)
while (inputLength < 32):
  for i in range(1, 2):
    subprocess.call(["./start_process.sh /home/kartik/code/flexsc_final_workspace/first/bin " + str(1 << i) + " " + str(inputLength) + " out1.out"], shell=True)
    while(not os.path.isfile('mutex.txt')):
      time.sleep(60)
    os.remove('mutex.txt')
  inputLength = inputLength * 2
