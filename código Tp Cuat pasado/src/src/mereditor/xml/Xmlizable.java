package mereditor.xml;

import org.w3c.dom.Element;

public interface Xmlizable {
	
	public Element toXml(ModeloParserXml parser) throws Exception;
	
	public Element toXml(ModeloLogicoParserXml parser) throws Exception;

	public void fromXml(Element elemento, ModeloParserXml parser) throws Exception;
	
	public void fromXml(Element elemento, ModeloLogicoParserXml parser) throws Exception;
	
}
