package mereditor.xml;

import org.w3c.dom.Element;

public interface XmlLogicizable {
	public Element toXml(ModeloLogicoParserXml parser) throws Exception;

	public void fromXml(Element elemento, ModeloLogicoParserXml parser) throws Exception;
}
