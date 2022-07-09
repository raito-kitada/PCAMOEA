import os
import lab.moea

if __name__ == '__main__':

    output_base_path = '../../output/basic_sample/'

    pName = 'DTLZ3'
    # aNames = ['CMAES','eMOEA','eNSGAII','GDE3','IBEA','MOEAD','NSGAII','NSGAIII','OMOPSO','PAES','PESA2','Random','SPEA2']
    aNames = ['MOEAD','NSGAII','NSGAIII','Random']

    ntrial = 10

    base_path = output_base_path + '/' + pName
    lab.moea.makeAccumDatas(base_path, aNames, ntrial)

