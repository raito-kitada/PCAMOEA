package correlation_sample;
import java.util.List;
import java.util.ArrayList;


public class Average {
	public static void main(String[] args) {
		   System.out.println("Hello java!!");
	}
	/**
	 * 計算クラス
	 */
	
		/**
		 * 平均値を計算する
		 * @param items 項目リスト
		 * @return 結果
		 */
		public Double average(final List<Double> items) {
			return divide(sum(items), (double) items.size());
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
		 * 項目Aを項目Bで割る
		 * @param itemA 項目A
		 * @param itemB 項目B
		 * @return 結果
		 */
		public Double divide(final Double itemA, final Double itemB) {
			return itemA / itemB;
		}	

}
