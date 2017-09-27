#!/bin/sh

cd -- "$(dirname "$0")"

# define local path
export CORTEX=Proline-Cortex-0.4.0-SNAPSHOT
export STUDIO=ProlineStudio-1.2.0-SNAPSHOT
# warning: other scripts are using java, make sure they use $JAVA instead of `java` (or at least use the given JAVA_HOME variable)
export JAVA_HOME=$(pwd)/$STUDIO/jre
export JAVA=$JAVA_HOME/bin/java
chmod +x $JAVA
#if test -z "$JAVA_HOME" ; then
#  JAVA=java
#else
#  JAVA="$JAVA_HOME/bin/java"
#fi


# start H2
$JAVA -cp "$CORTEX/lib/*" org.h2.tools.Console -tcp -pg & pidh2=$!

# setup databases if needed
if [ ! -f setup.done ]; then

  cd $CORTEX
  $JAVA -Xmx1024m -cp "lib/*:config" fr.proline.admin.RunCommand setup
  $JAVA -Xmx1024m -cp "lib/*:config" fr.proline.admin.RunCommand create_user -l proline -p proline

  cd ..
  touch setup.done
fi

# start hornetq
cd $CORTEX/hornetq_light-2.4.0.Final/bin
chmod +x *.sh
./run.sh & pidhq=$!
sleep 5
cd ../../..

# start cortex
cd $CORTEX
chmod +x *.sh
./start_cortex.sh & pidpc=$!
# catch cortex real pid otherwise it may not stop by itself
pid_cortex=$(pgrep -P $pidpc)
sleep 5
cd ..

# start studio
cd $STUDIO/bin/
chmod +x prolinestudio
./prolinestudio
cd ../..

# stop cortex
kill -9 $pidpc $pid_cortex
# stop hornetq
cd $CORTEX/hornetq_light-2.4.0.Final/bin
./stop.sh
cd ../..
# stop h2
kill -9 $pidh2
sleep 5

