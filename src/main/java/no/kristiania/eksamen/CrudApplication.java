package no.kristiania.eksamen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrudApplication {

	public static void main(String[] args) {
		String thing = System.getenv("JDBC_DATABASE_URL" );
		System.out.println("JDBC_DATABASE_URL IS " + thing);
		thing = System.getenv("JDBC_DATABASE_USERNAME");
		System.out.println("JDBC_DATABASE_USERNAME IS " + thing);
		thing = System.getenv("DATABASE_URL");
		System.out.println("DATABASE_URL IS " + thing);
		//SpringApplication.run(CrudApplication.class, args);
	}
}

