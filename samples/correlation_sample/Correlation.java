package correlation_sample;
import java.util.ArrayList;
import java.util.List;

public class Correlation {

	/**
	 * 相関係数を計算する
	 * @param itemXi 項目リスト（Xi）
	 * @param itemYi 項目リスト（Yi）
	 * @return 結果
	 */
	public Double correlationCoefficient(final List<Double> itemsXi, final List<Double> itemsYi) {
		Double sxy = deviationSumOfProduct(itemsXi, itemsYi);
		Double sxx = sumOfSquares(itemsXi);
		Double syy = sumOfSquares(itemsYi);
		return  sxy / Math.sqrt(sxx * syy);
	}

	/**
	 * 偏差積和を計算する
	 * @param itemXi 項目リスト（Xi）
	 * @param itemYi 項目リスト（Yi）
	 * @return 結果
	 */
	public Double deviationSumOfProduct(final List<Double> itemsXi, final List<Double> itemsYi) {
		List<Double> itemsXiYi = new ArrayList<>();
		int n = itemsXi.size();

		for (int i = 0; i < n; i++) {
			itemsXiYi.add(itemsXi.get(i) * itemsYi.get(i));
		}
		Double xiyiSum = sum(itemsXiYi);
		Double xiSum = sum(itemsXi);
		Double yiSum = sum(itemsYi);
		return xiyiSum - ((xiSum * yiSum) / n);
	}

	/**
	 * 平方和を計算する
	 * @param items 項目リスト
	 * @return 結果
	 */
	public Double sumOfSquares(final List<Double> items) {
		Double xbar = average(items);
		List<Double> squares = new ArrayList<>();

		for (Double item : items) {
			Double sqare = (item - xbar) * (item - xbar);
			squares.add(sqare);
		}
		return sum(squares);
	}

	/**
	 * 平均値を計算する
	 * @param items 項目リスト
	 * @return 結果
	 */
	public Double average(final List<Double> items) {
		return sum(items) / items.size();
	}

	/**
	 * 総和を計算する
	 * @param items 項目リスト
	 * @return 結果
	 */
	public Double sum(final List<Double> items) {
		Double result = 0.0;

		for (Double item : items) {
			result += item;
		}
		return result;
	}
	/**
	 * サンプルのテスト用クラス
	 */

		private static int[] xi = {
			28, 30, 25, 27, 32, 36, 31, 29, 30, 35, 33, 37, 36, 33, 28,
			34, 27, 35, 33, 31, 35, 28, 31, 39, 34, 31, 38, 37, 32, 31};

		private static int[] yi = {
			73, 67, 62, 71, 70, 73, 72, 71, 73, 74, 76, 78, 77, 71, 66,
			70, 64, 75, 68, 66, 69, 65, 66, 71, 77, 70, 81, 73, 75, 78};

		public static void main(String[] args) {
			List<Double> itemsXi = prepareTestData(xi);
			List<Double> itemsYi = prepareTestData(yi);
			Correlation calc = new Correlation();
			Double sxy = calc.deviationSumOfProduct(itemsXi, itemsYi);
			Double sxx = calc.sumOfSquares(itemsXi);
			Double syy = calc.sumOfSquares(itemsYi);
			Double r = calc.correlationCoefficient(itemsXi, itemsYi);
			System.out.println("偏差積和（Sxy）\t\t：" + sxy);
			System.out.println("偏差平方和（Sxx）\t：" + sxx);
			System.out.println("偏差平方和（Syy）\t：" + syy);
			System.out.println("相関係数（r）\t\t：" + r);
		}

		private static List<Double> prepareTestData(int[] sample) {
			List<Double> items = new ArrayList<>();

			for (int data : sample) {
				items.add((double) data);
			}
			return items;
		}
	
}
