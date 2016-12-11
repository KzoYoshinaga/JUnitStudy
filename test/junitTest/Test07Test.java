package junitTest;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Objects;

import org.junit.Test;

public class Test07Test {

	@Test
	public void test() {
		//fail("まだ実装されていません");
	}

	//******************************************************************************************************************
	// ７章 CORRECT(境界条件の扱い) ************************************************************************************

	// CORRECT
	// Conformance
	// Ordering
	// Range
	// Reference
	// Existence
	// Cardinality
	// Time

	// 考えられるすべての出所からのデータについて、それぞれの境界条件に該当する場合の影響を検討する必要がある

	// 受け取った引数、フィールド、ローカルに管理される変数などについて次のように自問する

	//         他にどのような点が問題を起こすのか？

	// 7.1 Conformance(適合) *******************************************************************************************

	// 多くのデータは、特定の形式に従っていなければならない

	// e.g. メールアドレスの形式
	// RFC822の仕様で定義されているようなチェックを全て自前で行う必要はない
	// 既存のライブラリに頼るべき

	// より複雑な構造を持ったデータでは、テストしなければならないケースの数が爆発的に増加する

	// e.g. 何らかのレポートを表すデータを読み込むというシナリオ
	//      ヘッダーが１つ記述されている
	//      任意の個数のデータ本体、そして１つのフッターがつく
	// このようなデータの境界条件としての例
	// ・ヘッダーがなく、データ本体とフッターだけがある
	// ・データ本体がなく、ヘッダーとフッターだけがある
	// ・フッターがなく、ヘッダーとデータ本体だけがある
	// ・ヘッダーだけがある
	// ・データ本体だけがある
	// ・フッターだけがある

	// バグはシステムの境界部分に潜んでいることが多い

	// 7.2 Ordering(順序) **********************************************************************************************

	// データの順序(あるいは、大きなコレクションの中であるデータが置かれている位置)が異なると、
	// コードが誤った振る舞いを示すことがよくある

	// 7.3 Range(範囲) *************************************************************************************************

	// Javaに組み込みの型を使った変数では、必要以上に幅広い範囲の値をセットできてしまう
	// e.g. int値で年齢を表す => 2000万世紀以上も長生きした人を表現できる

	// プリミティブ型を過剰に用いるのは「プリミティブ型中毒」というコードの臭い(code smell 深刻な問題の兆候)です

	// e.g. 角度は360度までしかない => プリミティブ型を使わずにBearing(方位)と言う型で表してみる
	public class Bearing {
		public static final int MAX = 359;
		private int value;

		public Bearing(int value) {
			if(value < 0 || MAX < value) throw new BearingOutOfRangeException();
			this.value = value;
		}

		public int value() { return value; }

		public int angleBetween(Bearing bearing) { return value - bearing.value; }
	}

	public class BearingOutOfRangeException extends RuntimeException {
		 private static final long serialVersionUID = 1L;
	}

	// 負の値を指定すると例外が発生する
	@Test(expected=BearingOutOfRangeException.class)
	public void throwsOnNegativeNumber() {
		new Bearing(-1);
	}

	// 大きすぎる値を指定すると例外が発生する
	@Test(expected=BearingOutOfRangeException.class)
	public void throwWhenBearingTooLarge() {
		new Bearing(Bearing.MAX +1);
	}

	// 正統なBearingを返す
	@Test
	public void answersValidBearing() {
		assertThat(new Bearing(Bearing.MAX).value, equalTo(Bearing.MAX));
	}

	// 別の方位との間の角度を返す
	@Test
	public void answersAngleBetweenItAndAnottherBearing() {
		assertThat(new Bearing(15).angleBetween(new Bearing(12)), equalTo(3));
	}

	// 自身よりも大きい値の方位に対しては負の角度を返す
	@Test
	public void angleBetweenIsNegativeWhenThisBearingSmaller() {
		assertThat(new Bearing(12).angleBetween(new Bearing(15)), equalTo(-3));
	}

	// このような抽象化が行われていれば、呼び出し側のコードは範囲外の方角を指定できない

	// より複雑な制約
	// e.g. ２次元平面上での座標値(xとyの組)を２つ保持するクラス
	//      この２点を結ぶ線が対角線になり、各辺が座標軸と平行になるような長方形を考えた場合に、どの辺も長さが100以下で
	//      なければならないという制約を設ける
	// このような場合、それぞれの座標値の範囲は相互に依存している

	// 制約(invariant)とはあるコードの実行の前後を通じて当てはまる条件

	// 以下長方形の例
	public class Rectangle {
	   private Point origin;
	   private Point opposite;

	   public Rectangle(Point origin, Point oppositeCorner) {
	      this.origin = origin;
	      this.opposite = oppositeCorner;
	   }

	   public Rectangle(Point origin) {
	      this.opposite = this.origin = origin;
	   }

	   public int area() {
	      return (int)(Math.abs(origin.x - opposite.x) *
	            Math.abs(origin.y - opposite.y));
	   }

	   public void setOppositeCorner(Point opposite) {
	      this.opposite = opposite;
	   }

	   public Point origin() {
	      return origin;
	   }

	   public Point opposite() {
	      return opposite;
	   }

	   @Override
	   public String toString() {
	      return "Rectangle(origin " + origin + " opposite " + opposite + ")";
	   }
	}
	public class Point {
	   public final double x;
	   public final double y;

	   public Point(double x, double y) {
	      this.x = x;
	      this.y = y;
	   }

	   @Override
	   public String toString() {
	      return String.format("(%s, %s)", x, y);
	   }
	}

	private Rectangle rectangle;

	// 制約を満たしている ここでは各テストにべた書き
	//@After
	//public void ensureInvariant() {
	//	assertThat(rectangle, ConstrainsSidesTo.constrainsSidesTo(100));
	//}

	// 面積を返す
	@Test
	public void answersArea() {
		rectangle = new Rectangle(new Point(5,5), new Point (15, 10));
		assertThat(rectangle.area(), equalTo(50));
		assertThat(rectangle, ConstrainsSidesTo.constrainsSidesTo(100));
	}

	// 動的に座標を変更できる
	@Test
	public void allowsDynamicallyChangingSize() {
		rectangle = new Rectangle(new Point(5,5));
		rectangle.setOppositeCorner(new Point(125, 105));
		assertThat(rectangle.area(), equalTo(12000));
		// 両辺は100以下の制約に反する
		assertThat(rectangle, ConstrainsSidesTo.constrainsSidesTo(100));
	}

	// 長方形のインスタンスを操作するどのようなテストを行っても常に制約を満たすかチェックしてくれる

	// 7.3.1 制約をチェックするマッチャー

	// 上述の@AfterメソッドではconstrainsSidesToというHacrestスタイルのカスタムマッチャーが使われている
	// 「assert that (the) rectangle constrains (its) sides to 100」
	// 「長方形の辺には100と言う制約がある」

	// org.hamcrest.TypeSafeMatcherクラスを継承しマッチング対象のRectangleにバインドする
	// マッチャーのメソッド名にあわせてConstrainsSidesToとするのが一般的

	// 7.3.2 制約をチェックする埋め込みのメソッド

	// 最も頻繁にチェック対象になるのは、アプリケーションドメインでの制約ではなくデータ構造に関する制約

	// ここでは、疎な配列(sparse array)の不適切な実装について考察

	public class SparseArray<T> {
	   public static final int INITIAL_SIZE = 1000;
	   private int[] keys = new int[INITIAL_SIZE];
	   private Object[] values = new Object[INITIAL_SIZE];
	   private int size = 0;

	   public void put(int key, T value) {
	      if (value == null) return;

	      int index = binarySearch(key, keys, size);
	      if (index != -1 && keys[index] == key)
	         values[index] = value;
	      else
	         insertAfter(key, value, index);
	   }

	   public int size() {
	      return size;
	   }

	   private void insertAfter(int key, T value, int index) {
	      int[] newKeys = new int[INITIAL_SIZE];
	      Object[] newValues = new Object[INITIAL_SIZE];

	      copyFromBefore(index, newKeys, newValues);

	      int newIndex = index + 1;
	      newKeys[newIndex] = key;
	      newValues[newIndex] = value;

	      if (size - newIndex != 0)
	         copyFromAfter(index, newKeys, newValues);

	      keys = newKeys;
	      values = newValues;
	   }

	   // 監視対象の配列内の非null値の数は配列のサイズと一致する、と言う制約
	   public void checkInvariants() throws InvariantException {
	      long nonNullValues = Arrays.stream(values).filter(Objects::nonNull).count();
	      if (nonNullValues != size)
	         throw new InvariantException("size " + size +
	               " does not match value count of " + nonNullValues);
	   }

	   private void copyFromAfter(int index, int[] newKeys, Object[] newValues) {
	      int start = index + 1;
	      System.arraycopy(keys, start, newKeys, start + 1, size - start);
	      System.arraycopy(values, start, newValues, start + 1, size - start);
	   }

	   private void copyFromBefore(int index, int[] newKeys, Object[] newValues) {
	      System.arraycopy(keys, 0, newKeys, 0, index + 1);
	      System.arraycopy(values, 0, newValues, 0, index + 1);
	   }

	   @SuppressWarnings("unchecked")
	   public T get(int key) {
	      int index = binarySearch(key, keys, size);
	      if (index != -1 && keys[index] == key)
	         return (T)values[index];
	      return null;
	   }

	   int binarySearch(int n, int[] nums, int size) {
	      // ...
	      int low = 0;
	      int high = size - 1;

	      while (low <= high) {
	         int midIndex = (low + high) / 2;
	         if (n > nums[midIndex])
	            low = midIndex + 1;
	         else if (n < nums[midIndex])
	            high = midIndex - 1;
	         else
	            return midIndex;
	      }
	      return low - 1;
	   }
	}

	public class InvariantException extends RuntimeException {
		public InvariantException(String message) {
			super(message);
		}
		private static final long serialVersionUID = 1L;
	}

	// 様々な値を入力してみる
	@Test
	public void handlesInsertionInDescendingOrder() {
		SparseArray<String> array = new SparseArray<>();
		array.put(7, "七");
		array.checkInvariants(); // 非null値の数と配列サイズとの比較
		array.put(6, "六");
		array.checkInvariants();
		assertThat(array.get(6), equalTo("六"));
		assertThat(array.get(7), equalTo("六"));
	}

	// 配列のインデックス操作にはさまざまなエラーの可能性が潜んでいる

	// Rangeのチェックとして以下のようなシナリオについてテストを行う

	// ・開始と終了のインデックスが同じ値
	// ・先頭のインデックスが末尾より大きい
	// ・インデックスの値が負
	// ・インデックスの値が許容範囲を超えて大きい
	// ・個数を管理する変数の値が実際の個数と一致していない

	// 7.4 Reference(参照) *********************************************************************************************

	// メソッドへのテストの際には以下の点について検証する

	// ・参照先が有効範囲の外にある場合はどうなるのか
	// ・外部への依存はあるか
	// ・特定の状態のオブジェクトに依存しているか
	// ・その他に何か必須の条件はあるか

	// e.g.
	// 顧客の入出金を行うWEBアプリケーションでは顧客はログインしていなければならない
	// スタックのpop()では空でないスタックが必要

	// 何らかの状態について仮定を行って処理を行う場合、その仮定が満たされない場合にも適切に振舞うようにする

	// 変速機を表すTransmissionのコードへのテスト
	public interface Moveable {
		int currentSpeedInMph();
	}

	public enum Gear {
		DRIVE, PARK
	}

	public class Car implements Moveable {
		private int mph;

		@Override
		public int currentSpeedInMph() {
			return mph;
		}

		public void accelerateTo(int mph) {
			this.mph = mph;
		}

		public void breakToStop() {
			mph = 0;
		}
	}

	public class Transmission {
		private Gear gear;
		private Moveable moveable;

		public Transmission(Moveable moveable) {
			this.moveable = moveable;
		}

		public void shift(Gear gear) {
			// bags for state-machine implementation
			if (moveable.currentSpeedInMph() > 0 && gear == Gear.PARK) return;
			this.gear = gear;
		}

		public Gear getGear() {
			return gear;
		}
	}

	// 車が走行中の場合とそうでない場合の変速機の振る舞いをテストする

	// 以下の３つの重要なシナリオに対応したテスト
	// ・加速を始めたらDの状態を保つこと
	// ・走行中にはPのシフトチェンジが試みられても無視すること
	// ・停止中にはPにシフトチェンジが出来ること

	// 加速を始めたらDの状態を保つ
	@Test
	public void remaintsInDriveAfterAccelaration() {
		Car car = new Car();
		Transmission transmission = new Transmission(car);
		transmission.shift(Gear.DRIVE);
		car.accelerateTo(34);

		assertThat(transmission.getGear(), equalTo(Gear.DRIVE));
	}

	// 走行中はPへのシフトチェンジを無視する
	@Test
	public void ignoresShiftToParkWhileInDrive() {
		Car car = new Car();
		Transmission transmission = new Transmission(car);
		transmission.shift(Gear.DRIVE);
		car.accelerateTo(30);

		transmission.shift(Gear.PARK);

		assertThat(transmission.getGear(), equalTo(Gear.DRIVE));
	}

	// 停止中はPにシフトチェンジする
	@Test
	public void allowsShiftToParkWhenNotMoving() {
		Car car = new Car();
		Transmission transmission = new Transmission(car);
		transmission.shift(Gear.DRIVE);
		car.accelerateTo(30);
		car.breakToStop();

		transmission.shift(Gear.PARK);

		assertThat(transmission.getGear(), equalTo(Gear.PARK));
		assertThat(car.currentSpeedInMph(), equalTo(0));
	}

	// 前提条件
	// そのメソッドの実行に必要な条件
	// シフトレバーをPに移動する際の前提は車が停止していること
	// この前提が満たされない場合にもコードは穏やかに振舞う必要がある
	// shiff() メッソド先頭の if トラップ

	// 事後条件
	// コードの実行によって成立が期待される条件
	// アサーションを通じて表現される
	// 場合によっては呼び出したメソッドからの戻り値がそのまま事後条件を表すこともある
	// この場合は副作用(呼び出した振る舞いの結果として発生する状態変化)についても検証が必要
	// allawsShiftToParkWhenNotMovingの場合はスピードが０になること

	// 7.5 Existence(存在) *********************************************************************************************

	// 「これは本当に存在するのか」と自問することで、かなりの量のバグを発見できる

	// null, 0, 空

	// 探索のためのメソッドがnullを返した場合
	// 存在するはずのファイルがなかった場合
	// ネットワークがダウンしていた場合

	// これらについてテストを作成する

	// データが存在しなくてもメソッドが適切にふるまうかどうか確認する

	// 7.6 Cardinality(要素数) *****************************************************************************************

	// fencepost error: 課題について十分に考えないことに起因する誤り

	// 個数を正しくカウントしていることを確認するための方法を検討する

	// 0-1-n rule
	// ・ゼロ個
	// ・１個
	// ・複数個(２個以上)
	// 値の種類によってはこれがわかりさえすればよい場合もある

	// e.g.
	// あるパンケーキショップでの人気商品のトップ１０を管理しているとする
	// このリストは注文が発生するたびに更新され、リアルタイムにアクセスできる

	// 個数と言う考え方を念頭においたテストケース

	// ・商品がない場合にリストを出力する
	// ・商品が１つだけある場合にリストを出力する
	// ・商品が１０個未満の場合にリストを出力する
	// ・商品がない場合に商品を追加する
	// ・商品が１つだけある場合に商品を追加する
	// ・商品が１０個未満の場合に商品を追加する
	// ・商品が１０個以上ある場合に商品を追加する

	// public static final int MAX_ENTRIES = 10;]
	// 定数にして置くと仕様変更に柔軟

	// 7.7 Time(時間) **************************************************************************************************

	// 時間については複数の側面がありそれぞれについて考慮する
	// ・相対的な時間(物事の発生の順序)
	// ・絶対的な時間(経過時間、現在の時刻)
	// ・並列処理でのタイミング

	// APIの中には、内部状態を保持しており特定の順序での呼び出しを要求するものがある
	// e.g. logout() の前に login() を呼び出す必要があること
	//      open() -> read() -> close() の順に呼び出すといった制約

	// これらの順序に反して呼び出しを行った場合のテスト
	// あるいは順序の一部をスキップしてみる

	// 相対的な時間にはタイムアウト(時間切れ)に関する問題も含まれる
	// ・常に利用できるわけではないリソースについて、利用可能になるまでどの程度待機するか検討
	// ・タイムアウトが指定されていない処理がないかチェックする

	// 実際の時刻について考慮する点
	// e.g. US夏時間

	// システム時刻に依存したテストも誤りにつながる
	// テストの中からコントロールできるような、別の情報源から時刻を取得するべき
	// 5.4 Repeatable で紹介している

	// 並列処理や排他処理の際の時間に関する問題
	// "Java Concurrency in Practice" Brien Goetz 邦題「Java並列処理プログラミング」
	// ・複数のスレッドが１つのオブジェクトに対して同時にアクセスしたらどうなるか
	// ・グローバルあるいはインスタンスレベルのデータやメソッドへのアクセスを排他制御する必要はあるか
	// ・ファイルやハードウェアへの同時アクセスについてはどうか

	// 7.8 まとめ ******************************************************************************************************

	// ユニットテストの際に考慮すべき境界条件
	// CORRECT
	// Conformance
	// Order
	// Range
	// Reference
	// Existence
	// Cardinally
	// Time

}
