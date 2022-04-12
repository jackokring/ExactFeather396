#!/bin/bash
echo "Data tools"
#echo $(basename $0) $*
#-jar option replaced by -cp
java -cp ef396-1.0.0.jar uk/co/kring/ef396/data/Data.java $(basename $0) $*
exit $?