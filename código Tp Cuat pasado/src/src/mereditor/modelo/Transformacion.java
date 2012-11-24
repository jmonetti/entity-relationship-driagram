package mereditor.modelo;

import java.awt.List;
import java.util.Collection;
import java.util.Set;

import mereditor.modelo.Relacion.EntidadRelacion;
import mereditor.modelo.Relacion.TipoRelacion;

public class Transformacion {

	private static Transformacion INSTANCE = null;
	 
    private Transformacion() {}
 
    private synchronized static void createInstance() {
        if (INSTANCE == null)  
            INSTANCE = new Transformacion();
        
    }
 
    public static Transformacion getInstance() {
        
    	createInstance();
        
    	return INSTANCE;
    }
    
    public Diagrama tranformarALogico(Diagrama diagramaDER, Proyecto proyecto){
    	
    	Diagrama dLogico = new Diagrama(proyecto);
    	dLogico.setLogico(true);
    	
    	dLogico = transformarAtributos(diagramaDER, dLogico);
    	    	
    	return dLogico;
    }

	private Diagrama transformarAtributos(Diagrama diagramaDER, Diagrama dLogico) {
		
		//obtengo todas las entidades y chequeo si tiene atributos Compuestos
		Entidad entidadNew;
				
		Set<Entidad> entidades = diagramaDER.getEntidades(false);
		// Recorro todas las entidades para encontrar sus atributos polivalentes
		for( Entidad entidad : entidades){
			
			entidadNew = new Entidad(entidad);
			entidadNew.setLogico(true);
			
			for ( Atributo atributo : entidad.getAtributos()){
				
				Collection<Atributo> atributosComp = atributo.getAtributos();
				
				if ( atributosComp != null && !atributosComp.isEmpty()){
					//Es Compuesto -> Cada atributo compuesto de un componente pasa a ser un un 
					//                atributo simple de la nueva entidad que contiene al compuesto 
					
					Entidad entidadComp = new Entidad(atributo.getNombre());
					entidadComp.setLogico(true);
					
					for ( Atributo at : atributosComp){
						Atributo atributoCopia = new Atributo(at);
						atributoCopia.setLogico(true);
						entidadComp.addAtributo(atributoCopia);
					}
					
					Relacion relacionCopia = new Relacion("RelacionCompuesta"+ atributo.getNombre());
					relacionCopia.setLogico(true);
					
					EntidadRelacion er1 = relacionCopia.new EntidadRelacion(relacionCopia);
					er1.setCardinalidadMaxima("1");
					er1.setCardinalidadMinima("1");
					er1.setEntidad(entidadNew);
					er1.setRol("");
					
					EntidadRelacion er2 = relacionCopia.new EntidadRelacion(relacionCopia);
					er2.setCardinalidadMaxima(atributo.getCardinalidadMaxima());
					er2.setCardinalidadMinima(atributo.getCardinalidadMinima());
					er2.setEntidad(entidadComp);
					er2.setRol("");
					
					relacionCopia.addParticipante(er1);
					relacionCopia.addParticipante(er2);
					
					entidadComp.addRelacion(relacionCopia);
					entidadNew.addRelacion(relacionCopia);
					
					//Agrego la nueva entidad creada a partir del tributo compuesto al diagrama
					dLogico.agregar(entidadComp);
					
				} else if ( !atributo.getCardinalidadMinima().equals("1") || !atributo.getCardinalidadMaxima().equals("1")){
					///Es polivalente -> transformo a una entidad
					
					Entidad entidadComp = new Entidad(atributo.getNombre());
					entidadComp.setLogico(true);
					
					Relacion relacionCopia = new Relacion("RelacionPolivalente"+ atributo.getNombre());
					relacionCopia.setLogico(true);
					
					EntidadRelacion er1 = relacionCopia.new EntidadRelacion(relacionCopia);
					er1.setCardinalidadMaxima("1");
					er1.setCardinalidadMinima("1");
					er1.setEntidad(entidadNew);
					er1.setRol("");
					
					EntidadRelacion er2 = relacionCopia.new EntidadRelacion(relacionCopia);
					er2.setCardinalidadMaxima(atributo.getCardinalidadMaxima());
					er2.setCardinalidadMinima(atributo.getCardinalidadMinima());
					er2.setEntidad(entidadComp);
					er2.setRol("");
					
					relacionCopia.addParticipante(er1);
					relacionCopia.addParticipante(er2);
					
					entidadComp.addRelacion(relacionCopia);
					entidadNew.addRelacion(relacionCopia);
					
					//Agrego la nueva entidad creada a partir del tributo compuesto al diagrama
					dLogico.agregar(entidadComp);
					
				} else {
					// no es ni compuesto ni polivalente. Todo Piola.
					 Atributo aux = new Atributo(atributo);
					 aux.setLogico(true);
					 
					entidadNew.addAtributo(aux);
				}
			}
			
			dLogico.agregar(entidadNew);			
		}
		
		
		return dLogico;
	}
}
