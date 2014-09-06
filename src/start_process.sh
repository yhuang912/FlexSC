java network.Master 30000 50000 54321 true &
echo 'hello'
java -cp .:/home/kartik/code/flexsc_final_workspace/first/bin/commons-io-2.4.jar network.Master 40000 55000 54321 false &
java network.Machine 30000 0 35000 true &
java network.Machine 30001 1 35001 true &
java network.Machine 30002 2 35002 true &
java network.Machine 30003 3 35003 true &
java -cp .:/home/kartik/code/flexsc_final_workspace/first/bin/commons-io-2.4.jar network.Machine 40000 0 35000 false &
java -cp .:/home/kartik/code/flexsc_final_workspace/first/bin/commons-io-2.4.jar network.Machine 40001 1 35001 false &
java -cp .:/home/kartik/code/flexsc_final_workspace/first/bin/commons-io-2.4.jar network.Machine 40002 2 35002 false &
java -cp .:/home/kartik/code/flexsc_final_workspace/first/bin/commons-io-2.4.jar network.Machine 40003 3 35003 false
