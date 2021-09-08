package torneo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.TreeMap;

public class Torneo {
	/*
	 * Tiene una clave que es el numero del participante y una cola de prioridad
	 * donde voy a guardar todos los puntajes de cada uno de los tiros de ese
	 * participante.
	 */
	private Map<Integer, PriorityQueue<Integer>> participantes;
	/*
	 * Este va a ser para el podio o sea que el Key va a ser el puntaje y despues
	 * una lista con los numeros de los participantes que hicieron ese puntaje.
	 */
	private TreeMap<Integer, LinkedList<Integer>> podio;

	public Torneo(String archivo) throws FileNotFoundException {
		setParticipantes(archivo);
		setPodio();
	}

	private void setParticipantes(String archivo) throws FileNotFoundException {
		/*
		 * Key un entero y la cola de prioridad tiene los mejores puntajes de todos los
		 * tiros de ese participante.
		 */
		participantes = new HashMap<Integer, PriorityQueue<Integer>>();

		Scanner sc = new Scanner(new File(archivo));

		while (sc.hasNext()) {
			String[] datos = sc.nextLine().split(",");
			Integer key = Integer.parseInt(datos[0]);
			Integer puntaje = getPuntaje(Double.parseDouble(datos[1]), Double.parseDouble(datos[2]));

			if (puntaje > 0) {
				if (participantes.containsKey(key)) {
					/*
					 * get(key) me devuelve la cola del prioridad del participante y le agrego el
					 * puntaje.
					 */
					participantes.get(key).offer(puntaje);
					/* Si no existe esa cola de prioridad la construyo. */
				} else {
					/*
					 * El Collections.reverseOrder() quiere decir que esa cola de prioridad de
					 * enteros va a estar ordenada por la prioridad del dato Integer que es el valor
					 * del Integer, pero de mayor a menor.
					 */
					PriorityQueue<Integer> colaP = new PriorityQueue<Integer>(Collections.reverseOrder());
					colaP.offer(puntaje);
					/* Entonces el par, clave y cola, los agrego al mapa de participantes. */
					participantes.put(key, colaP);
				}
			}

		}
		sc.close();
	}

	private void setPodio() {
		/* Inicializamos el TreeMap con el parametro Collections.reverseOrder(). */
		podio = new TreeMap<Integer, LinkedList<Integer>>(Collections.reverseOrder());

		/* Recorrido del mapa. */
		for (Map.Entry<Integer, PriorityQueue<Integer>> entry : this.participantes.entrySet()) {
			/*
			 * Como solo se consideran los arqueros con 5 tiros válidos o más por lo tanto
			 * para cada entry le voy a pedir el valor y el size de ese valor.
			 */
			if (entry.getValue().size() >= 5) {
				Integer partipante = entry.getKey();
				Integer suma = 0;
				for (int i = 0; i < 5; i++) {
					/* poll() siempre me devuelve el primero y lo saca. */
					suma += entry.getValue().poll();
				}
				/* Ahora la clave va a ser la suma del podio. */
				if (podio.containsKey(suma)) {
					podio.get(suma).add(partipante);
				} else {
					/* Y sino la contiene agrego el par al podio. */
					LinkedList<Integer> listaP = new LinkedList<Integer>();
					listaP.add(partipante);
					podio.put(suma, listaP);
				}
			}
		}
	}

	public void getSalida() throws FileNotFoundException {
		Iterator<Map.Entry<Integer, LinkedList<Integer>>> itr = this.podio.entrySet().iterator();
		PrintWriter salida = new PrintWriter(new File("podio.out"));
		int i = 1;
		while (itr.hasNext() && i <= 3) {
			Map.Entry<Integer, LinkedList<Integer>> entry = itr.next();
			System.out.println(i + "º puesto: " + entry.getKey() + " puntos, participante: " + entry.getValue());
			salida.println(i + "º puesto: " + entry.getKey() + " puntos, participante: " + entry.getValue());
			i++;
		}
		salida.close();
	}

	private Integer getPuntaje(Double x, Double y) {
		Integer puntaje = 0;
		Double distanciaAlCentro = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		if (distanciaAlCentro <= 10)
			puntaje = 1000;
		else if (distanciaAlCentro <= 20)
			puntaje = 500;
		else if (distanciaAlCentro <= 30)
			puntaje = 200;
		else if (distanciaAlCentro <= 40)
			puntaje = 100;
		else if (distanciaAlCentro <= 50)
			puntaje = 50;
		else if (distanciaAlCentro > 50)
			puntaje = -1;

		return puntaje;
	}

}
