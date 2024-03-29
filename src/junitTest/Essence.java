package junitTest;

public class Essence {

	// よいテストはFIRSTである

	// Fast: 迅速 --- 遅いコードへの依存を減らす
	// Isolated: 隔離 --- データや他のテストや同一メッソド内のシナリオにも依存しない
	// Repeatable: 繰り返し可能 --- 自分が管理できない外部の環境から切り離す
	// Self-validating: 自律的検証 --- セットアップ処理等、検証に関わる作業はテスト自身が行う
	// Timely: タイムリー --- バグ可能性があり、将来的な変更の可能性のある対象コードから

	// FIRST: 最初に -> テスト駆動開発


	// テストの対象Right-BICEP

	// Right: 正しい               => 結果は正しいですか？
	// Boundary: 境界              => 境界条件は適切ですか？
	// Inverse: 逆                 => 逆の関係はチェックできますか？
	// Cross-check: クロスチェック => 別の方法を使って結果をチェックできますか？
	// Error: エラー               => エラーの条件を強制的に発生させることはできますか？
	// Performance: パフォーマンス => パフォーマンスの特性は許容範囲内ですか？


	// 境界条件の設定CORRECT

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


	// コードの重複はきわめて大きな設計上の問題

	// ユニットテストのセットアップに数行あるいは複数行が必要なコードは設計が間違っている

	// privateメソッド(実装の詳細)をテストしなければならない状況はおそらく、クラスが大きすぎる
	// => privateメソッドが多すぎる場合、別のクラスに移動しpublicな振る舞いに変える

	// ユニットテストの作成が難しいなら設計に問題があると思うべき

	// privateは振る舞いをテストするのは難しい
	// publicに変更できたら、公開されるふるまいのドキュメントとしてテストを作成する

	// http://misko.hevery.com/code-reviewers-guide/fl\aw-constructor-does-real-work/

	// Shotgun Sergery: 散弾銃を使った手術 code smell
	// 実装の詳細が複数のクラスに広まっている状態
	// 解決例 => Facade(インタフェイスを提供するクラス)の中に実装を隠蔽

	// 複数個所で同じループを実行している
	// 同一アルゴリズムをまとめる
	// 解決例 => Visitorパターンの導入

	// 第１０章 モックオブジェクト

	// ・外部への実際の呼び出しは低速である
	// ・外部APIが常に利用可能であり、常に正しい値を返すとは保証できない

	// テスト用のハードコードされた値を返す実装をスタブと言う

	// 依存性の注入(dependency injection / DI)と言う手法を利用する
	// スタブをテスト対象インスタンスに注入する

	// コンストラクタ以外での注入方法
	// setter() の利用
	// Factoryのメソッドをオーバライド
	// AbstractFactoryの導入
	// Google GuiceやSpringなどのツールの使用

	// ハードコードのスタブでは誤ったパラメータの検証ができない

	// モック(mock)とはテストの構成要素の１つで、模擬的なふるまいを提供するとともに受け取ったパラメータの検証も行う

	// Mockito 汎用のモック作成ツール
	// https:/code.google.com/p/mochito/

	// コンストラクタではなくDIツールを使った注入

	// 使い方
	// 1. アノテーション @Mock を使い、モックのインスタンスを生成
	// 2. アノテーション @InjectMocks を使い、注入対象のインスタンスを指定
	// 3. 対象のインスタンスを生成した後で、MockitoAnnotations.initMockes(this) を呼び出す
	// 4. Arrange部分でwhen(mock.targetMethod(conditions(targetParam))).thenReturn(return);
	//    targetMethodに渡される引数targetParamがconditionsを満たすときreturnを返すことを指定する








}
