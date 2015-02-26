for i in $(seq 0 $(($2 - 1)))
do
    java -Xmx10g test.harness.TestSpeed $1 $((61000 + $i )) &
done
