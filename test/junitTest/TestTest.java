package junitTest;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.IsCloseTo.*;
import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.naming.InsufficientResourcesException;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestTest {

	//*******************************************************************************
	// 浮動小数点のテスト ***********************************************************
	@Test
	public void floatEqualToTest() {
		// Expected: <6.96> but: was <6.959999999999999> 失敗
		assertThat(2.32 * 3, equalTo(6.96));
	}

	@Test
	public void floatCloseToTest() {
		// 誤差0.0005以内なので成功
		assertThat(2.32 * 3 , closeTo(6.96, 0.0005));
	}

	//*******************************************************************************
	// 例外のテスト *****************************************************************

	// アノテーション
	@Test(expected=InsufficientResourcesException.class)
	public void throwsSomeException() throws InsufficientResourcesException {
		// 期待された例外が投げられたら成功
		throw new InsufficientResourcesException("");
	}

	@Test(expected=InsufficientResourcesException.class)
	public void throwsNoException() {
		// 期待された例外が投げられないので失敗
	}

	// 古い方法
	@Test
	public void regacyAssertionForExpectedExceptionThrow() {
		try {
			// 期待された例外が投げられるので成功
			throw new Exception("Expected exception");
			// fail();
		}
		catch(Exception expected) {
			// 例外に含まれるメッセージの検証
			assertThat(expected.getMessage(), equalTo("Expected exception"));
		}
	}

	@Test
	public void regacyAssertionForUnexpectedExceptionThrow() {
		try {
			// 期待された例外が投げられるので成功
			throw new Exception("Unexpected exception");
			// fail();
		}
		catch(Exception expected) {
			// 例外に含まれるメッセージの検証 失敗
			assertThat(expected.getMessage(), equalTo("Expected exception"));
		}
	}

	@Test
	public void regacyAssertionForExceptionFail() {
		try {
			// Act
			// 期待された例外が投げられないので失敗
			fail();
		}
		catch (Exception expected) {
			assertThat(expected.getMessage(), equalTo("Expected exception"));
		}
	}

	// 新しい方法
	// p41 テストを実行するフローの中での出来事を細かくコントロールするために、カスタムのルールという仕組みがある
	// cross-cutting concern(複数のクラスにまたがって必要な機能)を、一連のテストに対して自動的に付加できる
	// ExpectedExceptionルール
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void exceptionRuleSuccess() throws Exception {
		thrown.expect(Exception.class);
		thrown.expectMessage("Expected exception");

		// Act
		throw new Exception("Expected exception");
	}

	@Test
	public void exceptionRuleFail() throws Exception {
		// テスト中に発生しなければならない例外の型を指定
		// Rule
		thrown.expect(Exception.class);
		thrown.expectMessage("Expected exception");

		// Action
		throw new Exception("Unexpected exception");
	}

	// FishBowl (http://github.com/stefanbirkner/fichbowl)
	// 例外を発生させるRambda式の実行結果を例外オブジェクトに設定できる
	// TODO

	// テスト自身から発生する例外はすべてthrowしてもかまわない
	@Test
	public void readFromTestFile() throws IOException {
		// Arrange
		String filename = "test.txt";
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		writer.write("Test data");
		writer.close();

		// Act
	}

	//*******************************************************************************
	// p45 4章 テストの構成 *********************************************************

	// ・AAAの構造に沿った記述を通して、テストに視覚的な一貫性を与える
	// ・メソッドではなくふるまいをテストすることによって、テストの保守を容易にする
	// ・テストに適切な名前をつけることの重要性
	// ・@Beforeや@Afterのアノテーションを使い、テスト間で共通に行われる初期化や後処理をまとめる
	// ・不要なテストを一時的に無視する

	// 2つの数値の算術平均を返す
	@Test
	public void answersArithmeticMeanOfTwoNumbers() {
		// Arrange
		int a = 100;
		int b = 200;

		// Act
		int actualResult = (a + b) / 2;

		// Assert
		assertThat(actualResult, equalTo(150));
	}

	// Arrange(セットアップ)
	// テストが実行される際に、システムが適切な状態にあることを保証する

	// Acrt(操作)
	// テストのコードを実行

	// Assert(アサーション)
	// テスト退場のコードが正しくふるまったかどうかを確認

	// After(事後処理)
	// 何らかのリソースを割り当ててテストを実行した場合、その終了後にリソースを開放する

	// p47 4.2 ふるまいのテストとメソッドのテスト
	// e.g. ATMクラスの出金メソッドのテストを行うためには、入金処理が必要

	// p47 4.3 テストと対象のコードの関係
	// テストコードは対象コードは同じプロジェクトの別の位置に分ける
	// テスト対象のコードはテストコードに依存しない
	// しかしテストしやすい設計に変更することは良い

	// 4.3.1 テストと対象のコードの分離

	// ソフトウェアにテストコードを含めることも可能だが
	// 低速化やコードへの攻撃対象領域(Attack_surface)の増大を招く

	// プロジェクト内のどこにテストコードを置くべきか
	// ・テスト対象のコードとパッケージ名を一致させ同じディレクトリに配置
	// 実装は簡単だがスクリプトによる仕分け等が必要なため実運用には向かない

	// ・テスト対象のコードとパッケージ名を一致させ別のディレクトリに配置
	// e.g.
	// |
	// +-----src
	// |      +------somePackage
	// |                +----------someSrc1.java
	// |                +----------someSrc2.java
	// +-----test
	//        +------somePackage
	//                  +----------someSrc1Test.java
	//
	// testディレクトリの構造はsrcディレクトリと一致しているため、それぞれのテストは対象のクラスと同じパッケージになる
	// そのためテストクラスは対象のクラス内のアクセス修飾なしのメンバーにアクセスできる

	// ・実運用向けのコードと似ているが異なるパッケージ構造を定義し、別のディレクトリに配置
	// e.g.
	// |
	// +-----src
	// |      +------somePackage
	// |                +----------someSrc1.java
	// |                +----------someSrc2.java
	// +-----test
	//        +-----test
	//                +------somePackage
	//                +----------someSrc1Test.java
	//
	// テストはpublicなAPIを介してのみアクセスできる。

	// 4.,3.2 privateなデータの公開、privateなふるまいの公開
	// テストが実装の詳細に踏み込みすぎると、コードの小さな変更が多数のテストの失敗を招くことになる

	// privateな振る舞いをテストしたくなるような設計は、何らかの問題があると言える
	// 興味深いふるまいが多数埋もれているという状態は、間違いなくSRP(Single Responsibility Principle: 単一責任の原則)に反している
	// クラスは目的を１つだけ持った小さなものであるべき
	// privateなふるまいの中に興味深いものがあったら、別のクラス(または新しいクラス)に移動させる
	// そうすればpublicなメソッドとして便利に利用できる

	// 4.4 1つの目的に特化したテストの価値 ****************************************************************************

	// テストケースごとに１つずつテストのメソッドを用意し、検証対象のふるまいを的確に表す名前をつける

	// 4.5 ドキュメントとしてのテスト *********************************************************************************

	// ソースに記述しているコメントの多くは、テストによって代替できる

	// 4.5.1 一貫性のある名前

	// 個々の振る舞いに着目した詳細なテストを作成するようになると、テスト名にもきちんとした名前を与えられる

	// e.g.
	// 悪い名前  makeSingleWithdrawa1: 1回出金する
	// 良い名前  withdrawalReducesBalanceByWithdrawnAmount: 出金を行うとその分だけ残高が減る

	// 悪い名前  attemptToWithdrawTooMuch: 多額の出金を試みる
	// 良い名前  withdrawalOfMoreThanAvailableFundsGeneratesErro: 残高以上の出金を行うとエラーが発生する

	// 悪い名前  multipleDeposits: 複数回の入金
	// 良い名前  multipleDepositesIncreaseBalanceBySumOfDeposits: 複数回入金を行うとその合計額の分だけ残高が増加する

	// 長くても7語程度にする

	// わかりやすい名前はすべて下記の形式になっている

	// doingSomeOperationGeneratesSomeResult: 何らかの処理を行うと何らかの結果が発生する
	// someResultOccursUnderSomeCondition: 何らかの条件化では何らかの結果が発生する

	// ビヘイビア駆動開発と呼ばれる開発プロセスから派生したgiven-when-thenという名前づけのパターンに基づいて下記のように
	// することも可能

	// givenSomeContextWhenDoingSomeBehaviorThenSomeResultOccurs: 何らかの条件化で、何らかの振る舞いを行うと何らかの結果が得られる
	// whenDoingSomeBehaviorThenSomeResultOccurs: 何らかのふるまいを行うと何らかの結果が生じる

	// どの形式を選んでもそれを一貫することが大事

	// 4.5.2 意味のあるテスト

	// テストが分かりにくいと感じたとき
	// ・ローカル変数の名前を改善する
	// ・意味のある定数を導入する
	// ・Hamcrestアサーションを利用する
	// ・長いテストは、より短く焦点を絞った複数のテストに分割する
	// ・些末なコードはヘルパーメソッドや@Beforeメソッドに移動する

	// 4.6 @Beforeと@Afterに関する補足(初期化と後処理)

	// @Before before1
	// @Before before2
	// @Test   test1
	// @After  after
	// @Before before2
	// @Before before1
	// @Test   test1
	// @After  after

	// テストの記述順番と実際に呼び出される順番は一致しない

	// 4.6.1 @BeforeClassと@AfterClas

	// テストクラス全体のセットアップ
	// すべてのテストの実行よりも前に１回だけ実行される

	// @BeforeClass
	// public static void initializeSomethingReallyExpensive() {...}

	// @AfterClass
	// public static void initializeSomethingReallyExpensive() {...}

	// @BeforeClass
	// @Before
	// @Test
	// @After
	//  ...
	// @AfterClass

	// 4.7 テストと緑のバーの重要性 ***********************************************************************************

	// すべてのテストが常に成功しているべき

	// 4.7.1 テストの所要時間を縮める

	// 可能な限り多くのテストを実行する

	// 外部リソースアクセスなど低速なテストがある場合
	// ・モックオブジェクトを使う
	// ・レベル１段階下げていずれかのパッケージに含まれる全てのテストを実行
	// ・Infinitest(https://infinitest.github.io/)などのツールを使ってバックグラウンドで常にテストが行われるようにする
	// ・JUnitのカテゴリーを使い特定アノテーションが付いているテストだけを実行する

	// 4.7.2 テストを無視する

	// 失敗したテストは一つずつ処理していく
	// 取り組んでいる失敗以外のテストはコメントアウトしておく
	// @Ignoreアノテーションを付与することで無視される
	@Ignore("Don't forget!")
	public void ignoredTest() {
		fail();
	}


}

