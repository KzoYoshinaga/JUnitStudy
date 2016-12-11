package junitTest;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MockTestTest {
	@Mock private Plus plus;
	@InjectMocks private MockTest mockTest;

	@Before
	public void init() {
		mockTest = new MockTest();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() {
		// Arrange
		when(plus.plus(eq(1), eq(2))).thenReturn(3);

		// Act
		int result = mockTest.getPlusAndSquare(1, 2);

		// Assert
		assertThat(result, equalTo(9));
	}

	// モック内のメソッドに与えられる引数がマッチしない場合テスト失敗
	@Test
	public void testErro() {
		// Arrange
		when(plus.plus(eq(1), eq(2))).thenReturn(3);

		// Act
		int result = mockTest.getPlusAndSquare(4, 2);

		// Assert
		assertThat(result, CoreMatchers. equalTo(9));
	}

}
