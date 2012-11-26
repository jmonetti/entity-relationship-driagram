package mereditor.modelo;

 
import java.util.Collection;
import java.util.Set;

import mereditor.control.DiagramaControl;
import mereditor.modelo.Relacion.EntidadRelacion;
 

public class Transformacion {

	private static Transformacion INSTANCE = null;
	private static int i = 0;
	 
    private Transformacion() {}
 
    private synchronized static void createInstance() {
        if (INSTANCE == null)  
            INSTANCE = new Transformacion();
        
    }
 
    public static Transformacion getInstance() {
        
    	createInstance();
        
    	return INSTANCE;
    }
    
    public DiagramaControl tranformarALogico(Diagrama diagramaDER, Proyecto proyecto){
    	
    	DiagramaControl dLogico = new DiagramaControl(proyecto);
    	dLogico.setLogico(true);
    	
    	dLogico = transformarAtributos(diagramaDER, dLogico);
    	dLogico = eliminarEntidadesGenerales(dLogico);
    	
    	dLogico.setNombre("DiagramaLogico" + i);
    	i++;
    	    	
    	return dLogico;
    }

	private DiagramaControl transformarAtributos(Diagrama diagramaDER, DiagramaControl dLogico) {
		
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
	
	
	/**
	 *  Para la conversion jerarquica se Eliminan la entidades generales
	 *  y heredandan a las especializadas sus atributos y relaciones
	 * @param dLogico Diagrama Logico al que se le realiza la transformacion de jerarquia
	 * @return Diagrama logico transformado
	 */
	private DiagramaControl eliminarEntidadesGenerales(DiagramaControl dLogico) {
		
		
		//obtengo todas las jerarquias del diagrama 
		//@param false: dado que un componente diagrama no tiene padre 
		Set<Jerarquia> jerarquiasDiagrama = dLogico.getJerarquias(false);
		
		//Para cada jerarquia voy a pasar los atributos a las entidades hijas
		for( Jerarquia jerarquia : jerarquiasDiagrama){
			Entidad generica = jerarquia.getGenerica();
			
			//recorro todas las entidades derivadas para agregarle los atributos del padre
			for(Entidad derivada: jerarquia.getDerivadas()){
				
				//recorro toodos los atributos del padre y se los agrego al hijo
				for(Atributo atributo : generica.getAtributos()){
					derivada.addAtributo(atributo);
				}
				
				//recorro todas las relaciones del padre y se las agrego al hijo
				for(Relacion relacion : generica.getRelaciones()){
					
					relacion.modificarParticipante(generica.getId(),derivada);
					derivada.addRelacion(relacion);
				}
				
				//Elimino padre del hijo
				derivada.removePadre(derivada.getId());
			}
			//Elimino al componente padre del diagrama
			dLogico.eliminar(generica);
		}
		
		//Elimino todas las jerarquias del diagrama 
		for( Jerarquia jerarquia : jerarquiasDiagrama){
			dLogico.eliminar(jerarquia);
		}
	
		
		return dLogico;		
	}
}
