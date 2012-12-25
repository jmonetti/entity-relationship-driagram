package mereditor.modelo;

 
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Collections;
import java.util.List;


import mereditor.control.AtributoControl;
import mereditor.control.DiagramaControl;
import mereditor.control.EntidadControl;
import mereditor.control.RelacionControl;
import mereditor.interfaz.swt.figuras.EntidadFigure;
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
    
    /**
     * @return diagrama convertido a logico, si el diagrama pasado por parametro es logico retorno null
     */
    public DiagramaControl tranformarALogico(Diagrama diagramaDER, Proyecto proyecto, int tipo){
    	
    	 
    	
    	if(!diagramaDER.esLogico()){//TODO agregar despues de las pruebas-> && diagramaDER.validar() == null){
	    	DiagramaControl dLogico = new DiagramaControl(proyecto);
	    	dLogico.setLogico(true);
	    	
	    	switch (tipo) {
			case 2: dLogico = transformarAtributosTipo3(diagramaDER, dLogico,proyecto);
					break;
			case 3: dLogico = transformarAtributosTipo1BIS(diagramaDER, dLogico,proyecto);
					break;		
			default:dLogico = transformarAtributosTipo2(diagramaDER, dLogico,proyecto);
					break;
			}
	    	
	    	
	    	
	    	dLogico.setNombre("DiagramaLogico-" + diagramaDER.getNombre());
	    	i++;
	    	diagramaDER.agregar(dLogico);	    	
	    	return dLogico;
    	}
    	else return null;
    }

	private DiagramaControl transformarAtributosTipo2(Diagrama diagramaDER, DiagramaControl dLogico,Proyecto proyecto) {
		
		//**********
		//Variable para guardar todas las entidades que sean padres en una jerarquia para no agregarla al logico directmente
    	List<Entidad> entidadesPadres = new ArrayList<Entidad>();
		//variable para guardar un mapa con Id de la entidad del DER que mapee con la entidad del logico
    	HashMap<String,String> mapaEntidades = new HashMap<String,String>();
    	//**************
    	
		//obtengo todas las entidades y chequeo si tiene atributos Compuestos
		EntidadControl entidadNew;
				
		Set<Entidad> entidades = diagramaDER.getEntidades(false);
		// Recorro todas las entidades para encontrar sus atributos polivalentes
		for( Entidad entidad : entidades){
			
			entidadNew = new EntidadControl(entidad);
			entidadNew.setLogico(true);
			
			for ( Atributo atributo : entidad.getAtributos()){
				
				Collection<Atributo> atributosComp = atributo.getAtributos();
				
				if ( atributosComp != null && !atributosComp.isEmpty()){
					//Es Compuesto -> Cada atributo compuesto de un componente pasa a ser un un 
					//                atributo simple de la nueva entidad que contiene al compuesto 
					
					EntidadControl entidadComp = new EntidadControl(atributo.getNombre());
					entidadComp.setLogico(true);
					
					for ( Atributo at : atributosComp){
						AtributoControl atributoCopia = new AtributoControl(at);
						atributoCopia.setLogico(true);
						entidadComp.addAtributo(atributoCopia);	
						//proyecto.agregarSoloAlProyecto(atributoCopia);
						//dLogico.agregar(atributoCopia);
					}
					
					RelacionControl relacionCopia = new RelacionControl("RelacionCompuesta"+ atributo.getNombre());
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
					
					//entidadComp.addRelacion(relacionCopia);
					//entidadNew.addRelacion(relacionCopia);
					
					proyecto.agregarSoloAlProyecto(relacionCopia);
					dLogico.agregar(relacionCopia);
					
					//Agrego la nueva entidad creada a partir del tributo compuesto al diagrama
					dLogico.agregar(entidadComp);
					proyecto.agregarSoloAlProyecto(entidadComp);
					
				} else if ( !atributo.getCardinalidadMinima().equals("1") || !atributo.getCardinalidadMaxima().equals("1")){
					///Es polivalente -> transformo a una entidad
					
					EntidadControl entidadComp = new EntidadControl(atributo.getNombre());
					entidadComp.setLogico(true);
					
					RelacionControl relacionCopia = new RelacionControl("R - "+ atributo.getNombre());
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
					
					
					
					EntidadFigure figura = entidadComp.getFigura(diagramaDER.getId());
					
					figura.setBounds(figura.getBounds().getTranslated(300, 300));
					
					//Agrego la nueva entidad creada a partir del tributo compuesto al diagrama
					dLogico.agregar(entidadComp);
					dLogico.agregar(relacionCopia);
					proyecto.agregarSoloAlProyecto(entidadComp);
					proyecto.agregarSoloAlProyecto(relacionCopia);
					
					
				} else {
					// no es ni compuesto ni polivalente. Todo Piola.
					 AtributoControl aux = new AtributoControl(atributo);
					 aux.setLogico(true);
					 
					entidadNew.addAtributo(aux);
				}
			}
			
			//Si es una entidad Padre lo agrego solamente a entidadesPadres dado que no van a ir en el logico( despues uso sus att y relaciones para pasasrselas al hijo)
			if ( !esPadre(entidad,proyecto)){
				dLogico.agregar(entidadNew);
				proyecto.agregarSoloAlProyecto(entidadNew);
			}
			else {
				entidadesPadres.add(entidadNew);
			}
			//Agrego al mapa de ids  el id DER mapeando con el idLogico
			mapaEntidades.put(entidad.getId(), entidadNew.getId());
		}
		
		/* Agrego Relaciones */
		for ( Relacion relacion : diagramaDER.getRelaciones(false)){
			
			//Creo una nueva relacion
			RelacionControl relacionCopia = new RelacionControl("Polivalente"+ relacion.getNombre());
			relacionCopia.setLogico(true);
			
			//recorro todos los participantes de la relacion
			for(EntidadRelacion er : relacion.getParticipantes()){
				
				// Si es padre, le voy a agregar la relacion a los hijos
				if(esPadre(er.getEntidad(), proyecto)){
				
					//obtengo todos los hijos
					List<Entidad> hijos = new ArrayList<Entidad>();
					
					for ( Jerarquia j : proyecto.getJerarquias() ){
						
						if(j.getGenerica().getId().equals(er.getEntidad().getId())){
							
							for(Entidad e : j.getDerivadas()){
								hijos.add(e);
							}
							
						}
					}
					
					//Agrego a todos los hijos a la relacion
					for( Entidad hijoDer : hijos){
					
						//obtengo el hijo logico
						Entidad hijoLogico = null;
						for(Entidad ehl : dLogico.getEntidades(false)){
							if ( mapaEntidades.get(hijoDer.getId()).equals(ehl.getId())){
								hijoLogico = ehl;
								break;
							}
						}
												
						EntidadRelacion er1 = relacionCopia.new EntidadRelacion(relacionCopia);
						er1.setCardinalidadMaxima(er.getCardinalidadMaxima());
						er1.setCardinalidadMinima(er.getCardinalidadMinima());
						er1.setEntidad(hijoLogico);
						er1.setRol(er.getRol());
												
						relacionCopia.addParticipante(er1);
					}
					
					
				}
				//Si no es una entidad padre agrego la entidad a la relacion
				else {
					//obtengo el hijo logico
					Entidad hijoLogico = null;
					for(Entidad ehl : dLogico.getEntidades(false)){
						if ( mapaEntidades.get(er.getEntidad().getId()).equals(ehl.getId())){
							hijoLogico = ehl;
							break;
						}
					}
											
					EntidadRelacion er1 = relacionCopia.new EntidadRelacion(relacionCopia);
					er1.setCardinalidadMaxima(er.getCardinalidadMaxima());
					er1.setCardinalidadMinima(er.getCardinalidadMinima());
					er1.setEntidad(hijoLogico);
					er1.setRol(er.getRol());
											
					relacionCopia.addParticipante(er1);
				}
			}
			
			proyecto.agregarSoloAlProyecto(relacionCopia);
			dLogico.agregar(relacionCopia);
		}
		
		/* Agrego Atributos del padre al hijo */
		for( Entidad ePadre : entidadesPadres ){
			
			//obtengo todos los hijos
			List<Entidad> hijos = new ArrayList<Entidad>();
			
			for ( Jerarquia j : proyecto.getJerarquias() ){
			
				if(mapaEntidades.get(j.getGenerica().getId()).equals(ePadre.getId())){
					
					for(Entidad e : j.getDerivadas()){
						hijos.add(e);
					}
					
				}
			}
			
			//Para todos los hijos busco su logico y le agrego los atributos del padre
			for( Entidad hijoDer : hijos){
				
				//obtengo el hijo logico
				Entidad hijoLogico = null;
				for(Entidad ehl : dLogico.getEntidades(false)){
					if ( mapaEntidades.get(hijoDer.getId()).equals(ehl.getId())){
						hijoLogico = ehl;
						
						for(Atributo atributo : ePadre.getAtributos()){
						
							AtributoControl aux = new AtributoControl(atributo);
							 aux.setLogico(true);
							 
							 hijoLogico.addAtributo(aux);
						}
						// cambio todas las relaciones del padre a la entidad hijo
						for(Relacion relacion : ePadre.getRelaciones()){
							for(EntidadRelacion er : relacion.getParticipantes()){
								if(er.getEntidad().getId().equals(ePadre.getId())){
									er.setEntidad(ehl);
								}
							}													
						}
						
						break;
					}
				}
			}
			

		}
		
		
		
		return dLogico;
	}
	
	// Si es una entidad PAdre(generica) devuelve true, sino false
	private boolean esPadre(Entidad entidad, Proyecto proyecto) {
		
		for ( Jerarquia j : proyecto.getJerarquias()){
			
			if(j.getGenerica().getId().equals(entidad.getId()))
				return true;
		}
		
		return false;
	}
	
	
	private boolean esHijo(Entidad entidad, Proyecto proyecto) {
		
		for ( Jerarquia j : proyecto.getJerarquias()){
			
			for(Entidad hijo : j.getDerivadas()){
				if(hijo.getId().equals(entidad.getId()))
					return true;
			}
			
			
		}
		
		return false;
	}

	private EntidadControl agregarAtributosJerarquia(EntidadControl entidadNew, Diagrama diagramaDER, Entidad entidad,Proyecto proyecto) {
		
		 Set<Jerarquia> jerarquias = diagramaDER.getJerarquias(true);
		 
		/* for( Jerarquia j : jerarquias){
			 for(Entidad e : j.getDerivadas()){
				 if(e.getId().equals(entidad.getId())){
					//recorro toodos los atributos del padre y se los agrego al hijo
					for(Atributo atributo : j.getGenerica().getAtributos()){
						
						AtributoControl atributoCopia = new AtributoControl(atributo);
						atributoCopia.setLogico(true);
						entidadNew.addAtributo(atributoCopia);	
					}
					
					//recorro todas las relaciones del padre y se las agrego al hijo
					/*for(Relacion relacion : j.getGenerica().getRelaciones()){
						
						relacion.modificarParticipante(j.getGenerica().getId(),entidadNew);
						entidadNew.addRelacion(relacion);
						proyecto.agregarSoloAlProyecto(relacion);
						
					}
					 
				 }
					 
			 }
		 }*/
		
		return entidadNew;
	}

	/**
	 *  Para la conversion jerarquica se Eliminan la entidades generales
	 *  y heredandan a las especializadas sus atributos y relaciones
	 * @param dLogico Diagrama Logico al que se le realiza la transformacion de jerarquia
	 * @return Diagrama logico transformado
	 */
	private DiagramaControl eliminarEntidadesGenerales(DiagramaControl dLogico,Diagrama dDER, Proyecto proyecto) {
		
		
		//obtengo todas las jerarquias del diagrama 
		//@param false: dado que un componente diagrama no tiene padre 
		Set<Jerarquia> jerarquiasDiagrama = dDER.getJerarquias(false);
		
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
					proyecto.agregar(relacion);
					
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
	
private DiagramaControl transformarAtributosTipo1(Diagrama diagramaDER, DiagramaControl dLogico,Proyecto proyecto) {
		
		//obtengo todas las entidades y chequeo si tiene atributos Compuestos
		EntidadControl entidadNew;
				
		Set<Entidad> entidades = diagramaDER.getEntidades(false);
		// Recorro todas las entidades para encontrar sus atributos polivalentes
		for( Entidad entidad : entidades){
			
			entidadNew = new EntidadControl(entidad);
			entidadNew.setLogico(true);
			
			for ( Atributo atributo : entidad.getAtributos()){
				
				Collection<Atributo> atributosComp = atributo.getAtributos();
				
				if ( atributosComp != null && !atributosComp.isEmpty()){
					//Es Compuesto -> Cada atributo compuesto de un componente pasa a ser un un 
					//                atributo simple de la nueva entidad que contiene al compuesto 
					
					EntidadControl entidadComp = new EntidadControl(atributo.getNombre());
					entidadComp.setLogico(true);
					
					for ( Atributo at : atributosComp){
						AtributoControl atributoCopia = new AtributoControl(at);
						atributoCopia.setLogico(true);
						entidadComp.addAtributo(atributoCopia);	
						//proyecto.agregarSoloAlProyecto(atributoCopia);
						//dLogico.agregar(atributoCopia);
					}
					
					RelacionControl relacionCopia = new RelacionControl("RelacionCompuesta"+ atributo.getNombre());
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
					
					//entidadComp.addRelacion(relacionCopia);
					//entidadNew.addRelacion(relacionCopia);
					
					proyecto.agregarSoloAlProyecto(relacionCopia);
					dLogico.agregar(relacionCopia);
					
					//Agrego la nueva entidad creada a partir del tributo compuesto al diagrama
					dLogico.agregar(entidadComp);
					proyecto.agregarSoloAlProyecto(entidadComp);
					
				} else if ( !atributo.getCardinalidadMinima().equals("1") || !atributo.getCardinalidadMaxima().equals("1")){
					///Es polivalente -> transformo a una entidad
					
					EntidadControl entidadComp = new EntidadControl(atributo.getNombre());
					entidadComp.setLogico(true);
					
					RelacionControl relacionCopia = new RelacionControl("RelacionPolivalente"+ atributo.getNombre());
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
					
					
					
					//Agrego la nueva entidad creada a partir del tributo compuesto al diagrama
					dLogico.agregar(entidadComp);
					dLogico.agregar(relacionCopia);
					proyecto.agregarSoloAlProyecto(entidadComp);
					proyecto.agregarSoloAlProyecto(relacionCopia);
					
				} else {
					// no es ni compuesto ni polivalente. Todo Piola.
					 AtributoControl aux = new AtributoControl(atributo);
					 aux.setLogico(true);
					 
					entidadNew.addAtributo(aux);
				}
			}
			
			//Agrego atributos en caso de jerarquia
			entidadNew = agregarAtributosJerarquia(entidadNew, diagramaDER, entidad, proyecto);
			
			dLogico.agregar(entidadNew);
			proyecto.agregarSoloAlProyecto(entidadNew);
		}
		
		
		return dLogico;
	}

private DiagramaControl transformarAtributosTipo3(Diagrama diagramaDER, DiagramaControl dLogico,Proyecto proyecto) {
	
	//**********
	//Variable para guardar todas las entidades que sean hijas en una jerarquia 
	List<Entidad> entidadesHijas = new ArrayList<Entidad>();
	//variable para guardar un mapa con Id de la entidad del DER que mapee con la entidad del logico
	HashMap<String,String> mapaEntidades = new HashMap<String,String>();
	//**************
	
	//obtengo todas las entidades y chequeo si tiene atributos Compuestos
	EntidadControl entidadNew;
			
	Set<Entidad> entidades = diagramaDER.getEntidades(false);
	// Recorro todas las entidades para encontrar sus atributos polivalentes
	for( Entidad entidad : entidades){
		
		entidadNew = new EntidadControl(entidad);
		entidadNew.setLogico(true);
		
		for ( Atributo atributo : entidad.getAtributos()){
			
			Collection<Atributo> atributosComp = atributo.getAtributos();
			
			if ( atributosComp != null && !atributosComp.isEmpty()){
				//Es Compuesto -> Cada atributo compuesto de un componente pasa a ser un un 
				//                atributo simple de la nueva entidad que contiene al compuesto 
				
				EntidadControl entidadComp = new EntidadControl(atributo.getNombre());
				entidadComp.setLogico(true);
				
				for ( Atributo at : atributosComp){
					AtributoControl atributoCopia = new AtributoControl(at);
					atributoCopia.setLogico(true);
					entidadComp.addAtributo(atributoCopia);	
					//proyecto.agregarSoloAlProyecto(atributoCopia);
					//dLogico.agregar(atributoCopia);
				}
				
				RelacionControl relacionCopia = new RelacionControl("RelacionCompuesta"+ atributo.getNombre());
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
				
				//entidadComp.addRelacion(relacionCopia);
				//entidadNew.addRelacion(relacionCopia);
				
				proyecto.agregarSoloAlProyecto(relacionCopia);
				dLogico.agregar(relacionCopia);
				
				//Agrego la nueva entidad creada a partir del tributo compuesto al diagrama
				dLogico.agregar(entidadComp);
				proyecto.agregarSoloAlProyecto(entidadComp);
				
			} else if ( !atributo.getCardinalidadMinima().equals("1") || !atributo.getCardinalidadMaxima().equals("1")){
				///Es polivalente -> transformo a una entidad
				
				EntidadControl entidadComp = new EntidadControl(atributo.getNombre());
				entidadComp.setLogico(true);
				
				RelacionControl relacionCopia = new RelacionControl("RelacionPolivalente"+ atributo.getNombre());
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
				
				//Agrego la nueva entidad creada a partir del tributo compuesto al diagrama
				dLogico.agregar(entidadComp);
				dLogico.agregar(relacionCopia);
				proyecto.agregarSoloAlProyecto(entidadComp);
				proyecto.agregarSoloAlProyecto(relacionCopia);
				
				
			} else {
				// no es ni compuesto ni polivalente. Todo Piola.
				 AtributoControl aux = new AtributoControl(atributo);
				 aux.setLogico(true);
				 
				entidadNew.addAtributo(aux);
			}
		}
		
		//Si es una entidad Hija lo agrego solamente a entidadesHijas dado que no van a ir en el logico
		if ( !esHijo(entidad,proyecto)){
			dLogico.agregar(entidadNew);
			proyecto.agregarSoloAlProyecto(entidadNew);
		}
		else {
			entidadesHijas.add(entidadNew);
		}
		//Agrego al mapa de ids  el id DER mapeando con el idLogico
		mapaEntidades.put(entidad.getId(), entidadNew.getId());
	}
	
	/* Agrego Relaciones */
	for ( Relacion relacion : diagramaDER.getRelaciones(false)){
		
		//Creo una nueva relacion
		RelacionControl relacionCopia = new RelacionControl("RelacionPolivalente"+ relacion.getNombre());
		relacionCopia.setLogico(true);
		
		//recorro todos los participantes de la relacion
		for(EntidadRelacion er : relacion.getParticipantes()){
			
			// Si es hijo, le voy a agregar la relacion al padre
			if(esHijo(er.getEntidad(), proyecto)){
				
				//obtengo el padre
				Entidad pad = null;
				for ( Jerarquia j : proyecto.getJerarquias() ){
					boolean end = false;
					for(Entidad h : j.getDerivadas()){
						if(h.getId().equals(er.getEntidad().getId())){
							pad = j.getGenerica();
							//obtengo el padre logico
							Entidad padreLogico = null;
							for(Entidad ehl : dLogico.getEntidades(false)){
								if ( ehl.getId().equals(mapaEntidades.get(pad.getId()))){
									padreLogico = ehl;
									pad = padreLogico;
									break;
								}
							}
							end = true;
							break;
						}
							
					}
					if(end) 
						break;
				}
				
				//Agrego al padre a la relacion con cardinalidad minoma = 0
				EntidadRelacion er1 = relacionCopia.new EntidadRelacion(relacionCopia);
				er1.setCardinalidadMaxima(er.getCardinalidadMaxima());
				er1.setCardinalidadMinima("0");
				er1.setEntidad(pad);
				er1.setRol(er.getRol());
				
				relacionCopia.addParticipante(er1);
				
			}
			//Si es padre
			else {
				//obtengo entidad logica
				Entidad entLogico = null;
				for(Entidad ehl : dLogico.getEntidades(false)){
					if ( ehl.getId().equals(mapaEntidades.get(er.getEntidad().getId()))){
						entLogico = ehl;
						break;
					}
				}
										
				EntidadRelacion er1 = relacionCopia.new EntidadRelacion(relacionCopia);
				er1.setCardinalidadMaxima(er.getCardinalidadMaxima());
				er1.setCardinalidadMinima(er.getCardinalidadMinima());
				er1.setEntidad(entLogico);
				er1.setRol(er.getRol());
										
				relacionCopia.addParticipante(er1);
			}
		}
		
		proyecto.agregarSoloAlProyecto(relacionCopia);
		dLogico.agregar(relacionCopia);
	}
	
	/* Agrego Atributos de los hijos al padre */
	for( Entidad eHija : entidadesHijas){
		
		//obtengo el padre
		Entidad pad = null;
		for ( Jerarquia j : proyecto.getJerarquias() ){
			boolean endi = false;
			for(Entidad h : j.getDerivadas()){
				if(mapaEntidades.get(h.getId()).equals(eHija.getId())){
					pad = j.getGenerica();//TODO
					
					//obtengo el padre logico
					Entidad padreLogico = null;
					for(Entidad ehl : dLogico.getEntidades(false)){
						if ( ehl.getId().equals(mapaEntidades.get(pad.getId()))){
							padreLogico = ehl;
							pad = padreLogico;
							break;
						}
					}
					
					endi = true;
					break;
				}
					
			}
			if(endi) 
				break;
		}
		
		for(Atributo atributo : eHija.getAtributos()){
			
			boolean agrego = true;
			for(Atributo attPad : pad.getAtributos()){
				if(attPad.getNombre().equals(atributo.getNombre())){
					agrego = false;
					break;
				}
			}
			
			if(!agrego)
				continue;
			
			AtributoControl aux = new AtributoControl(atributo);
			aux.setLogico(true);
			 
			pad.addAtributo(aux);
		}
		
		// cambio todas las relaciones del hijo a la entidad padre
		for(Relacion relacion : eHija.getRelaciones()){
			for(EntidadRelacion er : relacion.getParticipantes()){
				if(er.getEntidad().getId().equals(eHija.getId())){
					er.setEntidad(pad);
				}
			}													
		}
			
		
	}
	
	
	
	return dLogico;
}

private DiagramaControl transformarAtributosTipo1BIS(Diagrama diagramaDER, DiagramaControl dLogico,Proyecto proyecto) {
	
	//**********
	//variable para guardar un mapa con Id de la entidad del DER que mapee con la entidad del logico
	HashMap<String,String> mapaEntidades = new HashMap<String,String>();
	//**************
	
	//obtengo todas las entidades y chequeo si tiene atributos Compuestos
	EntidadControl entidadNew;
			
	Set<Entidad> entidades = diagramaDER.getEntidades(false);
	// Recorro todas las entidades para encontrar sus atributos polivalentes
	for( Entidad entidad : entidades){
		
		entidadNew = new EntidadControl(entidad);
		entidadNew.setLogico(true);
		
		for ( Atributo atributo : entidad.getAtributos()){
			
			Collection<Atributo> atributosComp = atributo.getAtributos();
			
			if ( atributosComp != null && !atributosComp.isEmpty()){
				//Es Compuesto -> Cada atributo compuesto de un componente pasa a ser un un 
				//                atributo simple de la nueva entidad que contiene al compuesto 
				
				EntidadControl entidadComp = new EntidadControl(atributo.getNombre());
				entidadComp.setLogico(true);
				
				for ( Atributo at : atributosComp){
					AtributoControl atributoCopia = new AtributoControl(at);
					atributoCopia.setLogico(true);
					entidadComp.addAtributo(atributoCopia);	
					//proyecto.agregarSoloAlProyecto(atributoCopia);
					//dLogico.agregar(atributoCopia);
				}
				
				RelacionControl relacionCopia = new RelacionControl("RelacionCompuesta"+ atributo.getNombre());
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
				
				//entidadComp.addRelacion(relacionCopia);
				//entidadNew.addRelacion(relacionCopia);
				
				proyecto.agregarSoloAlProyecto(relacionCopia);
				dLogico.agregar(relacionCopia);
				
				//Agrego la nueva entidad creada a partir del tributo compuesto al diagrama
				dLogico.agregar(entidadComp);
				proyecto.agregarSoloAlProyecto(entidadComp);
				
			} else if ( !atributo.getCardinalidadMinima().equals("1") || !atributo.getCardinalidadMaxima().equals("1")){
				///Es polivalente -> transformo a una entidad
				
				EntidadControl entidadComp = new EntidadControl(atributo.getNombre());
				entidadComp.setLogico(true);
				
				RelacionControl relacionCopia = new RelacionControl("RelacionPolivalente"+ atributo.getNombre());
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
				
				//Agrego la nueva entidad creada a partir del tributo compuesto al diagrama
				dLogico.agregar(entidadComp);
				dLogico.agregar(relacionCopia);
				proyecto.agregarSoloAlProyecto(entidadComp);
				proyecto.agregarSoloAlProyecto(relacionCopia);
				
				
			} else {
				// no es ni compuesto ni polivalente. Todo Piola.
				 AtributoControl aux = new AtributoControl(atributo);
				 aux.setLogico(true);
				 
				entidadNew.addAtributo(aux);
			}
		}
		
		//Agrego todas las entidades
		dLogico.agregar(entidadNew);
		proyecto.agregarSoloAlProyecto(entidadNew);
		
		//Agrego al mapa de ids  el id DER mapeando con el idLogico
		mapaEntidades.put(entidad.getId(), entidadNew.getId());
	}
	
	/* Agrego Relaciones */
	for ( Relacion relacion : diagramaDER.getRelaciones(false)){
		
		//Creo una nueva relacion
		RelacionControl relacionCopia = new RelacionControl("RelacionPolivalente"+ relacion.getNombre());
		relacionCopia.setLogico(true);
		
		//recorro todos los participantes de la relacion
		for(EntidadRelacion er : relacion.getParticipantes()){
			
			//obtengo entidad logica
			Entidad entLogico = null;
			for(Entidad ehl : dLogico.getEntidades(false)){
				if (ehl.getId().equals(mapaEntidades.get(er.getEntidad().getId()))){
					entLogico = ehl;
					break;
				}
			}
									
			EntidadRelacion er1 = relacionCopia.new EntidadRelacion(relacionCopia);
			er1.setCardinalidadMaxima(er.getCardinalidadMaxima());
			er1.setCardinalidadMinima(er.getCardinalidadMinima());
			er1.setEntidad(entLogico);
			er1.setRol(er.getRol());
									
			relacionCopia.addParticipante(er1);
			
		}
		
		proyecto.agregarSoloAlProyecto(relacionCopia);
		dLogico.agregar(relacionCopia);
	}
	
	/* Modifico Jerarquias a Relaciones */
	for ( Jerarquia j : proyecto.getJerarquias() ){
		boolean end = false;
		for(Entidad h : j.getDerivadas()){
			
			//obtengo la entidad logica hija
			Entidad entLogico = null;
			for(Entidad ehl : dLogico.getEntidades(false)){
				if ( ehl.getId().equals(mapaEntidades.get(h.getId()))){
					entLogico = ehl;
					break;
				}
			}
			
			//obtengo la entidad logica padre
			Entidad entPadreLogico = null;
			for(Entidad ehl : dLogico.getEntidades(false)){
				if ( ehl.getId().equals(mapaEntidades.get(j.getGenerica().getId()))){
					entPadreLogico = ehl;
					break;
				}
			}
			
			//Creo la relacion entre ambas
			RelacionControl relacionCopia = new RelacionControl(entLogico.getNombre() + " - " + entPadreLogico.getNombre());
			relacionCopia.setLogico(true);
			
			EntidadRelacion er1 = relacionCopia.new EntidadRelacion(relacionCopia);
			er1.setCardinalidadMaxima("1");
			er1.setCardinalidadMinima("1");
			er1.setEntidad(entLogico);
			er1.setRol("");
			
			EntidadRelacion er2 = relacionCopia.new EntidadRelacion(relacionCopia);
			er2.setCardinalidadMaxima("1");
			er2.setCardinalidadMinima("1");
			er2.setEntidad(entPadreLogico);
			er2.setRol("");
			
			relacionCopia.addParticipante(er1);
			relacionCopia.addParticipante(er2);
			
			proyecto.agregarSoloAlProyecto(relacionCopia);
			dLogico.agregar(relacionCopia);
			
				
		}
		
	}
	
	
	return dLogico;
}
}
