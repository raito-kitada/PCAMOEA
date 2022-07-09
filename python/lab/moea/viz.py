""" Visuzlization functions for multi-objective optimization results"""
import matplotlib.pyplot as plt
import matplotlib as mpl
from pandas.plotting import parallel_coordinates
import numpy as np


def linestyle_generator():
    """Generate default linestyle"""
    linestyles = ['solid', 'dotted', 'dashed', 'dashdot',
                  (0, (5, 1)),  # densely dashed
                  (0, (5, 5))]  # dashed
    while True:
        for linestyle in linestyles:
            yield linestyle


def marker_generator():
    """Generate default marker"""
    markers = ['o', 's', '^', 'D', 'v', '+']
    while True:
        for marker in markers:
            yield marker


def markersize_generator():
    """Generate default markersize"""
    while True:
        yield 6


def scatter3D_with_sphere(x, y, z, px, py, pz):
    """Draw a 3D scatter plot with sphere mesh

    Radius of sphere is one.

    Args:
        x (array-like): x coordinates of dominated solution
        y (array-like): y coordinates of dominated solution
        z (array-like): z coordinates of dominated solution
        px (array-like): x coordinates of non-dominated solution
        py (array-like): y coordinates of non-dominated solution
        pz (array-like): z coordinates of non-dominated solution

    Returns:
        Figure object
    """

    """Generate mesh for sphere"""
    angle = np.linspace(0, 0.5 * np.pi, 16)
    theta, phi = np.meshgrid(angle, angle)
    r = 1  # radius = 1
    X = r * np.cos(phi) * np.cos(theta)
    Y = r * np.cos(phi) * np.sin(theta)
    Z = r * np.sin(phi)

    amax = max(max(x), max(y), max(z))

    fig = plt.figure(figsize=(14, 7), dpi=100)  # figure size
    plt.rcParams["font.size"] = 22  # whole font size

    ax = fig.add_subplot(111, projection='3d')
    azim, elev = ax.azim, ax.elev
    azim = -45
    ax.view_init(elev, azim)

    ax.plot_surface(X, Y, Z, color='w', edgecolor='b', shade=False,
                    rstride=1, cstride=1, alpha=0.5)
    ax.scatter(x, y, z, s=10, c='b')
    # sc = ax.scatter(px, py, pz, s=10, c='r')
    ax.scatter(px, py, pz, s=10, c='r')
    ax.tick_params(axis='both', which='major', labelsize=12)
    ax.set_xlabel('$f_1$', labelpad=15, fontsize=18)
    ax.set_ylabel('$f_2$', labelpad=15, fontsize=18)
    ax.set_zlabel('$f_3$', labelpad=15, fontsize=18)

    ax.set_xlim3d(0, amax)
    ax.set_ylim3d(amax, 0)
    ax.set_zlim3d(0, amax)

    plt.show()

    return fig


def plot2D(x, y, xlabel, ylabel):
    """Draw simple line plot

    Args:
        x (array-like): Horizontal coordinates
        y (array-like): Vertical coordinates
        xlabel (str): Label of horizontal axis
        ylabel (str): Label of virtical axis

    Returns:
        Figure object
    """

    fig = plt.figure(figsize=(8, 7), dpi=100)  # figure size
    plt.rcParams["font.size"] = 22  # whole font size

    ax = fig.add_subplot(1, 1, 1)
    ax.set_axisbelow(True)

    plt.plot(x, y, marker='o', c='black')
    plt.grid(ls='--')

    plt.xlabel(xlabel)
    plt.ylabel(ylabel)

    plt.show()

    return fig


def scatter2D(x, y, xlabel, ylabel):
    """Draw scatter plot

    Args:
        x (array-like): Horizontal coordinates
        y (array-like): Vertical coordinates
        xlabel (str): Label of horizontal axis
        ylabel (str): Label of virtical axis

    Returns:
        Figure object
    """

    fig = plt.figure(figsize=(8, 7), dpi=100)  # figure size
    plt.rcParams["font.size"] = 22  # whole font size

    ax = fig.add_subplot(1, 1, 1)
    ax.set_axisbelow(True)

    plt.scatter(x, y, marker='o', c='black')
    plt.grid(ls='--')

    plt.xlabel(xlabel)
    plt.ylabel(ylabel)

    plt.show()

    return fig


def scatter2D_obj_history_with_referenceset(history, referenceSet):
    """Draw 2-dimensional objective space by history with reference set

    It's colored by NFE value.

    Args:
        history (dataframe): Data frame of history

    Returns:
        Figure object
    """

    fig = plt.figure(figsize=(8, 7), dpi=100)  # figure size
    plt.rcParams["font.size"] = 22  # whole font size

    ax = fig.add_subplot(1, 1, 1)
    ax.set_axisbelow(True)

    cmap = plt.get_cmap('rainbow')

    nfes = history.getNFESet()

    for i in nfes:
        objs = history.getObjByNFE(i)
        cs = history.getNFEByNFE(i)
        sc = plt.scatter(objs.f0, objs.f1, s=10, c=cs, cmap=cmap,
                         vmin=0, vmax=history.getNumberOfEvaluations())

    """Plot reference set"""
    ref_s = referenceSet.sort_values('f0')
    plt.plot(ref_s.f0, ref_s.f1)
    plt.grid(ls='--')

    plt.xlabel('$f_1$')
    plt.ylabel('$f_2$')

    plt.colorbar(sc, label='NFE')

    plt.show()
    
    return fig


def scatter2D_obj_history(history):
    """Draw 2-dimensional objective space by history

    It's colored by NFE value.

    Args:
        history (dataframe): Data frame of history

    Returns:
        Figure object
    """

    fig = plt.figure(figsize=(8, 7), dpi=100)  # figure size
    plt.rcParams["font.size"] = 22  # whole font size

    ax = fig.add_subplot(1, 1, 1)
    ax.set_axisbelow(True)

    cmap = plt.get_cmap('rainbow')

    nfes = history.getNFESet()

    for i in nfes:
        objs = history.getObjByNFE(i)
        cs = history.getNFEByNFE(i)
        sc = plt.scatter(objs.f0, objs.f1, s=10, c=cs, cmap=cmap,
                         vmin=0, vmax=history.getNumberOfEvaluations())

    plt.grid(ls='--')

    plt.xlabel('$f_1$')
    plt.ylabel('$f_2$')

    plt.colorbar(sc, label='NFE')

    plt.show()

    return fig


def scatter2D_var_history(history, variable_ids=None):
    """Draw 2-dimensional variable space by history

    It's colored by variable ID value.

    Args:
        history (dataframe): Data frame of history
        varialbe_ids (list): List of variables
                             If None, [i for i in range(history.nvar)] is set.

    Returns:
        Figure object
    """

    fig = plt.figure(figsize=(8, 7), dpi=100)  # figure size
    plt.rcParams["font.size"] = 22  # whole font size

    ax = fig.add_subplot(1, 1, 1)
    ax.set_axisbelow(True)

    cmap = plt.get_cmap('rainbow')

    nfes = history.df.NFE

    if variable_ids is None:
        variable_ids = [i for i in range(history.nvar)]

    vmin = min(variable_ids)
    vmax = max(variable_ids)

    for i, variable_id in enumerate(variable_ids):
        vstr = 'v' + str(variable_id)
        var = history.df[vstr]
        cs = [i for _ in range(len(history.df))]
        sc = plt.scatter(nfes, var, s=10, c=cs, cmap=cmap,
                         vmin=vmin, vmax=vmax)

    plt.grid(ls='--')

    plt.xlabel('NFE')
    plt.ylabel('Variable value')

    plt.colorbar(sc, label='Variable')

    plt.show()

    return fig


def parallel_coordinates_var_history(history):
    """Parallel coordinate plot of variables

    Args:
        history (dataframe): Data frame of history

    Returns:
        Figure object
    """

    fig, ax = plt.subplots(figsize=(8, 8), dpi=100)

    vars = history.df.loc[:, ['ITE'] + history.vstr]

    cmap = plt.get_cmap('rainbow')
    bounds = np.arange(vars.ITE.min(), vars.ITE.max())
    norm = mpl.colors.BoundaryNorm(bounds, 256)

    parallel_coordinates(vars, 'ITE', colormap=cmap, alpha=1, ax=ax)
    ax.legend_.remove()
    plt.colorbar(mpl.cm.ScalarMappable(norm=norm, cmap=cmap),
                 ax=ax, orientation='horizontal', label='ITE', alpha=1)
    plt.show()

    return fig


def plot_accumData(accumData, x_attr_name, y_attr_name, xlabel, ylabel):
    """Plot single accumulator dataset

    Args:
        accumData (set): accumulator dataset
        x_attr_name (str): Column name of data frame for horizontal axis
        y_attr_name (str): Column name of data frame for vertival axis
        xlabel (str): Name for horizontal axis
        ylabel (str): Name for vertical axis

    Returns:
        Figure object
    """

    fig = plt.figure(figsize=(8, 7), dpi=100)  # figure size
    plt.rcParams["font.size"] = 22  # whole font size

    ax = fig.add_subplot(1, 1, 1)
    ax.set_axisbelow(True)

    # cmap = plt.get_cmap('rainbow')

    x = accumData[x_attr_name]
    y = accumData[y_attr_name]
    plt.plot(x, y)

    plt.grid(ls='--')

    plt.xlabel(xlabel)
    plt.ylabel(ylabel)

    plt.show()

    return fig


def plot_accumDatas(accumDatas, labels, x_attr_name, y_attr_name, xlabel, ylabel,
                    loc=None, anchor=None, yscale=None, marker=None):
    """Plot accumulator datasets

    Args:
        accumDatas (set): Set of accumulator datasets
        x_attr_name (str): Column name of data frame for horizontal axis
        y_attr_name (str): Column name of data frame for vertival axis
        xlabel (str): Name for horizontal axis
        ylabel (str): Name for vertical axis
        loc: loc option for legend function
        anchor: bbox_to_anchor option for legend function
        yscale (str): sclale option (e.g. 'log')
        marker (boolean): plot marker if True

    Returns:
        Figure object
    """

    fig = plt.figure(figsize=(8, 7), dpi=100)  # figure size
    plt.rcParams["font.size"] = 22  # whole font size

    ax = fig.add_subplot(1, 1, 1)
    ax.set_axisbelow(True)

    linestyle_gen = linestyle_generator()
    marker_gen = marker_generator()
    markersize_gen = markersize_generator()

    for k, df in accumDatas.items():
        x = df[x_attr_name]
        y = df[y_attr_name]
        if marker is None:
            plt.plot(x, y, label=labels[k],
                     linestyle=next(linestyle_gen))
        else:
            plt.plot(x, y, label=labels[k],
                     linestyle=next(linestyle_gen),
                     marker=next(marker_gen),
                     markersize=next(markersize_gen))

    plt.grid(ls='--')

    plt.xlabel(xlabel)
    plt.ylabel(ylabel)

    if yscale == 'log':
        plt.yscale("log")

    if anchor is None:
        anchor = (1, 1)

    if loc is None:
        loc = 'upper left'

    ax.legend(loc=loc, bbox_to_anchor=anchor)

    plt.show()

    return fig


def savefig(fig, fname):
    """Save figure with tight option

    Args:
        fig (figure): Figure object
        fname (str): File name
    """
    fig.savefig(fname, bbox_inches="tight")