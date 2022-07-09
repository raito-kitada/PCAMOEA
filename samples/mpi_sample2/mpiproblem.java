package mpi_sample2;

import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

public class mpiproblem extends AbstractProblem {
		private static int nobj = 2;
		private static int nvar = 1214;
		private static int ncon = 0;
		
		public mpiproblem() {
			super(nvar, nobj, ncon);
			// enable evaluateAll in parallel
			Settings.PROPERTIES.setBoolean("org.moeaframework.problem.evaluateall_in_parallel", true);
		}
		
		public String getName() {
			// ここにMPIコマンドを記載してください
			return "./mpirun.sh";
		}
		
		@Override
		public void evaluate(Solution solution) {
			throw new UnsupportedOperationException("evaluate関数で外部mpiプログラムの実行は未サポートです");
		}
		
		@Override
		public Solution newSolution() {
			Solution solution = new Solution(nvar, nobj, ncon);

			// ここで設計変数のレンジで初期化します
			int index = 0;

			for (int i=0; i<607; i++) {
				solution.setVariable(index,  EncodingUtils.newReal(-300.0, 300.0));
				index++;
			}
			
			for (int i=0; i<121; i++) {
				solution.setVariable(index,  EncodingUtils.newReal(60.0, 100.0));
				index++;
			}

			for (int i=0; i<378; i++) {
				solution.setVariable(index,  EncodingUtils.newReal(80.0, 190.0));
				index++;
			}

			for (int i=0; i<4; i++) {
				solution.setVariable(index,  EncodingUtils.newReal(60.0, 100.0));
				index++;
			}

			for (int i=0; i<81; i++) {
				solution.setVariable(index,  EncodingUtils.newReal(80.0, 190.0));
				index++;
			}

			for (int i=0; i<10; i++) {
				solution.setVariable(index,  EncodingUtils.newReal(60.0, 100.0));
				index++;
			}
			
			for (int i=0; i<13; i++) {
				solution.setVariable(index,  EncodingUtils.newReal(80.0, 190.0));
				index++;
			}

			return solution;
		}
}
