package me.ingjedagobethm.springboot.reactor.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Flux es un Publisher u Observable
		Flux<String> nombres = Flux.just("Cindy", "Javier", "Adrian", "Fabian", "Duvalier", "Ivonne")
				//.doOnNext(System.out::println); // de lambda a callable
				.map(nombre -> nombre.toLowerCase())
				.filter(elemento -> elemento.contains("y")) // filtra por los elementos que contengas "y"
				.doOnNext(elemento -> { // Para múltiples instrucciones se coloca entre llaves
					if(elemento.isEmpty()){
						throw new RuntimeException("Nombre no puede ser vacio.");
					}
					System.out.println(elemento);
				})
				.map(nombre -> nombre.toUpperCase());

		// Por si solo no es posible "observar" la ejecución del flujo,
		// nos debemos subscribir al evento.
		nombres.subscribe(
				log::info,
				error -> log.error(error.getMessage()),
				new Runnable() {
					@Override
					public void run() {
						log.info("Ha finalizado la ejecución del observable con éxito.");
					}
				}); // Consumer u Observador
	}
}
