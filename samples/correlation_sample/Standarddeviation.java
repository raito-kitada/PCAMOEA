package correlation_sample;

import java.util.ArrayList;
import java.util.List;

/**
 * 計算クラス
 */
public class Standarddeviation {

	/**
	 * 標準偏差を計算する
	 * @param items 項目リスト
	 * @return 結果
	 */
	public Double standardDeviation(final List<Double> items) {
		return Math.sqrt(variance(items));
	}

	/**
	 * 分散を計算する
	 * @param items 項目リスト
	 * @return 結果
	 */
	public Double variance(final List<Double> items) {
		int n = items.size();
		return sumOfSquares(items) / (n - 1);
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
	
	public static void main(String[] args) {
		List<Double> items = prepareTestData();
		Standarddeviation calc = new Standarddeviation();
		Double xbar = calc.average(items);
		Double sx = calc.standardDeviation(items);
		printResult(items, xbar, sx);
	}

	private static List<Double> prepareTestData() {
		List<Double> items = new ArrayList<>();
		items.add(10.0);
		items.add(12.1);
		items.add(11.3);
		items.add(13.4);
		items.add(9.0);
		return items;
	}

	private static void printResult(List<Double> items, Double xbar, Double sx) {
		System.out.print("サンプル\t：");
		for (int i = 0; i < items.size(); i++) {
			if (i == 0) System.out.print(items.get(i));
			else System.out.print("," + items.get(i).toString());
		}
		System.out.print("\n");
		System.out.println("平均\t\t：" + xbar);
		System.out.println("標準偏差\t：" + sx);
	}
}
