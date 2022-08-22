package me.ingjedagobethm.springboot.reactor.app;

import me.ingjedagobethm.springboot.reactor.app.models.Comentario;
import me.ingjedagobethm.springboot.reactor.app.models.Usuario;
import me.ingjedagobethm.springboot.reactor.app.models.UsuarioComentario;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		transformaFlujo();
		//transformaFlujoFlatMap();
		//transformaFlujoToString();
		//transformaFlujoToCollectList();
		//mergeFlujosFlatMap();
		//mergeFlujosZipWith();
		//mergeFlujosZipWithTuple();
		//mergeFlujosZipWithRange();
		//flujoInterval();
		//flujoDelayElements();
		//flujoIntervalInfinito();
		//crearObservableDesdeCero();
		//contrapresionManual();
		//contrapresionOperador();
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

	public void flujoInterval(){
		Flux<Integer> rango = Flux.range(1, 12);
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));

		rango.zipWith(retraso, (r, rr) -> r)
				.doOnNext(i -> log.info(i.toString()))
				//.subscribe(); // No es posible observar debido al no bloqueo de procesos
				.blockLast(); // Se subscribe al flujo pero bloquea el proceso (para poder ver la salida) **No Recomendable**
	}

	public void flujoDelayElements(){
		Flux<Integer> rango = Flux.range(1, 12)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(i -> log.info(i.toString()));

		rango.subscribe();
		//.blockLast(); // Se subscribe al flujo pero bloquea el proceso (para poder ver la salida) **No Recomendable**
	}

	public void flujoIntervalInfinito() throws InterruptedException {

		CountDownLatch latch = new CountDownLatch(1);

		Flux.interval(Duration.ofSeconds(1))
				.doOnTerminate(latch::countDown)
				.flatMap(i -> {
					if(i > 4){
						return Flux.error(new InterruptedException("Solo hasta 5!"));
					}
					return Flux.just(i);
				})
				.map(i -> "#".concat(i.toString()))
				.retry(2) // Reintenta las veces definidas si detecta error.
				.subscribe(s -> log.info(s), e -> log.error(e.getMessage()));

		latch.await();
	}

	public void crearObservableDesdeCero(){
		Flux.create(emiter -> {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				private Integer contador = 0;
				@Override
				public void run() {
					emiter.next(++contador);
					if(contador == 10){
						timer.cancel(); // Detiene el proceso Timer
						emiter.complete(); // Termina el flujo
					}
					if (contador == 5){
						timer.cancel();
						emiter.error(new InterruptedException("El contador lanzó 5. :("));
					}
				}
			}, 1000, 1000);
		}).subscribe(
				next -> log.info(next.toString()),
				error -> log.error(error.getMessage()),
				() -> log.info("Finalizado.")); // OnCompleted se lanza solo si finaliza con éxito.
	}

	public void contrapresionManual(){
		Flux.range(1, 20)
				.log()
				.subscribe(new Subscriber<Integer>() { // <tipo_dato> elemenos del flujo
					private Subscription s;
					private Integer limite = 5;
					private Integer consumido = 0;
					@Override
					public void onSubscribe(Subscription subscription) {
						this.s = subscription;
						// s.request(Long.MAX_VALUE); // Pide el número máximo de elementos del flujo
						s.request(limite); // Pide 2 elementos del flujo
					}

					@Override
					public void onNext(Integer integer) {
						log.info(integer.toString());
						consumido++;
						if(consumido == limite){
							consumido = 0;
							s.request(limite);
						}
					}

					@Override
					public void onError(Throwable throwable) {

					}

					@Override
					public void onComplete() {

					}
				});
	}

	public void contrapresionOperador(){
		Flux.range(1, 20)
				.log()
				.limitRate(5)
				.subscribe(i -> log.info(i.toString()));
	}
}
