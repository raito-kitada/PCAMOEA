package correlation_sample;
import java.util.ArrayList;
import java.util.List;

public class Test1 {
	public static void main(String[] args) {
		List<Double> items = prepareTestData();
		Double xbar = doTest(items);
		printResult(items, xbar);
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

	private static Double doTest(List<Double> items) {
		Average calc = new Average();
		return calc.average(items);
	}

	private static void printResult(List<Double> items, Double xbar) {
		System.out.print("xi = {");
		for (int i = 0; i < items.size(); i++) {
			if (i == 0) System.out.print(items.get(i));
			else System.out.print("," + items.get(i).toString());
		}
		System.out.println("}の平均値は「" + xbar + "」です。");
	}
}
