import lab.moea.post as lmp
import lab.moea.viz as lmv


def post1():
    output_base_path = '../output/basic_sample/'

    pName = 'DTLZ3'
    aNames = ['MOEAD', 'NSGAII', 'NSGAIII', 'Random']

    base_path = '{}/{}'.format(output_base_path, pName)

    """Make mean accum data"""
    # lab.moea.makeMeanAccumDatas(base_path, aNames, ntrial)
    
    meanAccumDatas = lmp.loadMeanAccumDatas(base_path, aNames)
    lmv.plot_accumDatas(meanAccumDatas, 'GenerationalDistance', 'GD','upper left', (1, 1))

    # lmv.plot(meanAccumDatas['NSGAII']['NFE'], meanAccumDatas['NSGAII']['GenerationalDistance'], 'NFE', 'GD')


if __name__ == '__main__':
    post1()
