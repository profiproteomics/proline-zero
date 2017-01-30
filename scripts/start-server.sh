#!/bin/sh

java -cp "Proline-Cortex-${cortex.version}/lib/*" org.h2.tools.Console -tcp -pg &

pushd .
cd hornetq-${hornetq.version}/bin
chmod +x *.sh
./run.sh &
popd

sleep 5
pushd .
cd Proline-Cortex-${cortex.version}
chmod +x *.sh
./start_cortex.sh &
popd
