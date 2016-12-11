package junitTest;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.List;

public class ArgsTest {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run() {
				List<String> arguments = Arrays.asList(args);
				arguments.stream().forEach(System.out::println);
			}

		});
	}
}
