#!/bin/sh

java -cp "Proline-Cortex-${cortex.version}/lib/*" org.h2.tools.Console -tcp -pg & pid=$!

cd Proline-Cortex-${cortex.version}
java -Xmx1024m -cp "lib/*;config" fr.proline.admin.RunCommand setup
java -Xmx1024m -cp "lib/*;config" fr.proline.admin.RunCommand create_user -l proline -p proline

(sleep 5 && kill -9 $pid) &