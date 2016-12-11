package junitTest;

import static java.lang.Math.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.IsCloseTo.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class Test06Test {

	//******************************************************************************************************************
	// ６章 Right-BICEP(テストの対象) **********************************************************************************

	// どのような部分へのテストが必要かのガイドライン
	// Right-BICEP(右腕の力こぶ)はテスト対象を選ぶための問いかけを示している

	// Right: 正しい               => 結果は正しいですか？
	// Boundary: 境界              => 境界条件は適切ですか？
	// Inverse: 逆                 => 逆の関係はチェックできますか？
	// Cross-check: クロスチェック => 別の方法を使って結果をチェックできますか？
	// Error: エラー               => エラーの条件を強制的に発生させることはできますか？
	// Performance: パフォーマンス => パフォーマンスの特性は許容範囲内ですか？

	// 6.1 Right(結果の正しさ) *****************************************************************************************

	@Test
	public void answersArithmeticMeanOfTwoNumbers() {
		// Arrange
		int a = 5;
		int b = 7;

		// Act
		int actualResult = (a + b) / 2;

		// Assert
		assertThat(actualResult, equalTo(6));
	}

	// テストをより確実なものにするためには、より多くの、あるいはより大きな値を指定するといったことが考えられる
	// しかし、このようなテストは単なる (happy path: 明らかに成功する実行経路) のテストに過ぎない

	// HappyPath のテストも"どうすればコードが正しく実行されたことがわかるのか"と言う問いへの１つの回答になる

	// "小さなコードでのHappyPathさえテストできないとしたら、
	// 自分が何を作ろうとしているのか理解するのもおぼつかないでしょう"

	// 与えられたシナリオでどんな答えを返すべきか示したテストをまず作成し、その後でコードを記述する

	// 完全に網羅する前からコーディングするのも可
	// 条件が判明した時点でその都度改善していく

	// ユニットテストには、自身が行った選択を記録するという意味もある

	// 6.2 Boundary(境界条件) ******************************************************************************************

	// 境界条件(入力として許容される限界付近のシナリオ)

	// 境界条件として考えられるものの例

	// ・捏造あるいは矛盾の含まれる入力値 e.g. ファイル名に"!*W:X\&Gi/w$->>$g/h#WQ@と指定された場合
	// ・形式が正しくないデータ e.g. fred@foobar. のようにトップレベルのドメイン名がないメールアドレス
	// ・オーバーフローを引き起こすような計算
	// ・空あるいは省略された値 e.g. 0、0.0、""、null など
	// ・通常想定される範囲を大きく超える値 e.g. 150歳の人など
	// ・重複が許されないはずのリスト(学級名簿)などに、同じ値が含まれる場合
	// ・並べ替えられているはずのリストが並べ替えられていなかったり、その逆だったりする場合
	//   並べ替え済みのリスト(あるいは逆順に並べ替えられているリスト)をソートアルゴリズムに渡す場合など
	// ・期待されているのと子多なる順序で物事が発生する場合 e.g. HTTPサーバがPOSTリクエストよりも後でOPTIONSのレスポンス
	//   を返す場合など

	// e.g.

	private ScoreCollection collection;

	@Before
	public void init() {
		collection = new ScoreCollection();
	}

	// コレクションにnullを追加
	@Test(expected=IllegalArgumentException.class)
	public void throwsExceptionWhenAddingNull() {
		collection.add(null);
	}

	// 何も追加されていない場合はゼロを返す
	@Test
	public void answersZeroWhenNoElementsAdded() {
		assertThat(collection.arithmeticMean(), equalTo(0));
	}

	// Integerとしてのオーバーフローに対処する
	@Test
	public void dealsWithIntegerOverflow() {
		collection.add(() -> Integer.MAX_VALUE);
		collection.add(() -> 1);

		assertThat(collection.arithmeticMean(), equalTo(1073741824));
	}

	@Test
	public void dealsWithIntegerMaxValue() {
		collection.add(() -> Integer.MAX_VALUE);
		collection.add(() -> Integer.MAX_VALUE);

		// 入力値はint値に制限されているので結果は必ずint値に収まる
		assertThat(collection.arithmeticMean(), equalTo(Integer.MAX_VALUE));
	}

	// ほとんどの場合はオーバーフローを見逃すべきではない

	// 6.2.1 CORRECTに従った境界条件の設定

	// Conformance: 適合
	//     値は期待される形式に適合しているか？

	// Ordering: 順序
	//     値の集合は適切な順序に並べ替えられているか？いないか？

	// Range: 範囲
	//     値は最小値と最大値の範囲内にありますか？

	// Reference: 参照
	//     自身が直接コントロールできない外部のコードを参照していないか？

	// Existence: 存在
	//     値は存在するか？具体的には、値は非nullや非ゼロか？あるいは、φ集合か？

	// Cardinality: 要素数
	//     十分な個数の値が用意されているか？

	// Time: 時間
	//     全ての出来事は一定の順序で発生するか？また、それぞれは適切なタイミングで発生し、想定される時間内に完了するか？

	// 6.3 Inverse(逆の関係をチェックする) *****************************************************************************

	// 逆のロジックの適用によって振る舞いをチェックできることがある
	// e.g. 掛け算を使って割り算を検証したり、引き算を使って足し算を検証したりできる

	// ニュートン法を使って平方根の値を算出する

	static class Newton {
		private static final double TOLERANCE = 1E-16; // 許容誤差 1/10^16

		public static double squareRoot(double n) {
			double approx = n; // 概算
			while (abs(approx - n / approx) > TOLERANCE * approx) {
				approx = (n / approx + approx) / 2.0;
			}
			return approx;
		}
	}

	@Test
	public void squareRoot() {
		double result = Newton.squareRoot(250.0);
		assertThat(result * result, closeTo(250.0, Newton.TOLERANCE));
	}

	// 掛け算 (result * result) を使って平方根のロジックを検証している

	// 注意: 逆のロジックを適用する際に、元の処理と共通のコードを使うとそのコードのバグに気づけない可能性がある

	// データベースにデータを追加するようなコードのテストでは、JDBCを使ってデータを直接検証する

	// リストから該当する要素を抽出する処理の場合
	// 該当する要素と該当しない要素を合わせる全体の要素と等しくなる

	// このようなクロスチェックはすべてのデータが集計されつじまが合っているか確認するために利用できる

	// 6.4 Cross-check(別の方法でチェックする) *************************************************************************

	// 平方根の例ではJavaライブラリの代替の実装を使ってクロスチェックが行える
	@Test
	public void squareRootAltercheck() {
		double result = Newton.squareRoot(1969.0);
		assertThat(result, closeTo(Math.sqrt(1969.0), Newton.TOLERANCE));
	}

	// e.g. 蔵書管理システムでは全ての本について貸し出し中の部数と在庫数の和が所蔵数と一致する必要がある

	// クラス自体から得た異なるデータについて、これらの間に矛盾がないことを確認するなど

	// 6.5 Error(エラーを強制的に発生させる) ***************************************************************************

	// ディスクがフルになったり、ネットワークが不通になったり、メールが届かなかったり
	// プログラムがクラッシュするような状況でもコードが適切に振舞うことを確認しなければならない

	// こういった時に必要となるのがエラーを強制的に発生させるようなテスト

	// テスト対象のコードに対してどのようなエラーや外的要件をもたらすべきか
	// e.g.
	// ・メモリがいっぱいになる
	// ・ディスクがいっぱいになる
	// ・時計がずれている
	// ・ネットワークに障害が発生する
	// ・システムの負荷が上昇する
	// ・描画可能な色が限られている
	// ・画像の解像度が高すぎる(または低すぎる)

	// 明白ではないパスもカバーする
	// 想像力が求められる

	// 6.6 Performance(パフォーマンスの特性) ***************************************************************************

	// "ボトルネックは予想もしていなかった箇所に発生するもの。
	//           ボトルネックの所在を確認せずに、当て推量でいい加減な修正をしてはいけない"
	//                                                 Rob Pike - Google

	// パフォーマンス関連の問題に対しては、盲目的な対策を行うべきでない
	// ユニットテストを通して、本当の問題がどこにあるか理解し、推測に基づく解決が効果を発揮するかどうか判断する

	// e.g. 処理が一定の時間内に完了するかどうかを検証
	private long run(int times, Runnable func) {
		long start = System.nanoTime();
		for (int i = 0; i < times; i++) {
			func.run();
		}
		long stop = System.nanoTime();
		return (stop - start) / 1000000;
	}

	@Test
	public void squareRootProccessEndUpInTime() {
		int numberOfTime = 100000;

		long elapsedMs = run(numberOfTime, () -> Newton.squareRoot(1969.0));

		assertTrue(elapsedMs < 40);
	}

	// 注意点
	// ・タイミングやクロックサイクルの影響を最小限にするために、コードの実行回数はかなり多くする必要がある
	// ・繰り返されるコードに対して、Javaが自動的に最適化を行ってしまわないようにする
	// ・パフォーマンスに関する低速なテストは他のユニットテストとは別に実行する
	// ・同じマシン上でも、システムの負荷やその他の要因によって実行時間は大きく変わる

	// 不確定要素が多すぎる
	// 実運用の環境と同等の条件化でのみテストを行うことが唯一の解決策

	// コードの変更の前後でパフォーマンスが悪化していないことを確認できる

	// ユニットテストよりも上位の階層での問題解決に主眼が置かれることが多い
	// JMeter, JUnitPerfなどのツールを検討

	// 6.7 まとめ ******************************************************************************************************

	// どのようなテストを作成するべきか
	// Right-BICEP
	// Right
	// Boundary
	// Inverse
	// Cross-check
	// Error
	// Performance
}