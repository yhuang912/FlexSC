eval `ps awux | grep 'java .*network' | awk '{print "kill -9 " $2}'`
