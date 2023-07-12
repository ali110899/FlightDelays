package it.polito.tdp.extflightdelays.model;

import java.util.List;

public class TestModel {

	public static void main(String[] args) {

		Model model = new Model();
		
		model.creaGrafo(8);
		System.out.println(model.getVertici());
		
	}

}