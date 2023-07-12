package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.EdgeClass;
import it.polito.tdp.extflightdelays.model.Flight;

public class ExtFlightDelaysDAO {

	//modifica inserendo mappa
	public List<Airline> loadAllAirlines(Map<Integer, Airport> mappa) {
		
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				Airline airline = new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE"));
				result.add(airline);
				//mappa.put(rs.getInt("ID"), airport);
				}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> loadAllAirports(Map<Integer, Airport> mappa) {
		
		String sql = "SELECT * FROM airports";
		 List<Airport> listaAereoporti =  new ArrayList<Airport>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				mappa.put(rs.getInt("ID"), airport);
				listaAereoporti.add(airport);
			}

			conn.close();
			return listaAereoporti;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Airport> getVertici(int nAirlines, Map<Integer, Airport> mappa) {
		
		//mi prendo come vertici gli aereoporti che hanno linee maggiori di"?"
		String sql = "SELECT tmp.ID, tmp.IATA_CODE, COUNT(*) AS N "
				+ "FROM (SELECT a.ID,  a.IATA_CODE, f.`AIRLINE_ID`, COUNT(*) AS n "
				+ "FROM flights f, airports a "
				+ "WHERE f.`ORIGIN_AIRPORT_ID`= a.ID OR f.`DESTINATION_AIRPORT_ID` = a.ID "
				+ "GROUP BY a.ID,  a.IATA_CODE, f.`AIRLINE_ID`)tmp "
				+ "GROUP BY tmp.ID, tmp.IATA_CODE "
				+ "HAVING N>=?";
		List<Airport> listaAereoporti = new ArrayList<Airport>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, nAirlines);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				listaAereoporti.add(mappa.get(rs.getInt("ID")));
			}

			conn.close();
			return listaAereoporti;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
public List<EdgeClass> getRotte(Map<Integer, Airport> mappa) {
		
		//vado a prendermi le coppie partenza-arrivo delle linee e faccio count sui voli
		String sql = "SELECT f.`ORIGIN_AIRPORT_ID`, f.`DESTINATION_AIRPORT_ID`, COUNT(*) AS n "
				+ "FROM flights f "
				+ "GROUP BY f.`ORIGIN_AIRPORT_ID`, f.`DESTINATION_AIRPORT_ID`";
		List<EdgeClass> rotte = new ArrayList<EdgeClass>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				EdgeClass rotta = new EdgeClass(mappa.get(rs.getInt("ORIGIN_AIRPORT_ID")), 
							mappa.get(rs.getInt("DESTINATION_AIRPORT_ID")),
							rs.getInt("n"));
				rotte.add(rotta);
				
			}

			conn.close();
			return rotte;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
}