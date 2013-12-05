#!/bin/bash

for ((  i = 1 ;  i <= 8;  i++  ))
do
	rm -rf zzz*
	java LOGISTIC.Implementation.Hw6LogReg /home/bolei/Desktop/data/test/DATA-$i.TXT > /home/bolei/Desktop/data/test/logreg-result-$i.txt
done

for ((  i = 1 ;  i <= 8;  i++  ))
do
		rm -rf zzz*
		java SVM.Implementation.Hw6SVM /home/bolei/Desktop/data/test/DATA-$i.TXT > /home/bolei/Desktop/data/test/svm-result-$i.txt
done