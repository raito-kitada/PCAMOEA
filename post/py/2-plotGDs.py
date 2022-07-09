import os
import lab.moea

if __name__ == '__main__':

    output_base_path = '../../output/basic_sample/'

    pName = 'DTLZ3'
    # aNames = ['CMAES','eMOEA','eNSGAII','GDE3','IBEA','MOEAD','NSGAII','NSGAIII','OMOPSO','PAES','PESA2','Random','SPEA2']
    aNames = ['MOEAD','NSGAII','NSGAIII','Random']
    # aNames = ['NSGAII']


    base_path = output_base_path + '/' + pName
    meanAccumDatas = lab.io.loadAccumDatas(base_path, aNames)

    lab.moea.plot_accumDatas(meanAccumDatas, 'GenerationalDistance', 'GD', anchor=(2.05,1.04))

