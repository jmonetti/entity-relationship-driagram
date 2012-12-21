package mereditor.interfaz.swt.builders;

import java.util.Iterator;
import mereditor.control.Control;
import mereditor.interfaz.swt.Principal;
import mereditor.modelo.Diagrama;
import mereditor.modelo.Proyecto;
import mereditor.modelo.base.Componente;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TreeManager {
	public static Tree tree;
	private static CTabItem tab;
	private static CTabFolder folder;
	private static TreeItem diagramaActivo;
      

	public static Tree build(Composite composite) {
		new TreeManager(composite);
		return TreeManager.tree;
	}

	private TreeManager(Composite composite) {
		folder = new CTabFolder(composite, SWT.CENTER);
		tab = new CTabItem(folder, SWT.BOTTOM);
		tree = new Tree(folder, SWT.NO_SCROLL);
		new MenuArbolBuilder(tree);

		this.init();
	}

	private void init() {
		tree.setBackground(new Color(null, 240, 240, 240));

		folder.setSimple(false);
		folder.setSelection(tab);
		folder.setMinimizeVisible(true);
		folder.addCTabFolder2Listener(this.minimizar);

		tab.setControl(tree);
		tab.setShowClose(false);
	}
        
        
           /**
	 * Elimina item de un dia logico 
	 * 
	 * @return
	 */
	public  static  void  eliminarItem() {
      for(int i=0;i<tree.getItemCount();i++) {
             TreeManager.tree.getItem(i).getData().toString();
            }
            /*
	
            	Componente componente = getComponente();
			TreeItem current = getItem();
			TreeItem parent = current.getParentItem();
			if (parent != null) {
				Diagrama diagrama = (Diagrama) parent.getData();
				diagrama.eliminar(componente);

				Principal.getInstance().actualizarVista();
				current.dispose();
			}
		*/
			
	}

	/**
	 * Agregado del diagrama principal y sus hijos.
	 * 
	 * @param diagrama
	 * @param tree
	 */
	private static void agregar(Diagrama diagrama, Tree tree) {
		tree.removeAll();
		tree.setData(diagrama);
		// Crear el item raiz.
		TreeItem item = new TreeItem(tree, SWT.NULL);
		item.setText(diagrama.getNombre());
		item.setData(diagrama);
		// Cargar el icono del item.
		String nombreIcono = ((Control<?>) diagrama).getNombreIcono();
		item.setImage(Principal.getIcono(nombreIcono));

		diagramaActivo = item;

		for (Diagrama diagramaHijo : diagrama.getDiagramas()){
                       
			agregar(diagramaHijo, item);
                            
                        if(Principal.getInstance().tienelogico(diagramaHijo)!=null){
                         
                        System.out.println("Se agrego un logico a: "+diagramaHijo.getNombre());
                        }
                        
                }

		for (Componente componente : diagrama.getEntidades(false))
			agregar(componente, item);

		for (Componente componente : diagrama.getRelaciones(false))
			agregar(componente, item);

		for (Componente componente : diagrama.getJerarquias(false))
			agregar(componente, item);

		item.setExpanded(true);
	}
        
        
          /**
	 * Devuelve el item que se encuentra con el nombre del diagrama
	 * 
	 * @return
	 */
	public static  void getItemEsp(String nombre_diagrama) {
            int cantidad;
        cantidad = tree.getItemCount();
        for(int i=0; i<cantidad;i++){
        System.out.println("Lista itemsooo: "+tree.getItem(i).getText());
        }
		

		
	}
        
        public static  void getItemDia(String nombre_diagrama) {
            int cantidad;
        cantidad = tree.getItemCount();
      
 /*   for(int i=0; i<cantidad;i++){
      System.out.println("Lista itemsooo: "+tree.getItem(0).getItem(0).dispose();
   //     }
		*/

		
	}

        
        
   
	/**
	 * Agregado de diagrama no principal y sus hijos.
	 * 
	 * @param diagrama
	 * @param padre
	 */
	private static void agregadoLogico(Diagrama diagrama, TreeItem padre) {
		TreeItem item = new TreeItem(padre, SWT.NULL);
		item.setText(diagrama.getNombre());
		item.setData(diagrama);
		String nombreIcono = ((Control<?>) diagrama).getNombreIcono();
		item.setImage(Principal.getIcono(nombreIcono));
        
		for (Diagrama diagramaHijo : diagrama.getDiagramas()){
			agregar(diagramaHijo, item);
                  if(Principal.getInstance().tienelogico(diagramaHijo)!=null){
                         
                        System.out.println("Se agrego un logico a: "+diagramaHijo.getNombre());
                        }
                            
                    
                }        

		for (Componente componente : diagrama.getEntidades(false))
			agregar(componente, item);

		for (Componente componente : diagrama.getRelaciones(false))
			agregar(componente, item);

		for (Componente componente : diagrama.getJerarquias(false))
			agregar(componente, item);
	}
     
        

	/**
	 * Agregado de diagrama no principal y sus hijos.
	 * 
	 * @param diagrama
	 * @param padre
	 */
	private static void agregar(Diagrama diagrama, TreeItem padre) {
		TreeItem item = new TreeItem(padre, SWT.NULL);
		item.setText(diagrama.getNombre());
		item.setData(diagrama);
		String nombreIcono = ((Control<?>) diagrama).getNombreIcono();
		item.setImage(Principal.getIcono(nombreIcono));
           diagramaActivo = item;
		for (Diagrama diagramaHijo : diagrama.getDiagramas()){
			agregar(diagramaHijo, item);
                  if(Principal.getInstance().tienelogico(diagramaHijo)!=null){
                         
                        System.out.println("Se agrego un logico a: "+diagramaHijo.getNombre());
                        }
                            
                    
                }        

		for (Componente componente : diagrama.getEntidades(false))
			agregar(componente, item);

		for (Componente componente : diagrama.getRelaciones(false))
			agregar(componente, item);

		for (Componente componente : diagrama.getJerarquias(false))
			agregar(componente, item);
	}

	/**
	 * Agregado de componente.
	 * 
	 * @param diagrama
	 * @param item
	 */
	private static void agregar(Componente componente, TreeItem padre) {
		TreeItem item = new TreeItem(padre, SWT.NULL);
		item.setText(componente.toString());
		item.setData(componente);

		String icono = ((Control<?>) componente).getNombreIcono();
		item.setImage(Principal.getIcono(icono));
	}

	/**
	 * Carga todo el arbol de componentes entero.
	 * @param proyecto
	 */
	public static void cargar(Proyecto proyecto) {
		TreeManager.agregar(proyecto.getDiagramaRaiz(), TreeManager.tree);
		tab.setText(proyecto.getNombre());
		folder.setEnabled(true);
	}

	public static void agregarADiagramaActual(Diagrama nuevoDiagrama) {
		agregar(nuevoDiagrama, diagramaActivo);
	}
        
        public static void agregarADiagramaActualLogico(Diagrama nuevoDiagrama) {
		agregadoLogico(nuevoDiagrama, diagramaActivo);
	}

	public static void agregarADiagramaActual(Componente nuevoComponente) {
		agregar(nuevoComponente, diagramaActivo);
	}
	
	public static Diagrama getDiagramaActual() {
		return (Diagrama) diagramaActivo.getData();
	}

	public static void setDiagramaActivo(TreeItem diagramaActivo) {
		TreeManager.diagramaActivo = diagramaActivo;
	}
        
        
	
	private CTabFolder2Listener minimizar = new CTabFolder2Adapter() {
		public void minimize(CTabFolderEvent event) {
			Principal.getInstance().mostrarArbol(false);
		};
	};
}
