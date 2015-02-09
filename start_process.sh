java -cp .:$1/commons-io-2.4.jar network.Master 30000 50000 54321 true $2 $3 $5 $6 $7 $8 $9 >> 'out/'$4/$4'_m1' &
#java -cp .:$1/commons-io-2.4.jar network.Master 40000 55000 54321 false $2 $3 $5 $6 $7 $8 $9 >> 'out/'$4/$4'_m2' &
for i in $(seq 0 $(($2 - 1)))
#for i in $(seq 0 7)
do
  java -Xmx4200m -cp .:$1/commons-io-2.4.jar network.Machine $((30000 + $i )) $i $((35000 + $i )) true $3 0 $5 $2 $6 $7 $8 $9 >> 'out/'$4/$4'_alice_'$i &
done
#for i in $(seq 0 $(($2 - 1)))
#do
#  java -Xmx3700m -cp .:$1/commons-io-2.4.jar network.Machine $((40000 + $i )) $i $((35000 + $i )) false $3 0 $5 $2 $6 $7 $8 $9 >> 'out/'$4/$4'_bob_'$i &
#done

