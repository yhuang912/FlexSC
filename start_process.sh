java network.Master 30000 50000 54321 true $2 $3 &
java -cp .:$1/commons-io-2.4.jar network.Master 40000 55000 54321 false $2 $3 &
for i in $(seq 0 $(($2 - 1)))
do
  java network.Machine $((30000 + $i )) $i $((35000 + $i )) true &
done
for i in $(seq 0 $(($2 - 1)))
do
  java -cp .:$1/commons-io-2.4.jar network.Machine $((40000 + $i )) $i $((35000 + $i )) false &
done

