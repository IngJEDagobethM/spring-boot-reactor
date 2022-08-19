package me.ingjedagobethm.springboot.reactor.app;

import me.ingjedagobethm.springboot.reactor.app.models.Comentario;
import me.ingjedagobethm.springboot.reactor.app.models.Usuario;
import me.ingjedagobethm.springboot.reactor.app.models.UsuarioComentario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//transformaFlujo();
		//transformaFlujoFlatMap();
		//transformaFlujoToString();
		//transformaFlujoToCollectList();
		//mergeFlujosFlatMap();
		//mergeFlujosZipWith();
		//mergeFlujosZipWithTuple();
		mergeFlujosZipWithRange();
	}

	public List<Usuario> usandoArrayUsuario(){
		List<Usuario> usuarios = new ArrayList<>();
		usuarios.add(new Usuario ("Cindy"));
		usuarios.add(new Usuario ("Javier"));
		usuarios.add(new Usuario ("Adrian"));
		usuarios.add(new Usuario ("Fabian"));
		usuarios.add(new Usuario ("Duvalier"));
		usuarios.add(new Usuario ("Ivonne"));
		return usuarios;
	}

	public List<String> usandoArray(){
		List<String> nombres = new ArrayList<>();
		nombres.add("Cindy");
		nombres.add("Javier");
		nombres.add("Adrian");
		nombres.add("Fabian");
		nombres.add("Duvalier");
		nombres.add("Ivonne");
		return nombres;
	}

	public Flux<String> usandoJust() {
		// Flux es un Publisher u Observable
		Flux<String> nombres = Flux.just("Cindy", "Javier", "Adrian", "Fabian", "Duvalier", "Ivonne");
		return nombres;
	}

	public void transformaFlujo(){
		Flux<String> nombres = usandoJust();
				//.doOnNext(System.out::println); // de lambda a callable
		Flux<String> newNombres = nombres.map(String::toLowerCase)
				.filter(elemento -> elemento.contains("y")) // filtra por los elementos que contengas "y"
				.doOnNext(elemento -> { // Para múltiples instrucciones se coloca entre llaves
					if(elemento.isEmpty()){
						throw new RuntimeException("Nombre no puede ser vacio.");
					}
					System.out.println(elemento);
				})
				.map(String::toUpperCase);

		// Por si solo no es posible "observar" la ejecución del flujo,
		// nos debemos subscribir al evento.
		// recordar que los flujos son inmutables.
		//nombres.subscribe( // Imprime el flujo original
		newNombres.subscribe( // Imprime el flujo transformado
				log::info,
				error -> log.error(error.getMessage()),
				new Runnable() {
					@Override
					public void run() {
						log.info("Ha finalizado la ejecución del observable con éxito.");
					}
				}); // Consumer u Observador
	}

	public void transformaFlujoFlatMap(){
		Flux<String> nombres = Flux.fromIterable(usandoArray())
				.map(String::toLowerCase)
				.flatMap(elemento -> {
					if(elemento.contains("n")){
						return Mono.just(elemento); // retorna un observable
					}else{
						return Mono.empty();
					}
				})
				.map(String::toUpperCase);

		nombres.subscribe(log::info);
	}

	public void transformaFlujoToString(){
		Flux.fromIterable(usandoArrayUsuario())
				.map(Usuario::toString)
				.flatMap(elemento -> {
					if(elemento.contains("an")){
						return Mono.just(elemento);
					}else{
						return Mono.empty();
					}
				})
				.map(String::toUpperCase)
				.subscribe(log::info);
	}

	public void transformaFlujoToCollectList(){
		Flux.fromIterable(usandoArrayUsuario())
				.collectList()
				//.subscribe(usuario -> log.info(usuario.toString()));
				.subscribe(lista -> log.info(lista.toString()));
	}

	public void mergeFlujosFlatMap(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Pepito"));
		Mono<Comentario> comentarioMono = Mono.fromCallable(() -> {
			Comentario comentario = new Comentario();
			comentario.addComentario("¡Hola!");
			comentario.addComentario("Este es mi super comentario.");
			comentario.addComentario("¡Adios!");
			return comentario;
		});

		usuarioMono.flatMap(usuario -> comentarioMono.map(comentario -> new UsuarioComentario(usuario, comentario)))
				.subscribe(usuarioComentario -> log.info(usuarioComentario.toString()));
	}

	public void mergeFlujosZipWith(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Pepito"));
		Mono<Comentario> comentarioMono = Mono.fromCallable(() -> {
			Comentario comentario = new Comentario();
			comentario.addComentario("¡Hola!");
			comentario.addComentario("Este es mi super comentario.");
			comentario.addComentario("¡Adios!");
			return comentario;
		});

		usuarioMono.zipWith(comentarioMono, (usuario, comentario) -> new UsuarioComentario(usuario, comentario))
				.subscribe(usuarioComentario -> log.info(usuarioComentario.toString()));
	}

	public void mergeFlujosZipWithTuple(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Pepito"));
		Mono<Comentario> comentarioMono = Mono.fromCallable(() -> {
			Comentario comentario = new Comentario();
			comentario.addComentario("¡Hola!");
			comentario.addComentario("Este es mi super comentario.");
			comentario.addComentario("¡Adios!");
			return comentario;
		});

		usuarioMono.zipWith(comentarioMono)
				.map((tuple) -> new UsuarioComentario(tuple.getT1(), tuple.getT2()))
				.subscribe(usuarioComentario -> log.info(usuarioComentario.toString()));
	}

	public void mergeFlujosZipWithRange(){
		Flux.just(1,2,3,4,5,6)
				.map(i -> (i*2))
				.zipWith(Flux.range(1,6), (uno, dos) -> String.format("Primer Flux: %d, Segundo Flux: %d", uno, dos))
				.subscribe(texto -> log.info(texto));
	}
}
