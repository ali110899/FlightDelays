package it.polito.tdp.extflightdelays.model;

public class EdgeClass {

	private Airport origin;
	private Airport destination;
	private int numero;
	
	public EdgeClass(Airport origin, Airport destination, int numero) {
		super();
		this.origin = origin;
		this.destination = destination;
		this.numero = numero;
	}

	public Airport getOrigin() {
		return origin;
	}

	public Airport getDestination() {
		return destination;
	}

	public int getNumero() {
		return numero;
	}

	@Override
	public String toString() {
		return "EdgeClass [origin=" + origin + ", destination=" + destination + ", numero=" + numero + "]";
	}
	
	
	
}
