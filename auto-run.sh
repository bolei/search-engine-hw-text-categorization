#!/bin/bash

for ((  i = 6 ;  i <= 8;  i++  ))
do
	./clean-logreg.sh
	java MyRun ~/Desktop/test/DATA$i.TXT > ~/Desktop/test/result$i.txt
done
