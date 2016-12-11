package junitTest;


public class Test10Test {

	// *****************************************************************************************************************
	// 第１０章 モックオブジェクト *************************************************************************************
	// *****************************************************************************************************************

	// テストへの取り込みが困難な相手への依存性を解消して、テストの障壁を取り除く

	// 10.1 テストでの課題 *********************************************************************************************

	// public Address AddressRetriever.retrieve(double latitude, doublel longitude);

	// @return Address: 住所情報
	// @param latitude: 緯度
	// @param longitude: 経度

	/** (AddressRetriever)
	 *
	 * public class AddressRetriever {
	 *     public Address retrieve(double latitude, double longitude) throws IOException, ParseException {
	 *         String param = String.format("lat=%.6flon=%6.f", latitude, longitude);
	 *         String response = new HttpImpl()
	 *             .get("http://open.mapquestapi.com/nominatim/v1/reverse?format=json&" + param);  // <--- HTTP request
	 *
	 *         JSONObject obj = (JSONObject)new JSONParser().parse(response);
	 *
	 *         JSONObject address = (JSONObject)obj.get("address");
	 *         String country = (String)address.get("country_code");
	 *         if (!country.equal("us")) {
	 *             throw new UnsuportedOperationException("Cant handle whitout US address");
	 *         }
	 *         String houseNumber = (String)address.get("house_number");
	 *         String road = (String)address.get("road");
	 *         String city = (String)address.get("city");
	 *         String state = (String)address.get("state");
	 *         String zip = (String)address.get("postcode");
	 *
	 *         return new Address(houseNumber, road, city, state, zip);
	 *     }
	 * }
	 */

	// HTTP リクエストを行っている箇所のテストはどうするか？

	/** (HttpImpl)
	 * import java.io.*;
	 * import org.apache.http.*;
	 * import org.apache.http.client.methods.*;
	 * import org.apache.http.impl.client.*;
	 * import org.apache.http.util.*;
	 *
	 * public class HttpImpl implements Http {
	 *     public String get(String url) throws IOException {
	 *         CloseableHttpClient client = HttpClients.createDefault();
	 *         HttpGet request = new HttpGet(url);
	 *         CloseableHttpResponse response = client.execute(request);
	 *         try {
	 *             HttpEntity entity = response.getEntity();
	 *             return EntityUtils.toString(entity);
	 *         } finally {
	 *             response.close();
	 *         }
	 *     }
	 * }
	 */

	/** (Http)
	 *
	 * public interface Http {
	 *     String get(String url) throws IOException;
	 * }
	 */

	// HttpImplクラスはHTTPを使って外部のサービスと通信しており、このことがユニットテストを難しくしている

	// AddressRetriever.retrieve() メソッドに対してテストを行おうとするとHTTP呼び出しが発生する
	// このことの問題点は
	// ・外部への実際の呼び出しは低速である
	// ・外部APIが常に利用可能であり、常に正しい値を返すとは保証できない

	// 他のコードや依存関係を排除してretriece()メソッドのロジックに集中してテストしたい
	// HttpImplクラスが信頼できるとするとテストしたロジックは
	// ・HTTP呼び出しのセットアップ
	// ・HTTPレスポンスからAdressオブジェクトへの値のセット

	// 10.2 厄介なふるまいをスタブで置き換える *************************************************************************

	// HTTPレスポンスからAddressオブジェクトを生成する部分のテスト

	// テスト用のハードコードされた値を返す実装をスタブと言う

	/** (In AddressRetrieverTest)
	 *
	 * Http http = (String url) ->
	 *     "\"address\":{"
	 *     + "\"house_Number\":\"324\","
	 *     + "\"road\":\"ノーステジョンストリート\","
	 *     + "\"city\":\"コロラドスプリングス\","
	 *     + "\"state\":\"コロラド\","
	 *     + "\"postcode\":\"80903\","
	 *     + "\"country_code\":\"us\"}"
	 *     + "]";
	 */

	// 匿名内部クラスを使うなら
	/** (In AddressRetrieverTest)
	 *
	 * Http http = new Http() {
	 *     @Override
	 *     plublic String get(String url) throws IOException {
	 *         return "..."
	 *     }
	 * }
	 */

	// 依存性の注入(dependency injection / DI)と言う手法を利用する
	// スタブをテスト対象インスタンスに注入する

	// ここではAddressRetrieverのコンストラクタで行う

	/** (AddressRetriecver)
	 *
	 * poublic class AddressRetriever {
	 *     private Http http;
	 *
	 *     public AddressRetriever(Http http) {
	 *         this.http = http;
	 *     }
	 *
	 *     public Address retrieve(double latitude, double longitude) throws IOException, ParseException {
	 *         String parms = String.format("lat=%.6lon=%.6f", latitude, longitude);
	 *         String response = http.get(
	 *             "http://open.mapquestapi.com/nominatim/v1/reverse?format=json&"
	 *             + param);
	 *
	 *         JSONObject obj = (JSONObject)new JSONParser().parse(resonse);
	 *         // ...
	 *     }
	 * }
	 *
	 */

	// テストコードは以下のようになる

	/** (In AddressRetrieverTest)
	 *
	 * // 正当な座標に対して適切な住所を返す
	 * @Test
	 * public void answersAppropriateAddressForValidCoordinates() throws IOException, ParseException {
	 *     // Arrange
	 *     // スタブの作成
	 *     Http http = (String url) ->
	 *         "\"address\":{"
	 *         + "\"house_Number\":\"324\","
	 *         + "\"road\":\"ノーステジョンストリート\","
	 *         + "\"city\":\"コロラドスプリングス\","
	 *         + "\"state\":\"コロラド\","
	 *         + "\"postcode\":\"80903\","
	 *         + "\"country_code\":\"us\"}"
	 *         + "}";
	 *     AddressRetriever retriever = new AddressRetriever(http);
	 *
	 *     // Act
	 *     Address address = retriever.retrieve(38.0, -104.0);
	 *
	 *     // Assert
	 *     assertThat(adress.houseNumber, equalTo("324"));
	 *     assertThat(adress.road, equalTo("ノーステジョンストリート"));
	 *     assertThat(adress.city, equalTo("コロラドスプリングス"));
	 *     assertThat(adress.state, equalTo("コロラド"));
	 *     assertThat(adress.zip, equalTo("80903"));
	 * }
	 */

	// 10.3 テスト向けの設計変更 ***************************************************************************************

	// テストのためにシステムの設計を変更すると
	// システムが期待通りにふるまっていることをシンプルな方法で示せる

	// コンストラクタ以外での注入方法
	// setter() の利用
	// Factoryのメソッドをオーバライド
	// AbstractFactoryの導入
	// Google GuiceやSpringなどのツールの使用

	// 10.4 スタブを賢くする(パラメータの検証)

	// ここまで作成したスタブは常に同じJSON文字列を返す
	// AddressRetrieverがパラメータを正しく渡していなくてもテストの中で検証できない

	// スタブにガードのためのクラスを追加
	/** (In AddressRetrieverTest)
	 *
	 * // 正当な座標に対して適切な住所を返す
	 * @Test
	 * public void answersAppropriateAddressForValidCoordinates() throws IOException, ParseException {
	 *     // Arrange
	 *     // スタブの作成
	 *     Http http = (String url) ->{
	 *         if (!url.contains("lat=38.000000&lon=-104.000000"))                      // <--- パラメータガード
	 *             fail("URL " + url + " に正しいパラメータが含まれていません");        // <---
	 *         return "\"address\":{"
	 *                + "\"house_Number\":\"324\","
	 *                + "\"road\":\"ノーステジョンストリート\","
	 *                + "\"city\":\"コロラドスプリングス\","
	 *                + "\"state\":\"コロラド\","
	 *                + "\"postcode\":\"80903\","
	 *                + "\"country_code\":\"us\"}"
	 *                + "}";
	 *         }
	 *     AddressRetriever retriever = new AddressRetriever(http);
	 *
	 *     // Act
	 *     Address address = retriever.retrieve(38.0, -104.0);
	 *
	 *     // Assert
	 *     assertThat(adress.houseNumber, equalTo("324"));
	 *     assertThat(adress.road, equalTo("ノーステジョンストリート"));
	 *     assertThat(adress.city, equalTo("コロラドスプリングス"));
	 *     assertThat(adress.state, equalTo("コロラド"));
	 *     assertThat(adress.zip, equalTo("80903"));
	 * }
	 */

	// AddresRetriever.retrieve() メソッドのURLパラメータに & が含まれていないためテストは失敗する

	// 10.5 モックツールを使ったテストの簡素化 *************************************************************************

	// スタブをモックへと作り上げる
	// ・(スタブではなく)テストの中で、どのようなパラメータが期待されているか指定する
	// ・get()メソッドに渡されるパラメータを監視して記録する
	// ・テストの中で、記録されているget()へのパラメータが期待通りのものか検証する

	// Mockito 汎用のモック作成ツール
	// https:/code.google.com/p/mochito/

	// 使用例
	/** (AddressReetrieverTest)
	 *
	 * import static org.mockito.Mockito.*;
	 *
	 * pbulic class AddressRetrieveTest {
	 *     // 正等な座標に対して適切な住所を返す
	 *     @Test
	 *     public void answersAppropriateAdressForValidCoordinates() throws IOException, ParseException {
	 *         // Arrange
	 *         Http http = mock(Http.class);
	 *         when(http.get(contains("lat=38.000000&lon=-104.000000"))).thenReturn(
	 *             "\"address\":{"
	 *             + "\"house_Number\":\"324\","
	 *             + "\"road\":\"ノーステジョンストリート\","
	 *             + "\"city\":\"コロラドスプリングス\","
	 *             + "\"state\":\"コロラド\","
	 *             + "\"postcode\":\"80903\","
	 *             + "\"country_code\":\"us\"}"
	 *             + "}");
	 *         // モックの注入
	 *         AddressRetriever retriever = new AddressRetriever(http);
	 *
	 *         // ...
	 *     }
	 * }
	 */

	// 10.6 注入ツールを使った簡素化 ***********************************************************************************

	// コンストラクタではなくDIツールを使った注入
	// Mochitoにも組み込みで用意されている

	// 使い方
	// 1. アノテーション @Mock を使い、モックのインスタンスを生成
	// 2. アノテーション @InjectMocks を使い、注入対象のインスタンスを指定
	// 3. 対象のインスタンスを生成した後で、MockitoAnnotations.initMockes(this) を呼び出す

	/** (AddressRetrieverTest)
	 *
	 * public class AddressRetrieverTest {
	 *     @Mock private Http http;                              // モックの生成指示
	 *     @InjectMocks private AddressRetriever retriever;      // モックの注入指示
	 *
	 *     @Before
	 *     public void createRetriever() {
	 *         retriever = new AddressRetriever();
	 *         MockitoAnnotations.initMockes(this);   // thisが指すこのクラス自身の中で @Mock が指定されているフィールド
	 *                                                // を探しそれぞれについてモックのインスタンスを生成する
	 *                                                // @InjectMocks で指定されているそれぞれのフィールドにモックを注入
	 *     }
	 *
	 *     // 正等な座標に対して適切な住所を返す
	 *     @Test
	 *     public void answersAppropriateAdressForValidCoordinates() throws IOException, ParseException {
	 *         // Arrange
	 *         when(http.get(contains("lat=38.000000&lon=-104.000000"))).thenReturn(
	 *             "\"address\":{"
	 *             + "\"house_Number\":\"324\","
	 *             + "\"road\":\"ノーステジョンストリート\","
	 *             + "\"city\":\"コロラドスプリングス\","
	 *             + "\"state\":\"コロラド\","
	 *             + "\"postcode\":\"80903\","
	 *             + "\"country_code\":\"us\"}"
	 *             + "}");
	 *
	 *         // ...
	 *     }
	 * }
	 */

	// モックオブジェクトを注入する際の動き
	// ・適切なコンストラクタを探す
	// ・セッターメソッドを探す
	// ・フィールドに対して探索(フィールドの型に基づいたマッチングが試みられる)

	// AddressRetrieverのコンストラクタはもはや不要

	/** (AddressRetriever)
	 *
	 * public class AddressRetriever {
	 *     private Http http = new HttpImpl();  // <--- static MochitoAnnotations.initMockes(this) が自動で探索し注入する
	 *
	 *     public Address retrieve(double latitude, double longitude) throws IOException, ParseException {
	 *         String parms = String.format("lat=%.6lon=%.6f", latitude, longitude);
	 *         String response = http.get(
	 *             "http://open.mapquestapi.com/nominatim/v1/reverse?format=json&"
	 *             + param);
	 *
	 *         JSONObject obj = (JSONObject)new JSONParser().parse(resonse);
	 *         // ...
	 *     }
	 * }
	 */

	// テスト対象コードの実装を知らなくてもよい

	// 10.7 モックを利用する際のポイント *******************************************************************************

	// モックを使ったテストでは、処理の内容を明確に表現する必要がある

	// このための方法の一つが関連付け(correlation)

	// answersAppropriateddressForValidCoordinates では明らかに期待されるURLパラメータの文字列が
	// Act部分の引数に関連している
	// lat=38.000000&lon=-104.000000 と 38.0, -104.0

	// モックは実際のふるまいを置き換えていることに留意する
	// 置き換えても安全かどうか確認すべきことがある

	// e.g.
	// モックが実運用のコードの振る舞いを本当に再現しているか？
	// 実運用のコードが、テストでは考えられないような形式のデータを返すことはないか？
	// 例外を発生させたり、nullを返したりすることはないか？
	// これらの条件についても別のテストが必要

	// テストではモックが完全に使われているか？
	// 実運用のコードが使われてしまってないか？
	// => シンプルな対策: 実運用のコードを一時的に書き換えて例外を発生させる
	//                    テスト中に例外が発生したら実運用のコードが使われている
	//                    テスト終了時には忘れずにthrow文を削除する
	// => よりよい対策: 実運用のコードでは使われないような架空のデータを使う
	//                  実運用のコードで使われたら失敗するようなデータを用意する
	//                  38.0,-104.0 は明らかに実際のコロラドスプリングの場所を指していない

	// モックを使ったテストは実運用のコードを直接テストしているわけではない
	// モックを導入するとテストのカバー範囲に漏れが生まれる
	// => より上位層でのテスト(統合テスト)も行って、実際のクラスを使ったエンドツーエンドのシナリオを検証する

	// 10.8 まとめ *****************************************************************************************************

	// モックを使って依存先オブジェクトのふるまいを模倣する手法の解説

	// モックの生成と注入を簡単に行うためのツール

	// ８～１０章ではテスト対象のコードがクリーンかつ適切に設計されていることを目指してきた

	// よりよい設計の全体像にはテスト自体への継続的なリファクタリングも含まれる
	// 次章からはテストのクリーンアップ

}
