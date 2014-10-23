/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Reads from file and stores XML nodes in a hash map by ID, 
 * Contains getter to navigate the map. */
/* Important assumption: ID uniqueness.
 * */

package uma.wdi.ir;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class NodeMap 
{
	/*
	 * Class for loading, storing and navigating input datasets.
	 *  @author Volha
	 *  @author Jakob Huber, Timo Sztyler; Bernd Opitz, Thorsten Knoeller
	 *  Jacob, Timo, Bernd and Thorsten optimized the code (added the use of ANode) : expressiveness/efficiency trade-off,
	 *   cannot work with complex xml structure, e.g. would concatenate actors/actor/name and actors/actor/surname;
	 *   in case of complex attribute paths the upper one should be specified ("actors" for comparing "actors/actor/name").
	 *  Stores list of values, e.g. list of actors/actor/name for a movie (stored as "actors" attribute).
	 */
	
	// Stores XML (attribute-value extracted from XML) nodes by IDs ("id" tag)
    private Map<String, ANode> map = new HashMap<>();
		
	// Get xml node by ID
	public ANode getNode(String id) 
	{
		return map.get(id);
	}
	
    // Get entry set, to iterate through the map
    public Set<Entry<String, ANode>> getEntrySet()
    {
        return map.entrySet();
    }

    // Get key set, to iterate through the map
    public Set<String> getKeySet()
    {
        return map.keySet();
    }
	
	/* Load data from .xml file
	 * idPath (e.g. "/movies/movie/id") is used as an entity string ID
	 * If called several times wit h different files, reads all in one map 
	 */		
	public boolean loadFromFile(String file, String idPath)
	{
		try 
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder;
		    builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
	
		    XPathFactory xPathFactory = XPathFactory.newInstance();
		    XPath xpath = xPathFactory.newXPath();
		    XPathExpression expr = xpath.compile(idPath);
		    NodeList list = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		    if (list.getLength() == 0)
		    {
		    	System.out.println("ERROR: no ids (" + idPath +") found in the input file " + file);
		    }
	        for (int i = 0; i < list.getLength(); i++)
	        {
	        	// attribute map:
                String id = list.item(i).getTextContent();
                Node parent;
                if(list.item(i).getParentNode() != null) 
                {
                   parent = list.item(i).getParentNode();
                } 
                else 
                {
                	parent = ((Attr) list.item(i)).getOwnerElement();
                }
                // map.put(id, parent); - old map
                NodeList children = parent.getChildNodes();
                ANode attributes = new ANode(id);
                for(int j = 0; j < children.getLength(); j++) 
                {
                	/*
                    // #start T&Y (version 1, no value lists)
                    Node child = children.item(j);
                    if(child instanceof Element) 
                    {
                        String attribute = child.getNodeName();
                        String value = child.getTextContent().trim();
                        if(value.contains("(") && value.contains("")) 
                        {
                            Pattern p = Pattern.compile("(.*) (\\(.*\\))");
                            Matcher m = p.matcher(value);
                            if(m.find()) 
                            {
                                value = m.group(1);
                            }
                        }
                        attributes.addAttribute(attribute, value);
                    }
                    // #end T&Y
                    */
                	
                    // #start B&T (version 2, supports value lists)
                    org.w3c.dom.Node child = children.item(j);
                    if(child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) 
                    {
                        String attribute = child.getNodeName();
                        String value;
                        // Single-valued node
                        if(child.getChildNodes().getLength() == 1) 
                        {
                            value = child.getTextContent().trim();
                            if(!value.isEmpty()) 
                            {
                                attributes.addAttribute(attribute, value);
                            }
                        }
                        
                        // Multi-valued node
                        else 
                        {
                            for(int vn_i = 0; vn_i < child.getChildNodes().getLength(); vn_i++) 
                            {
                                // XXX: vn doesn't have to be the value node, but we do not care about that while matching
                                org.w3c.dom.Node vn = child.getChildNodes().item(vn_i);
                                value = vn.getTextContent().trim();
                                if(!value.isEmpty()) 
                                {
                                    attributes.addAttribute(attribute, value);
                                }
                            }
                        }                        
                    }
                    // #end B&T
                }
                map.put(id, attributes);
                // attributes.printAttributeNode();
	        }
	        return true;
		}
		catch (ParserConfigurationException e)
		{
    	   e.printStackTrace();
    	} 
		catch (SAXException e) 
		{
			System.out.println("ERROR: your xml file " + file + " is not well-formed");
 		} 
		catch (IOException e) 
		{
			System.out.println("ERROR: input file " + file + " not found");
		} 
		catch (XPathExpressionException e) 
		{
    	   e.printStackTrace();
		}         
		return false;
	}
}
