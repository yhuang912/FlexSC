for i in $(seq 0 $(($2 - 1)))
do
    java -Xmx10g test.harness.TestCompute $1 $((63000 + $i )) &
done
