#!/bin/bash
echo "Data tools"
#echo $(basename $0) $*
#-jar option replaced by -cp
# removed uk/co/kring/ef396/data/Data.java $(basename $0)
java -jar ef396-1.0.0.jar $*
exit $?