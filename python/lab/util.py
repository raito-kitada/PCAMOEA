""" Utility functions collections"""
import matplotlib.pyplot as plt
import numpy as np


def plot_color_map(cmap_name):
    """Draw a color map with RGBA

    Args:
        cmap_name (string): colormap name (e.g. 'rainbow')
    """

    # construct color map
    cm = plt.get_cmap(cmap_name)

    # construct R,G,B,A array
    Rs = []
    Gs = []
    Bs = []
    As = []

    for n in range(cm.N):
        Rs.append(cm(n)[0])
        Gs.append(cm(n)[1])
        Bs.append(cm(n)[2])
        As.append(cm(n)[3])

    # 1d array
    gradient = np.linspace(0, 1, cm.N)
    # 2d array (for imshow)
    gradient_array = np.vstack((gradient, gradient))

    # plot ccmap
    fig = plt.figure(figsize=(8, 7), dpi=100)
    plt.rcParams["font.size"] = 22

    ax = fig.add_axes((0.1, 0.3, 0.8, 0.6))
    ax.plot(As, 'k', label='A')
    ax.plot(Rs, 'r', label='R')
    ax.plot(Gs, 'g', label='G')
    ax.plot(Bs, 'b', label='B')
    ax.set_xlabel('Index')
    ax.set_xlim(0, cm.N)
    ax.set_title(cmap_name)
    ax.legend()

    ax2 = fig.add_axes((0.1, 0.1, 0.8, 0.05))
    ax2.imshow(gradient_array, aspect='auto', cmap=cm)
    ax2.set_axis_off()

    plt.show()
