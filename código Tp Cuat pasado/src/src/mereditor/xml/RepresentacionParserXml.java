package mereditor.xml;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mereditor.control.Control;
import mereditor.interfaz.swt.figuras.Figura;
import mereditor.modelo.Diagrama;
import mereditor.modelo.Proyecto;
import mereditor.modelo.base.Componente;
import mereditor.representacion.PList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Genera y parsea el xml de representación del proyecto.
 * 
 * @author jfacorro
 * 
 */
class RepresentacionParserXml extends ParserXml {

	/**
	 * Constructor utilizado para leer el xml del path especificado
	 * 
	 * @param proyecto
	 *            Instancia vacía del proyecto.
	 * @param path
	 *            Ubicación del archivo XML de representación.
	 * @throws Exception
	 */
	public RepresentacionParserXml(Proyecto proyecto, String path) throws Exception {
		super();
		File source = new File(path);
		this.root = docBuilder.parse(source).getDocumentElement();
		this.proyecto = proyecto;
		if (proyecto.getComponentes().size() > 1)
			throw new Exception("El proyecto debe contener sólo el diagrama raíz.");
	}

	/**
	 * Constructor a utilizar cuando se quiere generar el XML de representación
	 * en base el objeto proyecto.
	 * 
	 * @param proyecto
	 * @throws Exception
	 */
	public RepresentacionParserXml(Proyecto proyecto) throws Exception {
		this.proyecto = proyecto;
	}

	/**
	 * Recorre la coleccion de componentes del proyecto y busca sus
	 * representaciones para cada diagrama en el que estén presentes.
	 */
	public void parsearRepresentacion() {
		// Para cada componente del proyecto
		for (Componente componente : this.proyecto.getComponentes()) {
			// Buscar las representaciones en cada diagrama
			Map<String, PList> representaciones = this.obtenerRepresentaciones(componente.getId());
			// Asignarselas a las figura correspondiente de cada diagrama
			Control<?> control = (Control<?>) componente;
			for (String idDiagrama : representaciones.keySet()){
				control.getFigura(idDiagrama).setRepresentacion(representaciones.get(idDiagrama));
                        }        
		}
	}

	/**
	 * Devuelve un mapa con las representaciones del componente por cada
	 * diagrama en el que se encuentra.
	 * 
	 * @param id
	 *            Id del componente
	 * @return Mapa con los ids de los diagramas como clave y las
	 *         representaciones como valor.
	 */
	public Map<String, PList> obtenerRepresentaciones(String id) {
		HashMap<String, PList> representaciones = new HashMap<>();

		// Buscar todas las representaciones para el id
		String query = String.format(Constants.REPRESENTACION_ID_QUERY, id);
		List<Element> representacionesXml = XmlHelper.query(this.root, query);

		for (Element representacionXml : representacionesXml) {
			Element diagramaXml = XmlHelper.querySingle(representacionXml, Constants.DIAGRAMA_PADRE_QUERY);

			String idDiagrama = this.obtenerId(diagramaXml);
			representaciones.put(idDiagrama, this.obtenerRepresentacion(representacionXml));
		}

		return representaciones;
	}

	/**
	 * Parsea un elemento de representación básico con posición y dimensión.
	 * 
	 * @param elemento
	 * @return
	 */
	protected PList obtenerRepresentacion(Element elemento) {
		PList representacion = new PList();
		for (Element elementoHijo : XmlHelper.query(elemento, "./*")) {
			// Si es una lista de representaciones
			PList hijo = this.obtenerRepresentacion(elementoHijo);
			representacion.set(elementoHijo.getNodeName(), hijo);
		}
		for (String nombre : XmlHelper.attributeNames(elemento)) {
			representacion.set(nombre, elemento.getAttribute(nombre));
		}
		return representacion;
	}

	/**
	 * Generar el XML de rerpesentación.
	 * @return
	 */
	public Document generarXml() {
		Document doc = this.docBuilder.newDocument();
		this.root = doc.createElement(Constants.PROYECTO_TAG);
		doc.appendChild(this.root);

		if(!this.proyecto.getDiagramaRaiz().esLogico())
			this.generarDiagramaXml(this.root, this.proyecto.getDiagramaRaiz());

		return doc;
	}
	
	public Document generarXmlLogico() {
		Document doc = this.docBuilder.newDocument();
		this.root = doc.createElement(Constants.PROYECTO_TAG);
		doc.appendChild(this.root);

		if(!this.proyecto.getDiagramaRaiz().esLogico()) //El diagrama raiz nunca va a ser logico
			this.generarDiagramaXmlLogico(this.root, this.proyecto.getDiagramaRaiz());

		return doc;
	}
	
	protected void generarDiagramaXmlLogico(Element elemento, Diagrama diagrama) {
		Element diagramaElem = this.agregarElemento(elemento, Constants.DIAGRAMA_TAG);
		
		this.agregarAtributo(diagramaElem, Constants.ID_ATTR, diagrama.getId());

		this.generarDiagramaXml(diagramaElem, diagrama.getId(), diagrama.getComponentes());

		// Recorrer todos los diagramas hijos del principal y nietos tambien
		for (Diagrama diagramaHijo : diagrama.getDiagramas()) {
                    /*verifico hijo del principal*/
			if(diagramaHijo.esLogico()){
				this.generarDiagramaXml(elemento, diagramaHijo);
			}
                    /*Verifico hijos del hijo actiu primer nivel*/    
                    for (Diagrama diagramaHijodelHijo : diagramaHijo.getDiagramas()) {    
                        if(diagramaHijodelHijo.esLogico()){
				this.generarDiagramaXml(elemento, diagramaHijodelHijo);
			}
                    /*Verifico hijos del hijo del hijo*/    
                    for (Diagrama diagramaHijodelHijodelHijo : diagramaHijodelHijo.getDiagramas()) {    
                        if(diagramaHijodelHijodelHijo.esLogico()){
				this.generarDiagramaXml(elemento, diagramaHijodelHijodelHijo);
			}
                        
                    }        
                        
                        
                        
                        
                    }    
                    
                   
                        
		}
	}

	/**
	 * Genera el element de XML de representación de un diagrama y sus hijos.
	 * Agrega el elemento generado al nodo raíz.
	 * 
	 * @param elemento
	 * @param diagrama
	 */
	protected void generarDiagramaXml(Element elemento, Diagrama diagrama) {
		Element diagramaElem = this.agregarElemento(elemento, Constants.DIAGRAMA_TAG);
		
		this.agregarAtributo(diagramaElem, Constants.ID_ATTR, diagrama.getId());

		this.generarDiagramaXml(diagramaElem, diagrama.getId(), diagrama.getComponentes());

		// Recorrer todos los diagramas hijos del principal
		for (Diagrama diagramaHijo : diagrama.getDiagramas()) {
			if(!diagramaHijo.esLogico()){
				this.generarDiagramaXml(elemento, diagramaHijo);
			}
		}
	}
	
	protected void generarDiagramaXml(Element diagramaElem, String idDiagrama, Collection<Componente> componentes) {
		// Recorrer todos los componentes y sus hijos.
		if(componentes != null) {
			for (Componente componente : componentes) {
				Control<?> control = (Control<?>) componente;
				Figura<?> figura = control.getFigura(idDiagrama);
	
				if (figura != null && figura.getRepresentacion() != null) {
					PList plist = figura.getRepresentacion();
					Element reprElement = this.agregarElemento(diagramaElem, Constants.REPRESENTACION_TAG);
					this.agregarAtributo(reprElement, Constants.ID_ATTR, componente.getId());
					this.agregarRepresentacion(reprElement, plist);
				}
				
				this.generarDiagramaXml(diagramaElem, idDiagrama, componente.getComponentes());
			}
		}
	}

	/**
	 * Genera un elemento Representación XML desde una PList.
	 * 
	 * @param elemento
	 * @param repr
	 */
	private void agregarRepresentacion(Element elemento, PList repr) {
		for (String nombre : repr.getNames()) {
			Object valor = repr.get(nombre);

			if (valor instanceof PList) {
				// Si el valor es una PList entonces armar un elemento
				Element elemHijo = this.agregarElemento(elemento, nombre);
				this.agregarRepresentacion(elemHijo, (PList) valor);
			} else {
				// Si no agregar como par atributo/valor
				this.agregarAtributo(elemento, nombre, valor.toString());
			}
		}
	}
}
