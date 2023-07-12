package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private Graph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> mappaAereoporti;  //mappa(id-aereoporto)
	
	public Model () {
		
		//this.grafo= new SimpleWeightedGraph(DefaultWeightedEdge.class);
		this.dao= new ExtFlightDelaysDAO();
		this.mappaAereoporti = new HashMap<Integer, Airport>();
		this.dao.loadAllAirports(mappaAereoporti);
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	
	public void creaGrafo(int nAirlines) {
		//per resettare ogni volta che creiamo il grafo
		this.grafo= new SimpleWeightedGraph(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(grafo, this.dao.getVertici(nAirlines, mappaAereoporti));
		List<EdgeClass>  edges = this.dao.getRotte(mappaAereoporti);
		
		for(EdgeClass e : edges) {
			Airport origin = e.getOrigin();
			Airport destination = e.getDestination();
			int N = e.getNumero();
			
			if(grafo.vertexSet().contains(origin) && grafo.vertexSet().contains(destination)) {
				//controlliamo se abbiamo gia messo una rotta
				//es. messo rotta da a->b e non voglio rimettere quella da b->a
				DefaultWeightedEdge edge = this.grafo.getEdge(origin, destination);
				if(edge!=null) {
					//se è presente aggiungo solo il peso
					double weight = this.grafo.getEdgeWeight(edge);
					weight = weight + N;
					this.grafo.setEdgeWeight(origin, destination, weight);
				} else {
					//se non è presente aggiungo tutto
					this.grafo.addEdge(origin, destination);
					this.grafo.setEdgeWeight(origin, destination, N);
				}
			}
		}
		
		System.out.println("Grafao creato");
		System.out.println("ci sono "+this.grafo.vertexSet().size()+" vertici, e "+this.grafo.edgeSet().size()+" archi\n");
	}
	
	public List<Airport> getVertici() {
		List<Airport> vertici = new ArrayList<>(this.grafo.vertexSet());
		Collections.sort(vertici);
		return vertici;
	}
/*
	public List<Airport> percorso (Airport origin, Airport arrival) {
		
			//Visita il grafo partendo da "origin"
			BreadthFirstIterator<Airport, DefaultWeightedEdge> visita = new BreadthFirstIterator<>(this.grafo, origin);
				
			List<Airport> percorso = new ArrayList<Airport>();
			Airport corrente = arrival;
			percorso.add(arrival); //devo aggiungere il vertice iniziale
				
			DefaultWeightedEdge e = visita.getSpanningTreeEdge(corrente);
			while(e!=null) {
				//faccio percorso all'indietro --> è univoca!
				Airport precedente = Graphs.getOppositeVertex(this.grafo, e, corrente); //basta trovare vertice opposto!
				//aggiung i miei aereoporti partendo dall'ultima posizione
				//////percorso.add(0, precedente);
				//stampa lista al contrario: dal fondo all'inizio
				percorso.add(precedente);
				/////corrente=precedente;
					
				e = visita.getSpanningTreeEdge(corrente);
			}	
			
		return percorso;
	}
	*/
	/*
	public boolean esistePercorso(Airport origin, Airport destination) {
		ConnectivityInspector<Airport, DefaultWeightedEdge> inspect = new ConnectivityInspector<Airport, DefaultWeightedEdge>(this.grafo);
		Set<Airport> componenteConnessaOrigine = inspect.connectedSetOf(origin);
		return componenteConnessaOrigine.contains(destination);
	}
	*/
	/**
	 * Metodo che calcola il percorso tra due aeroporti. Se il percorso non viene trovato, 
	 * il metodo restituisce null.
	 * @param origin
	 * @param destination
	 * @return
	 */
	
	public List<Airport> trovaPercorso(Airport origin, Airport destination){
		List<Airport> percorso = new ArrayList<>();
	 	BreadthFirstIterator<Airport,DefaultWeightedEdge> it = new BreadthFirstIterator<>(this.grafo, origin);
	 	Boolean trovato = false;
	 	
	 	//visito il grafo fino alla fine o fino a che non trovo la destinazione
	 	while(it.hasNext() & !trovato) {
	 		Airport visitato = it.next();
	 		if(visitato.equals(destination))
	 			trovato = true;
	 	}
	 
	 	/* se ho trovato la destinazione, costruisco il percorso risalendo l'albero di visita in senso
	 	 * opposto, ovvero partendo dalla destinazione fino ad arrivare all'origine, ed aggiiungo gli aeroporti
	 	 * ad ogni step IN TESTA alla lista
	 	 * se non ho trovato la destinazione, restituisco null.
	 	 */
	
	 	if(trovato) {
	 		percorso.add(destination);
	 		Airport step = it.getParent(destination);
	 		while (!step.equals(origin)) {
	 			percorso.add(0,step);
	 			step = it.getParent(step);
	 		}
		 
		 percorso.add(0,origin);
		 return percorso;
	 	} else {
	 		return null;
	 	}
	}
	
}



















