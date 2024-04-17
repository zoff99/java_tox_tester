#! /bin/bash

toxid_file="/storage/emulated/0/Android/data/com.zoffcc.applications.trifa/files/001.txt"
adb=adb

x=0
while [ "$x""x" == "0x" ]; do
   "$adb" pull "$toxid_file"
   if [ -s 001.txt ]; then
      echo "ID found"
      cat 001.txt
      echo ""
      x=1
   else
      echo "waiting ..."
      sleep 2
   fi
done

id=$(cat 001.txt)
rm 001.txt
"$adb" shell "rm /storage/emulated/0/Android/data/com.zoffcc.applications.trifa/files/001.txt"

./do_run.sh "$id"
echo "done"


