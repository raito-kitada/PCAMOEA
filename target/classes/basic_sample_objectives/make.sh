#!/bin/bash

export PATH=/home/tatsukawa/.p2/pool/plugins/org.eclipse.justj.openjdk.hotspot.jre.full.linux.x86_64_15.0.1.v20201027-0507/jre/bin:${PATH}

javac -cp "../../bin:../../lib/*:/usr/share/java/jna.jar" basic_sample.java 
javac -cp "../../bin:../../lib/*:/usr/share/java/jna.jar" solveProblem.java 
