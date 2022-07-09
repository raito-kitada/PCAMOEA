#!/bin/bash

export PATH=/home/tatsukawa/.p2/pool/plugins/org.eclipse.justj.openjdk.hotspot.jre.full.linux.x86_64_15.0.1.v20201027-0507/jre/bin:${PATH}

java -cp "../../bin:../../lib/*:/usr/share/java/jna.jar" basic_sample/basic_sample $1

