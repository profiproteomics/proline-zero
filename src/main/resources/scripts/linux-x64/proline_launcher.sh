#!/bin/bash

cd -- "$(dirname "$0")"


# read ini file
echo "***********************************************************************************"
export INI_FILE=proline_launcher.ini
echo "Read file $INI_FILE"
while IFS='= ' read var val
do
  if [[ $var == \[*] ]]; then
    section=$var
  elif [[ $val ]]; then
    # remove \r if any
    val=`echo $val|sed 's/\r//'`
    declare "$var$section=$val"
  fi
done < $INI_FILE

# set absolute directory pathes
export PROLINE_ZERO_HOME=$(pwd)
export CORTEX_NAME=Proline-Cortex-${cortex_version}
export CORTEX_HOME=$PROLINE_ZERO_HOME/$CORTEX_NAME
export HORNETQ_HOME=$CORTEX_HOME/hornetq_light-$hornetq_version
export HORNETQ_CONFIG_DIR=$HORNETQ_HOME/config/stand-alone/non-clustered
export STUDIO_HOME=$PROLINE_ZERO_HOME/ProlineStudio-$studio_version
export JAVA=$PROLINE_ZERO_HOME/$java_home/bin/java

# make sure java is executable
chmod +x $JAVA


# start H2
echo "***********************************************************************************"
echo "Start H2"
$JAVA -cp "$CORTEX_HOME/lib/*" org.h2.tools.Console -tcp -pg & pidh2=$!


# setup databases if needed
if [ ! -f $PROLINE_ZERO_HOME/setup.done ]; then

  echo "***********************************************************************************"
  echo "First run: setup databases"
  cd $CORTEX_HOME
  $JAVA -Xmx1024m -cp "lib/*:config" fr.proline.admin.RunCommand setup
  $JAVA -Xmx1024m -cp "lib/*:config" fr.proline.admin.RunCommand create_user -l proline -p proline

  touch $PROLINE_ZERO_HOME/setup.done
fi


# start hornetq
echo "***********************************************************************************"
echo "Start HornetQ"
cd $HORNETQ_HOME/bin

export CLUSTER_PROPS="-Djnp.port=1099 -Djnp.rmiPort=1098 -Djnp.host=localhost -Dhornetq.remoting.netty.host=0.0.0.0 -Dhornetq.remoting.netty.port=5445"
export JVM_ARGS="$CLUSTER_PROPS -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -Xms512M -Xmx1024M -Dhornetq.config.dir=$HORNETQ_CONFIG_DIR -Djava.util.logging.manager=org.jboss.logmanager.LogManager -Dlogging.configuration=file://$HORNETQ_CONFIG_DIR/logging.properties -Djava.library.path=./lib/linux-i686:./lib/linux-x86_64"
export CLASSPATH=$HORNETQ_CONFIG_DIR:../schemas/
for i in `ls ../lib/*.jar`; do
	CLASSPATH=$i:$CLASSPATH
done
$JAVA $JVM_ARGS -classpath $CLASSPATH -Dcom.sun.management.jmxremote org.hornetq.integration.bootstrap.HornetQBootstrapServer hornetq-beans.xml &
sleep 5


# start cortex
echo "***********************************************************************************"
echo "Start Proline-Cortex"
cd $CORTEX_HOME
chmod +x *.sh
export LANG=en_US.UTF-8
$JAVA -Xmx4G -XX:+UseG1GC -XX:+UseStringDeduplication -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=30 -cp "config:Proline-Cortex-$cortex_version.jar:lib/*" -Dlogback.configurationFile=config/logback.xml fr.proline.cortex.ProcessingNode & pid_cortex=$!
sleep 5


# start studio
echo "***********************************************************************************"
echo "Start Proline Studio"
cd $STUDIO_HOME/bin
chmod +x prolinestudio
./prolinestudio


# stop all and quit
echo "***********************************************************************************"
echo "Stop all processes and quit"
# stop cortex
kill -9 $pidpc $pid_cortex
# stop hornetq
touch $HORNETQ_CONFIG_DIR/STOP_ME
# stop h2
kill -9 $pidh2
sleep 5
echo "***********************************************************************************"



