ps awux | grep 'java .*network' | awk '{print "kill -9 " $2}' > kill_process.sh
