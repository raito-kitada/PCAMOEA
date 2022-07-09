""" Post-process functions for multi-objective optimization results"""
import math
import pandas as pd
#import paretoset


def calcGD_Sphere(objs):
    """ Calculate Generational Distance(GD) assuming that
    the PF is on a circumference of radius one.

    Args:
        objs (DataFrame): Objective values
    Returns:
        GD
    """

    gd = 0.0
    for index, row in objs.iterrows():
        gd += sum([v*v for v in row]) - 1
    return math.sqrt(gd) / len(objs)


class HistoryData:
    """DataFrame of history"""

    def __init__(self, path):
        # Name of history file
        self.name = path
        # Data frame of history
        self.df = pd.read_csv(path)

        # The number of objectives
        self.nobj = 0
        # The number of constraints
        self.ncon = 0
        # The number of variables
        self.nvar = 0
        # The number of attributes
        self.natr = 0

        for c in self.df.columns:
            if c.find('f') == 0:
                self.nobj += 1
            elif c.find('c') == 0:
                self.ncon += 1
            elif c.find('v') == 0:
                self.nvar += 1
            elif c.find('a') == 0:
                self.natr += 1

        # String list of objectives, constraints, variables, attributes
        self.fstr = ['f' + str(i) for i in range(0, self.nobj)]
        self.cstr = ['c' + str(i) for i in range(0, self.ncon)]
        self.vstr = ['v' + str(i) for i in range(0, self.nvar)]
        self.astr = ['a' + str(i) for i in range(0, self.natr)]

    def getName(self):
        return self.name

    def getNumberOfEvaluations(self):
        """Get the number of evaluation"""
        return self.df['NFE'].max()

    def getNumberOfIterations(self):
        """Get the number of iteration"""
        return self.df['ITE'].max()

    def getNFEList(self):
        """Get the list of NFE"""
        return list(self.df['NFE'])

    def getITEList(self):
        """Get the list if ITE"""
        return list(self.df['ITE'])

    def getNFESet(self):
        """Get the set of NFE"""
        return set(self.df['NFE'])

    def getITESet(self):
        """Get the set of ITE"""
        return set(self.df['ITE'])

    def getITEByNFE(self, NFE):
        """Get ITE list filtered by NFE value"""
        return self.df.loc[self.df.NFE == NFE, 'ITE']

    def getITEByITE(self, ITE):
        """Get ITE list filtered by ITE value"""
        return self.df.loc[self.df.ITE == ITE, 'ITE']

    def getNFEByNFE(self, NFE):
        """Get NFE list filtered by NFE value"""
        return self.df.loc[self.df.NFE == NFE, 'NFE']

    def getNFEByITE(self, ITE):
        """Get NFE list filtered by ITE value"""
        return self.df.loc[self.df.ITE == ITE, 'NFE']

    def getObjByNFE(self, NFE):
        """Get objective list filtered by NFE value"""
        return self.df.loc[self.df.NFE == NFE, self.fstr]

    def getObjByITE(self, ITE):
        """Get objective list filtered by ITE value"""
        return self.df.loc[self.df.ITE == ITE, self.fstr]

    def getConByNFE(self, NFE):
        """Get constraint list filtered by NFE value"""
        return self.df.loc[self.df.NFE == NFE, self.cstr]

    def getConByITE(self, ITE):
        """Get constraint list filtered by ITE value"""
        return self.df.loc[self.df.ITE == ITE, self.cstr]

    def getVarByNFE(self, NFE):
        """Get variable list filtered by NFE value"""
        return self.df.loc[self.df.NFE == NFE, self.vstr]

    def getVarByITE(self, ITE):
        """Get variable list filtered by ITE value"""
        return self.df.loc[self.df.ITE == ITE, self.vstr]

    def getObjAll(self):
        """Get all variable list"""
        return self.df.loc[:, self.ostr]

    def getConAll(self):
        """Get all variable list"""
        return self.df.loc[:, self.cstr]

    def getVarAll(self):
        """Get all variable list"""
        return self.df.loc[:, self.vstr]


def loadReferenceSet(path, nobj):
    """Load reference pareto-fronto set

    Args:
        path (str): path of reference set
        nobj (int): the number of objectives

    Returns:
        Reference Set
    """
    header = ['f' + str(i) for i in range(0, nobj)]
    return pd.read_csv(path, names=header, delimiter=' ')


def loadCsvData(path):
    """Load CSV dataset as dataframe"""
    return pd.read_csv(path, delimiter=',')


def calcMeanAccumData(path, ntrial):
    """Calculate mean from all trial results(accum_img.csv)

    Final path of accum_img.csv is 'path/[0, ntrial]/accum_img.csv'.

    Args:
        path (str): Base path of accum_img.csv
        ntrial (int): The number of trial
    Returns:
        meanAccumSet (DataFrame): Mean results
    """

    for i in range(0, ntrial):
        accum_path = '{}/{}/accum_img.csv'.format(path, str(i))
        print(accum_path)
        accumSet = loadCsvData(accum_path)
        if i == 0:
            meanAccumSet = accumSet
        else:
            meanAccumSet += accumSet

    meanAccumSet /= ntrial

    return meanAccumSet


def loadMeanAccumDatas(path, aNames):
    """Load mean_accm.csv for each algorithm

    Final path of mean_accm.csv is 'base_path/[aName]/mean_accm.csv'

    Args:
        path (str): Base path of mean_accm.csv
        aNames (list): Array of algorithm names

    Returns:
        meanAccumDatas (dict): Loaded mean data stored in dictitonary format
    """

    meanAccumDatas = {}
    for aName in aNames:
        print(aName)
        accum_path = '{}/{}/mean_accum.csv'.format(path, aName)
        meanAccumData = loadCsvData(accum_path)
        meanAccumDatas[aName] = meanAccumData

    return meanAccumDatas


def makeMeanAccumDatas(path, aNames, ntrial):
    """Calculate the mean from all trial results
    and save results into mean_accum.csv

    Final path of accum_img.csv is 'path/[aName]/[0, ntrial]/accum_img.csv'.

    Final path of mean_accm.csv is 'path/[aName]/mean_accm.csv'

    Args:
        path (str): Base path of input/output data
        aNames (list): Array of algorithm names
        ntrial (int): The number of trial
    """

    for aName in aNames:
        accum_path = '{}/{}'.format(path, aName)
        meanAccumData = calcMeanAccumData(accum_path, ntrial)
        output_path = '{}/{}/{}'.format(path, aName, 'mean_accum.csv')
        meanAccumData.to_csv(output_path, index=False)  # save results


def calc_nd(objs, sense=None):
    """Calculate non-dominated(Pareto) solutions

    Args:
        objs (DataFrame): Objective values
        sense (list): Optimal directions ('min' or 'max')

    Returns:
        Non-dominated solutions
    """
    if sense is None:
        ncol = len(objs.columns)
        sense = ['min' for i in range(ncol)]
    mask = paretoset(objs, sense=sense)
    return objs[mask]
