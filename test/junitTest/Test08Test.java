package junitTest;


public class Test08Test {

	// *****************************************************************************************************************
	// 第３部 より大きな設計の全体像 ***********************************************************************************
	// *****************************************************************************************************************

	// 優れたユニットテストを行えばリファクタリングか可能になる
	// リファクタリングの効果を上げるためには、優れた設計とは何かと言う点について理解する必要がある

	// テストが困難なものがあれば、モックのオブジェクトを作成してテストを依存先から切り離す必要がある

	// 複雑なテストを低コストで保守できるものへ作り替える必要がある


	// *****************************************************************************************************************
	// 第８章 クリーンなコードをめざすリファクタリング *****************************************************************

	// コードの重複は大きなコストを招く
	// 保守の手間の増大、変更の際のリスク
	// コードの重複は最小限に抑える

	// コードを理解するためのコスト
	// コードは最大限に明瞭なものであるべき

	// 最小限の重複と最大限の明瞭さ
	// この２つの目標を念頭においてコードをリファクタリングする


	// 8.1 リファクタリングとは ****************************************************************************************

	// ケンタウルス座プロキシマ星(プロキシマ・ケンタウリ): 太陽に最も近い恒星(4.37光年)

	// 「リファクタリングとは、機能面での振る舞いを買えずにコードの構造を変えること」
	// 「リファクタリングとは、コードの一部を別の箇所に動かし、かつシステム全体としては正しく機能させつづけること」

	// システムの破壊を防ぐためにユニットテストを活用する

	// 8.1.1 リファクタリングを行うべき時

	// 8.1.2 メソッドを抜き出す(次善のリファクタリング)

	// 名前の変更: 最もよく使われているリファクタリング
	// コードの明瞭さ -> 意図の宣言の明瞭さ

	// メソッドをよりシンプルなものにし、どんなポリシーの下に、何に対して責任を持っているのか理解しやすくする

	/** (In Profile)
	 *
	 * for (Criterion criterion: criteria) {
	 *     Answer answer = answers.get(criterion.getAnswer().getQuestionText());
	 *     boolean match = criterion.getWeight() == Weight.DontCare || answer.match(criterion.getAnswer()); // <=
	 *     // ....
	 * }
	 *
	 */

	// match への代入の複雑さを別のメソッドに隔離する

	/** (In Profile)
	 *
	 * for (Criterion criterion: criteria) {
	 *     Answer answer = answers.get(criterion.getAnswer().getQuestionText());
	 *     boolean match = matches(criterion, answer); // <=
	 *     // ...
	 * }
	 *
	 */

	// 小さな変更の度にテストを行うようにする


	// 8.2 メソッドの置き場を決める ************************************************************************************

	// matches() メソッドはProfileオブジェクトとは全く関係がない
	// AnswerオブジェクトとCriterionオブジェクトの間でマッチングが行われているので、
	// 両者のうちいずれかが処理に責任を持つべき

	// ここでは matches() メソッドをCriterionクラスへと移動させる
	// CriterionオブジェクトはAnswerオブジェクトについて既に知っているが逆は成り立たないため
	// Answer は Criterion に依存していないため

	/** (In Criterion)
	 *
	 * public boolean matches(Answer answer) {
	 *     return getWeight() == Weight.DontCare || answer.match(getAnswer());
	 * }
	 *
	 */

	/** (In Profile)
	 *
	 * for (Criterion criterion: criteria) {
	 *     Answer answer = answer.get(criterion.getAnswer().getQuestionText());
	 *     boolean match = criterion.matches(answer); // <---
	 *     // ...
	 * }
	 *
	 */

	// 次にローカル変数 answer に代入を行っている部分に注目する

	/**
	 * Answer answer = answer.get(criterion.getAnswer().getQuestionText());
	 */

	// デメテルの法則(Law of Demeter: 別のオブジェクトへと連鎖するようなメソッド呼び出しを避けるべき)
	// 最小知識の原則 直接の友達とだけ話すこと

	// 右辺を別メソッドに抜き出す

	/** (In Profile)
	 *
	 * Answer answer = answerMatching(criterion);
	 *
	 * //...
	 *
	 * private Answer answerMatching(Criterion criterion) {
	 *     return answer.get(criterion.getAnswer().getQuestionText());
	 * }
	 *
	 */

	// 一時変数にはコードの意図を明確にするという効果もある

	// 他にも高コストな処理の結果をキャッシュしたり
	// メソッドの中で値が変更させるオブジェクトを保持したり

	// 8.3 リファクタリングの自動実行と手動実行 ************************************************************************

	// ローカル変数 answer はコードの明確化に貢献していない
	// answer を answerMatch(criterion) で置き換えインライン化(変数削除)する

	/** (In Profile)
	 *
	 * for (Criterion criterion: criteria) {
	 *     boolean match = criterion.matches(answerMatch(criterion)); // <=
	 *     // ...
	 * }
	 *
	 */

	// Eclipse では[リファクタリング]->[インライン化] で行える
	// TODO IDEの自動リファクタリング機能の確認

	/** (In Profile)
	 *
	 * public boolean matches(Criteria criteria) {
	 *     score = 0;
	 *
	 *     boolean kill = false;
	 *     boolean anyMatches = false;
	 *     for (Criterion criterion: criteria) {
	 *         boolean match = criterion.matches(answerMatching(criterion));
	 *
	 *         if (!match && criterion.getWeight() == Weight.MustMatch) {
	 *             kill = true;
	 *         }
	 *         if (match) {
	 *             score += criterion.getWeight().getValue();
	 *         }
	 *         anymatches |= match;
	 *     }
	 *     if (kill)
	 *         return false;
	 *     return anyMatches;
	 * }
	 *
	 * private Answer answerMatching(criterion criterion) {
	 *     return answer.get(criterion.getAnswer().getQuestionText());
	 * }
	 *
	 */

	// matches() メソッドでの詳細を外部に出し概略を理解しやすくなった

	/*  (このメソッドでのコアとなるゴール)
	 * ・該当する条件について、重みを合計する
	 * ・必須の条件について回答がマッチしない場合 false を返す
	 * ・これ以外の場合、マッチする回答があれば true を返し、なければ false
	 */

	// これらの意図をより明確に示すために更にリファクタリングを行う
	// ３番目のゴールについて
	// anyMatches の値を返すのではなく anyMatches() メソッドの実行結果を返すようにする

	/** (In Profile)
	 *
	 * public boolean matches(Criteria criteria) {
	 *     score = 0;
	 *
	 *     boolean kill = false;
	 *     boolean anyMatches = false;
	 *     for (Criterion criterion: criteria) {
	 *         boolean match = criterion.matches(answerMatching(criterion));
	 *
	 *         if (!match && criterion.getWeight() == Weight.MustMatch) {
	 *             kill = true;
	 *         }
	 *         if (match) {
	 *             score += criterion.getWeight().getValue();
	 *         }
	 *     }
	 *     if (kill)
	 *         return false;
	 *     return anyMatches(criteria); // <---
	 * }
	 *
	 * private boolean anymatches(Criteria criteria) {
	 *     boolean anyMatches = fales;
	 *     for (Criterion criterion: criteria)
	 *         anyMatches |= criterion.matches(anyMatchint(criterion));
	 *     return anyMatches;
	 * }
	 *
	 */

	// 連続していないコードを新しいメソッドに移動するには自動化が使えない

	// 自動化が使えるならそれに頼り、テストを重視しリファクタリングの度に実行する

	// パフォーマンスの低下について


	// 8.4 過剰なリファクタリングの是非 ********************************************************************************

	// 全てのマッチの重みを合計する部分についても別のメソッドに抜き出す

	/** (In Profile)
	 *
	 * public boolean matches(Criteria criteria) {
	 *     caluculateScore(criteria); // <---
	 *
	 *     boolean kill = false;
	 *     boolean anyMatches = false;
	 *     for (Criterion criterion: criteria) {
	 *         boolean match = criterion.matches(answerMatching(criterion));
	 *
	 *         if (!match && criterion.getWeight() == Weight.MustMatch) {
	 *             kill = true;
	 *         }
	 *     }
	 *     if (kill)
	 *         return false;
	 *     return anyMatches(criteria);
	 * }
	 *
	 * private void caluculateScore(criteria) {
	 *     score = 0;
	 *     for (Criterion criterion: criteria)
	 *         if (criterion.matches(answerMatching(criterion)))
	 *             score += criterion.getWeight().getValue();
	 * }
	 *
	 */

	// 必須の条件の中にマッチしないものがあるかどうか判定するロジックも抜き出してみる

	/** (In Profile)
	 *
	 * public boolean matches(Criteria criteria) {
	 *     caluculateScore(criteria);
	 *     if (doesNotMeetAnyMustMatchCriterion(criteria))  // <---
	 *         return false;                                // <---
	 *     return anyMatches(criteria);
	 * }
	 *
	 * private boolean doesNotMeetAnyMustMatchCriterion(Criteria criteria) {
	 *     for (Criterion criterion: criteria) {
	 *         boolean match = criterion.matches(answerMatching(criterion));
	 *         if (!match && criterion.getWeight() == Weight.MustMatch)
	 *             return ture;
	 *     }
	 *     return false;
	 * }
	 *
	 */

	// ３つのメソッドが作られそれぞれでループが実行されている

	// 8.4.1 メリット(明確で個々のテストが可能)

	// リファクタリング後の matches() メソッドは全体のアルゴリズムを明確に示していて瞬時に判断できる
	// 実装の詳細はヘルパーメソッドの中へと隠蔽されている

	// 8.4.2 パフォーマンス面の不安

	// リファクタリングの結果がパフォーマンスの要件にを満たせないという根拠がない

	// パフォーマンスが緊急の問題でないなら、早まった最適化に時間を浪費せず
	// コードをクリーンな状態に保つように努力する

	// クリーンなコードは最適化に対応する柔軟性をもつ

	// 最適化を求められる可能性があるなら、クリーンな設計を行うのが一番良い

	// パフォーマンスがすでに問題となってる場合にはまずパフォーマンステストを行う
	// リファクタリングの前後のコードを比較するような小さなテストを作成し、何％の悪化が生じたのかを測定する

	// 8.5 まとめ ******************************************************************************************************

	// コードをクリーンに保つための手法

}
