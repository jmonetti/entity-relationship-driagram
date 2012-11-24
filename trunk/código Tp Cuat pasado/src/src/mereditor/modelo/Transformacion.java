package mereditor.modelo;

import java.awt.List;
import java.util.Collection;
import java.util.Set;

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
    	
    	dLogico = transformarAtributosCompuestos(diagramaDER, dLogico);
    	
    	return dLogico;
    }

	private Diagrama transformarAtributosCompuestos(Diagrama diagramaDER, Diagrama dLogico) {
		
		//obtengo todas las entidades y chequeo si tiene atributos Compuestos
		Entidad entidadNew;
		Atributo atributoNew;
		
		Set<Entidad> entidades = diagramaDER.getEntidades(false);
		// Recorro todas las entidades para encontrar sus atributos polivalentes
		for( Entidad entidad : entidades){
			
			entidadNew = new Entidad();
			entidadNew.setLogico(true);
			
			for ( Atributo atributo : entidad.getAtributos()){
				
				Collection<Atributo> atributosComp = atributo.getAtributos();
				
				if (atributosComp == null || atributosComp.isEmpty()){
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
					
					//relacionCopia.addParticipante(entidadComp);
					
					
						
						
					
					
				} else if ( atributo.getCardinalidadMinima().equals("1") && atributo.getCardinalidadMaxima().equals("1")){
					///Es polivalente -> transformo a una entidad
					
				} else {
					// no es ni compuesto ni polivalente. Todo Piola.
					entidadNew.addAtributo(atributo);
				}
			}
			
		}
		
		
		return null;
	}
}
