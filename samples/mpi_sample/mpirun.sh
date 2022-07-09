#!/bin/bash

cd evaluator 
mpiexec -n 4 ./gaiamop_mpi --inter-dir=/home/tatsukawa/Work/Repos/MOEAFramework/samples/mpi_sample
