package junitTest;

import static org.junit.Assert.*;

import org.junit.Test;

public class Test09Test {

	// *****************************************************************************************************************
	// 第９章 より大きな設計上の課題 ***********************************************************************************
	// *****************************************************************************************************************

	// ユニットテストは設計の一部
	// システムの設計がテストに制約を与える、その逆の影響もある

	// SRP(Single Respoinsibility Principle: 単一責任の原則) に着目し小さなクラスを通じて
	// 柔軟性とテストの容易さを向上させる

	// コマンドとクエリの分離という原則について検討し、値を返す際に副作用を発生させて呼び出し元を惑わせるようなことを回避


	// 9.1 ProfileクラスとSRP ******************************************************************************************

	/** (In Profile)
	 *
	 * public class Profile {
	 *     private Map<String, Answer> answers = new HashMap<>();
	 *
	 *     private int score;
	 *     private String name;
	 *
	 *     public Profile(String name) {
	 *         this.name = name;
	 *     }
	 *
	 *     public String getName() {
	 *         return name;
	 *     }
	 *
	 *     poublic void add(Answer answer) {
	 *         answers.put(answer.getQuestionText(), answer);
	 *     }
	 *
	 *     public boolean matches(Criteria criteria) {
	 *         calculateScore(criteria);
	 *         if (doesNotMeetAnyMustMatchCriterion(criteria))
	 *             return false;
	 *         return anyMatches(criteria);
	 *     }
	 *
	 *     // 必須条件にマッチしないものがある
	 *     private boolean doesNotMeetAnyMustMatchCriterion(Criteria criteria) {
	 *         for (Criterion criterion: criteria) {
	 *             boolean match = criterion.matches(answerMatching(criterion));
	 *             if (!match && criterion.getWeight() == Weight.MustMatch)
	 *                 return ture;
	 *         }
	 *         return false;
	 *     }
	 *
	 *     private void caluculateScore(criteria) {
	 *         score = 0;
	 *         for (Criterion criterion: criteria)
	 *             if (criterion.matches(answerMatching(criterion)))
	 *                 score += criterion.getWeight().getValue();
	 *     }
	 *
	 *     private boolean anymatches(Criteria criteria) {
	 *         boolean anyMatches = fales;
	 *         for (Criterion criterion: criteria)
	 *             anyMatches |= criterion.matches(anyMatchint(criterion));
	 *         return anyMatches;
	 *     }
	 *
	 *     private Answer answerMatching(Criterion criterion) {
	 *         return answers.get(criterion.getAnswer().getQuestionText());
	 *     }
	 *
	 *     public int score() {
	 *        return score;
	 *     }
	 *
	 *     @Override
	 *     poublic String toString() {
	 *         return name;
	 *     }
	 *
	 *     public List<Answer> find(Predivate<Answer> pred) {
	 *         return answers.values().stream().filter(pred).collect(Collectors.toList());
	 *     }
	 * }
	 *
	 */

	// このクラスの設計が必ずしも理想的ではないことのサイン

	// Profileが保持している情報(名前や質問への回答のコレクションなど)は時とともに変化する
	// スコア算出の機能もあるが、マッチングのアルゴリズムが今後改善されていく

	// SRPに違反している
	// 「クラスにとって変更されるべき理由は１つだけでなければならない」

	// c.f. クラス設計での原則群SOLID

	/** SRP (Single Responsibility Principle: 単一責任の原則)
	 *
	 * クラスを変更するための理由は１つだけであるべき
	 * クラスは目的を１つだけ持つ小さな物にする
	 */

	/** OCP (Open-Close Principle: 開放と閉鎖の原則)
	 *
	 * 拡張に対しては開かれた形で、しかし変更に大しては閉ざされた形でクラスを設計する
	 * 既存のクラスに対する変更の必要性を最小限にする
	 */

	/** LSP (Liskov Substitution Principle: リスコフの置換原則)
	 *
	 * 派生型は基本型に置き換え可能であるべき
	 * クライアントの観点からは、オーバライドされたメソッドが従来の機能を壊してはならない
	 */

	/** ISP (Interface Segregation Principle: インタフェイス分離の原則)
	 *
	 * クライアントは自らが利用しないメソッドに対しては依存関係を持つべきではない
	 * 大きなインタフェイスは分割して、小さな複数のインタフェイスを用意する
	 */

	/** DIP (Dependency Inversion Principle: 依存関係逆転の原則)
	 *
	 * 上位のモジュールは下位のモジュールに依存するべきではない
	 * 全てのモジュールはアブストラクション(Abstraction: 抽象化されたもの) に対して依存しなければならない
	 * アブストラクションは実装の詳細に依存してはいけない
	 * 実装の詳細がアブストラクションに依存するようにする
	 */

	// 9.2 新しいクラスの抜き出し **************************************************************************************

	// 現在の Profile は以下の２つの責任を持っている

	/* ・プロフィールに関する情報の管理
	 * ・条件とプロフィールのマッチング
	 */

	// これらの責任を、個別のクラスへと分離する

	// マッチングの機能を MatchSet に移動する
	// まず calculateScore() のロジックを MatchSet に移動する

	/** (In Profile)
	 *
	 * public boolean matches(Criteria criteria) {
	 *     score = new MatchSet(answers, criteria).getScore(); // <--- セット時にスコア計算
	 *     if (doesNotMeetAnyMustMatchCriterion(criteria))
	 *         return false;
	 *     return anyMatches(criteria);
	 * }
	 */

	// answerMatching() も呼び出しているのでこれも MatchSet に移動

	/** (In MathSet)
	 *
	 * public class MatchSet {
	 *     private Map<String, Answer> answers;
	 *     private int score = 0;
	 *
	 *     pbulic MatchSet(Map<String, Answer> answer, Criteria criteria) {
	 *         this.answers = answers;
	 *         caluculateScore(criteria);
	 *     }
	 *
	 *     private void caluculateScore(Criteria criteria) {
	 *         for (Criterion criterion: criteria)
	 *             if (criterion.matches(answerMatching(criterion)))
	 *                 score += criterion.getWeight().getValue();
	 *     }
	 *
	 *     private Answer answerMatching(Criterion criterion) {
	 *         return answer.get(criterion.getAnswer().getQuestionText());
	 *     }
	 *
	 *     public int getScore() {
	 *         return score;
	 *     }
	 * }
	 *
	 */

	// answerMatching() メソッドは依然 Profile の中で使われているが、同じコードが重複することになる
	// コードを１箇所にまとめられないか検討する必要がある

	// スコア関連のコードを MatchSet に移動したので matches() メソッドは
	// 条件が回答の集合にマッチしているかどうかの判定だけに使われる
	// この判定に関する責任を MatchSet に委譲することにする

	/** (In Profile)
	 *
	 * public boolean matches(Criteria criteria) {
	 *     score = new MatchSet(answers, criteria).getScore();
	 *     if (doesNotMeetAnyMustMatchCriterion(criteria))     // <---
	 *         return false;                                   // <--- この３行をMatchSetに移動
	 *     return anyMatches(criteria);                        // <---
	 * }
	 */

	// 呼び出されている doesNotmetAnyMustMatchCriterion() と anyMatches() メソッドも移動する

	/** (In Profile)
	 *
	 * public boolean matches(Criteria criteria) {
	 *     MatchSet matchSet = new MatchSet(answers, criteria);
	 *     score = matchSet.getScore();
	 *     return machSet.matches();                           // <-- 集約された
	 * }
	 */

	/** (In MathSet)
	 *
	 * public class MatchSet {
	 *     private Map<String, Answer> answers;
	 *     private int score = 0;
	 *     private Criteria criteria;                         // <---
	 *
	 *     pbulic MatchSet(Map<String, Answer> answer, Criteria criteria) {
	 *         this.answers = answers;
	 *         this.criteria = criteria;                      // <---
	 *         caluculateScore(criteria);
	 *     }
	 *
	 *     private void caluculateScore(Criteria criteria) {
	 *         for (Criterion criterion: criteria)
	 *             if (criterion.matches(answerMatching(criterion)))
	 *                 score += criterion.getWeight().getValue();
	 *     }
	 *
	 *     private Answer answerMatching(Criterion criterion) {
	 *         return answer.get(criterion.getAnswer().getQuestionText());
	 *     }
	 *
	 *     public int getScore() {
	 *         return score;
	 *     }
	 *
	 *     public boolean matches() {                                              // <---
	 *         if (doesNotMeetAnyMustMatchCriterion(criteria))
	 *             return false;
	 *         return anyMatches(criteria);
	 *     }
	 *
	 *     public boolean doesNotMeetAnyMustMatchCriterion(Criteria criteria) {    // <---
	 *          // ...
	 *     }
	 *
	 *     private boolean anyMatchs(Criterion criteria) {                         // <---
	 *          // ...
	 *     }
	 * }
	 *
	 */

	// マッチングに関するコードはすべて、MatchSetクラスに記述されるようになった
	// criteria がフィールドとして保持されているのでメソッドに引数として渡す必要はない

	/** (In MathSet)
	 *
	 * public class MatchSet {
	 *     private Map<String, Answer> answers;
	 *     private int score = 0;
	 *     private Criteria criteria;
	 *
	 *     pbulic MatchSet(Map<String, Answer> answer, Criteria criteria) {
	 *         this.answers = answers;
	 *         this.criteria = criteria;
	 *         caluculateScore();                                                  // <---
	 *     }
	 *
	 *     private void caluculateScore() {                                        // <---
	 *         for (Criterion criterion: criteria)
	 *             if (criterion.matches(answerMatching(criterion)))
	 *                 score += criterion.getWeight().getValue();
	 *     }
	 *
	 *     private Answer answerMatching(Criterion criterion) {
	 *         return answer.get(criterion.getAnswer().getQuestionText());
	 *     }
	 *
	 *     public int getScore() {
	 *         return score;
	 *     }
	 *
	 *     public boolean matches() {
	 *         if (doesNotMeetAnyMustMatchCriterion())                             // <---
	 *             return false;
	 *         return anyMatches();                                                // <---
	 *     }
	 *
	 *     public boolean doesNotMeetAnyMustMatchCriterion() {                     // <---
	 *          // ...
	 *     }
	 *
	 *     private boolean anyMatchs() {                                           // <---
	 *          // ...
	 *     }
	 * }
	 *
	 */

	// オブジェクト指向の設計で、実世界に即したモデル化には限界がある
	// 実世界でのプロフィールにマッチしているからといって、Profileという１つのクラスで全てを処理しようとすると
	// 大きな代償を受けることになる

	// 具体的なモノではなく、概念と対応した形でクラスを定義する

	// matchSet と言う概念を使うと、マッチングに関連した概念を独立して記述でき、コードをシンプルに出来る

	// コードを変更する際には必ず、設計を意識する必要がある

	// クラス間でのインタラクションだけでなく、保守と言う側面についても十分な考慮が求められる


	// 9.3 コマンドとクエリの分離 **************************************************************************************

	/** (In Profile)
	 *
	 * public boolean matches(Criteria criteria) {
	 *     MatchSet matchSet = new MatchSet(answers, criteria);
	 *     score = matchSet.getScore();
	 *     return machSet.matches();
	 * }
	 */

	// このメソッドには、算出されたスコアをProfileオブジェクトに保持するという不自然な副作用がある
	// Profile のコンテクストにとって、この操作には意味がない
	// スコアは与えられた条件ごとに決まるものであり、プロフィール単体で決まるものではない

	// この副作用は、関心の分離ができないと言う別の問題も引き起こす
	// スコアを知りたいときに matches() を呼び出さないといけないというのは非合理的

	// スコア取得のみであればマッチングの処理は無駄になる
	// マッチングの結果だけ知りたい場合には意図せずスコアが書き換えられる

	// 値を返すのと同時に副作用を発生させる(クラスやシステム内の他のエンティティの状態を変更する)ようなメソッドは
	// コマンドとクエリの分離と言う原則に反している

	// コマンドとクエリの分離の原則
	// メソッドはコマンド(副作用を生むような何らかの処理)を実行してもよいし、
	// クエリ(戻り値の要求)に応答しても良いが、両方を同時に行ってはいけない

	// クエリのメソッドがオブジェクトの状態を変更する場合
	// これを複数回呼び出すと異なる値が返されるかもしれない

	// この原則に反している古典的な例
	// java.util.Iteratorインタフェイスの next() メソッドは参照されているオブジェクトを返すとともに参照先を次に進める

	// MatchSet の扱いにはクライアント側のコードが責任を持つとする
	// Profile を変更して、渡された Criteriaインスタンスを元に MatchSetオブジェクトを生成して返すだけのメソッドを追加
	// クライアント側ではこのオブジェクトに対してスコアを取得したり戻り値(条件にマッチするか否か)を受け取ったりできる

	/** (Profile)
	 *
	 * public class Profile {
	 *     private Map<String, Answer> answers = new hashMap<>();
	 *     private Stirng name;
	 *
	 *     public Profile(String name) {
	 *         this.name = name;
	 *     }
	 *
	 *     public String getName() {
	 *         return name;
	 *     }
	 *
	 *     public void add(Answer answer) {
	 *         answers.put(answer.getQuestionText(), answer);
	 *     }
	 *
	 *     public MatchSet getMatchSet(Criteria criteria) {
	 *         return new MatchSet(answers, criteria);
	 *     }
	 *
	 *     @Override
	 *     public String toString() {
	 *         return name;
	 *     }
	 *
	 *     public List<Answer> find(Predicate<Answer> pred) {
	 *         return answer.values().stream().filter(pred).collect(Collectors.toList());
	 *     }
	 * }
	 */

	// SRPに適合したシンプルなクラスが出来た
	// このクラスの責任はプロフィールや回答のコレクションを管理することだけ

	// スコア関連のテストが成功しなくなるので修正

	// 9.4 ユニットテストを保守するコスト ******************************************************************************

	// クラスがAPIを変えればふるまいを公開するやり方が変わり、その意味ではクラスのふるまいが変わる

	// テストの失敗は設計上の問題を示すと考えてみる
	// 動かなくなったテストが多いほど問題は深刻である

	// 9.4.1 失敗を防ぐ方法

	// コードの失敗はきわめて大きな設計上の問題

	// テストの観点から見た重複したコードの問題点

	// ・テストを理解するのが難しくなる
	// e.g. Arrangeに複数行必要な場合それぞれの行について意味や順序を理解しなければならない
	//      その複数行を１つのヘルパーメソッドにして提供する

	// ・同じ変更が複数個所に対して必要になる

	// ユニットテストのセットアップに数行、あるいは数十行必要なコードは設計が間違っている

	// privateメソッド（実装の詳細）をテストしなければならないという状況も、おそらくクラスが大きすぎることが原因

	// privateメソッドが過度に多い場合、別のクラスに移動してpublicな振る舞いへとかえる方がよい

	// ユニットテストの作成が難しいなら設計に問題があると思うべき

	// Summary
	// システムの設計やコードの質が低いと、ユニットテストの保守にかかるコストが増大する

	// 9.4.2 壊れたテストの修正 ****************************************************************************************

	/** (MatchSetTest)
	 *
	 * pubic class MatchSetTest {
	 *     private Criteria criteria;
	 *     private Questiopn questionReimbursesTuition;
	 *     // ...
	 *
	 *     private Map<String, Answer> answers;
	 *
	 *     @Before
	 *     public void createAnswers() {
	 *         answers = new HashMap<>();
	 *     }
	 *
	 *     @Before
	 *     public void createCriteria() {
	 *         criteria = new Criteria();
	 *     }
	 *
	 *     @Before
	 *     public void createQuestionAndAnswers() {
	 *         // ...
	 *     }
	 *
	 *     private void add(Answer answer) {                            // セットアップのヘルパメソッド
	 *         answers.put(answer.getQuestionText(), answer);           //
	 *     }                                                            //
	 *
	 *     private MatchSet createMatchSet() {                          // セットアップのヘルパメソッド
	 *         return new MatchSet(answers, criteria);                  //
	 *     }                                                            //
	 *
	 *     // 必須の条件にマッチしない場合、matchesはfalseを返す
	 *     @Test
	 *     public void matchAnswersFalseWhenMustMatchCriteriaNotMet() {
	 *         add(answerDoesNotReimburseTuition);
	 *         criteria.add(new Criterion(answerReimbursesTuition, Weight.MusutMatch));
	 *
	 *         assertFalse(createMatchSet().matches());
	 *     }
	 *
	 *     // 不問の条件があればmatchesはtrueを返す
	 *     @Test
	 *     public void matchAnswersTrueForAnyDontCareCriterioa() {
	 *         add(answerDoesNotReimbursesTuition);
	 *         criteria.add(new Criterion(answerReimbursesTuition, Weight.DontCare));
	 *
	 *         assertTrue(createMatchSet().matches());
	 *     }
	 *
	 *     // ...
	 *
	 * }
	 *
	 */

	// インスタンスのセットアップ部分をヘルパメソッドに移す

	// privateは振る舞いをテストするのは難しい
	// publicに変更できたら、公開されるふるまいのドキュメントとしてテストを作成する

	// 9.5 その他の設計上のポイント ************************************************************************************

	// コンストラクタMatchSet()はスコアの算出も受け持っているが、クライアント側で必要ない場合無駄になる
	// 要求された場合のみ計算する仕様に変更

	/** (MatchSet)
	 *
	 * public class MatchSet {
	 *     // ...
	 *
	 *     public MatchSet(Map<String, Answer> answers, Critera criteria) {
	 *         this.answers = answers;
	 *         this.criteria = criteria;
	 *     }
	 *
	 *     public int getScore() {
	 *         int score = 0;
	 *         for (Criterion criterion: criteria) {
	 *             if (criterion.matches(answerMatching(criterion))) {
	 *                 score += criterion.getWeight().getValue();
	 *             }
	 *         }
	 *         return score;
	 *     }
	 *
	 *     // ...
	 * }
	 */

	// もしもscoreの再計算がパフォーマンス上問題だと言う場合には、結果をキャッシュすることによって対策できる

	// コレクションanswersの扱われ方
	// Profileでは質問の文字列をキーとして回答を保持するMap<Strng, Answer>が生成されている
	// MatchSetオブジェクトにもマップanswersへの参照が渡されている

	// 両者のクラスがともに回答の保持や取得の方法を正しく知っていなければならない

	// このように実装の詳細が複数のクラスに広まっている状態には、Shotgun Surgery:散弾銃を使った手術 という
	// コードの臭いが現れている

	// たとえばanswersをデータベースのテーブルに置き換えなければならない場合に
	// 複数個所で変更が必要になる

	// Map<Sring, Answer>は回答の保持や取得の方法の実装

	// マップanswerを２箇所に置くことによって、データの状態について混乱も生じる

	// 回答の保管場所をAnswersCollectionという別のクラスへ隔離する

	/** (Profile)
	 *
	 * public class Profile {
	 *     private AnswerCollection answers = new AnswerCollection();    // <---
	 *     private String name;
	 *
	 *     public Profile(String name) {
	 *         this.name = name;
	 *     }
	 *
	 *     public String getName() {
	 *         return name;
	 *     }
	 *
	 *     public void add(Answer answer) {
	 *         answers.add(answer);                                      // <---
	 *     }
	 *
	 *     pbulic MatchSet getMatchSet(Criteria critera) {
	 *         return new MatchSet(answers, criteria);                   // <---
	 *     }
	 * }
	 */

	/** (AnswerCollection)
	 *
	 * public class AnswerCollection {
	 *     private Map<String, Answer> answers = new HashMap<>();
	 *
	 *     public void add(Answer answer) {
	 *         answers.pub(answer.getQuestionText(), answer);
	 *     }
	 *
	 *     public Answer answerMatching(Criterion criterion) {
	 *         return answers.get(criterion.getAnswer().getQuestionText());
	 *     }
	 *
	 *     pbulic List<Answer> find(Predicate<Answer> pred) {
	 *         return answer.values().stream().filter(pred).collect(Collection.toList());
	 *     }
	 * }
	 */

	/** (MatchSet)
	 *
	 * public class MatchSet {
	 *     private AnswerCollection answers;
	 *
	 *     public MatchSet(AnswerCollection answers, Critera criteria) {
	 *         this.answers = answers;
	 *         this.criteria = criteria;
	 *     }
	 *
	 *     public int getScore() {
	 *         int score = 0;
	 *         for (Criterion criterion: criteria) {
	 *             if (criterion.matches(answers.answerMatching(criterion))) {
	 *                 score += criterion.getWeight().getValue();
	 *             }
	 *         }
	 *         return score;
	 *     }
	 *
	 *     // ...
	 * }
	 */

	// リファクタリングにはもう一つ考えられる
	// MatchSetでcriteriaにい含まれるCriterionオブジェクトに対して複数個所でループの処理が行われている

	// 解決例 => Visitorパターンを導入して解決する


	// 9.6 まとめ ******************************************************************************************************

	// ユニットテストのカバー範囲を広げ、確信を持って設計を改善し続けられるようにする

	// Single Responsibility Principle
	// コマンドとクエリの分離

	// 小さなクラス、小さなメソッドを作る




	@Test
	public void test() {
		fail("まだ実装されていません");
	}

}
