package lab.moea.util.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;

import lab.moea.operator.real.PODOP;

public class SimplePIO {
	private String SEP = ",";
	
	private String path;
	private BufferedWriter history;
	private boolean is_first;
	
	public SimplePIO(String path) {
		setPath(path);
	}
	
	/**
	 * Set path for output
	 * 
	 * @param path string of output path
	 */
	public void setPath(String path) {
		this.path = path;

		try {
			Path p = Paths.get(path);
			if (!Files.exists(p)) {
				Files.createDirectories(p);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Get path for output
	 * 
	 * @return path for output
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * 
	 * 
	 * @param fname
	 * @param append
	 */
	public void openHistoryFile(String fname, boolean append) {
		try {
			if (history != null) {
				history.close();
			}
			
			history = new BufferedWriter(new FileWriter(new File(path + fname), append));
			is_first = true;
		} catch (IOException e) {
			Logger.getLogger("setHistoryFileName").log(Level.SEVERE, null, e);
		}
	}
	
	/**
	 * 
	 */
	public void closeAll() {
		if (history != null) {
			try {
				history.close();
			} catch (IOException e) {
				Logger.getLogger(SimplePIO.class.getEnclosingClass().getName()).log(Level.SEVERE, null, e);
			}
		}
	}
	
	/**
	 * 
	 * @param gen
	 * @param nfe
	 * @param solutions
	 */
	public void writeHistory(int nfe, int ite, Iterable<Solution> solutions) {
		try {	
			for (Solution solution : solutions) {
				int nobj = solution.getNumberOfObjectives();
				int ncon = solution.getNumberOfConstraints();
				int nvar = solution.getNumberOfVariables();
				
				if (is_first) {
					writeHeader(nobj, ncon, nvar, history);					
					is_first = false;
				}
				
				history.write(Integer.toString(nfe));
				history.write(SEP);				
				history.write(Integer.toString(ite));
				
				for (int i = 0; i < nobj; i++) {
					history.write(SEP);
					history.write(Double.toString(solution.getObjective(i)));
				}
				
				for (int i = 0; i < ncon; i++) {
					history.write(SEP);
					history.write(Double.toString(solution.getConstraint(i)));
				}
				
				for (int i = 0; i < nvar; i++) {
					history.write(SEP);
					history.write(Double.toString(((RealVariable) solution.getVariable(i)).getValue()));
				}
				
				history.write(SEP);
				history.write(Integer.toString(PODOP.getNumElem()));

				history.newLine();
			}
		} catch (IOException e) {
			Logger.getLogger(SimplePIO.class.getEnclosingClass().getName()).log(Level.SEVERE, null, e);
        }
	}
	
	public void writeSolutions(String fname, Iterable<Solution> solutions) {
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(new File(path + fname), false));
			
			for (Solution solution : solutions) {
				writer.write(Double.toString(solution.getObjective(0)));
				
				for (int i = 1; i < solution.getNumberOfObjectives(); i++) {
					writer.write(SEP);
					writer.write(Double.toString(solution.getObjective(i)));
				}
				
				for (int i = 0; i < solution.getNumberOfConstraints(); i++) {
					writer.write(SEP);
					writer.write(Double.toString(solution.getConstraint(i)));
				}
				
				for (int i = 0; i < solution.getNumberOfVariables(); i++) {
					writer.write(SEP);
					writer.write(Double.toString(((RealVariable) solution.getVariable(i)).getValue()));
				}

				writer.newLine();
			}
			
			writer.close();
		} catch (IOException e) {
			Logger.getLogger(SimplePIO.class.getEnclosingClass().getName()).log(Level.SEVERE, null, e);
        }
	}
	
	public void writeAccumToImg(String fname, Accumulator accumulator) {
		try {
			new Plot()
			.add(accumulator)
			.save(new File(path + fname));
		} catch (IOException e) {
			Logger.getLogger(SimplePIO.class.getEnclosingClass().getName()).log(Level.SEVERE, null, e);
		}
	}
	
	public void writeAccumToCSV(String fname, Accumulator accumulator) {
		try {
			accumulator.saveCSV(new File(path + fname));
		} catch (IOException e) {
			Logger.getLogger(SimplePIO.class.getEnclosingClass().getName()).log(Level.SEVERE, null, e);
		}
	}
	
	private void writeHeader(int nobj, int ncon, int nvar, BufferedWriter bf) {
		try {
			bf.write("NFE");
			bf.write(SEP);				
			bf.write("ITE");
			
			for (int i = 0; i < nobj; i++) {
				bf.write(SEP);
				bf.write("f" + i);
			}
			
			for (int i = 0; i < ncon; i++) {
				bf.write(SEP);
				bf.write("g" + i);
			}
			
			for (int i = 0; i < nvar; i++) {
				bf.write(SEP);
				bf.write("v" + i);
			}
			
			bf.write(SEP);
			bf.write("mode");
	
			bf.newLine();
		} catch (IOException e) {
			Logger.getLogger(SimplePIO.class.getEnclosingClass().getName()).log(Level.SEVERE, null, e);
        }
	}
}
