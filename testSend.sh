for i in $(seq 0 $(($2 - 1)))
do
    java -Xmx10g test.harness.TestSend $1 $((62000 + $i )) &
done
