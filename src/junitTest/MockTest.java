package junitTest;

/**
 * Plusクラスを外部リソースアクセスが必要なクラスと仮定する
 * Plusの実装についてはテスト不要とする
 * @author ict816
 *
 */
public class MockTest {
	private Plus plus = new Plus();

	public int getPlusAndSquare(int n1, int n2) {
		return plus.plus(n1, n2) * plus.plus(n1, n2);
	}


}
