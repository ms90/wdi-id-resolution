/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uma.wdi.ir;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ANode
{
    /*
     * Class for loading, storing and accessing the attributes of an xml node. Allows value lists.
     * @author Jakob Huber, Timo Sztyler
     */

    String id;
    Map<String, Set<String>> attributes = new HashMap<>();

    public ANode(String id)
    {
        this.id = id;
    }

    public void addAttribute(String attribute, String value)
    {
        if(attributes.containsKey(attribute)) 
        {
            attributes.get(attribute).add(value);
        } 
        else 
        {
            Set<String> valueSet = new HashSet<>();
            valueSet.add(value);
            attributes.put(attribute, valueSet);
        }
    }


    public Set<String> getAttribute(String attribute)
    {
        if(attributes.containsKey(attribute)) 
        { 
        	return (attributes.get(attribute)); 
        }

        return new HashSet<>();
    }


    public void printAttributeNode()
    {
        System.out.println("> " + id);
        for(String key : attributes.keySet()) 
        {
            System.out.println("\t" + key);
            for(String value : attributes.get(key)) 
            {
                System.out.println("\t\t" + value);
            }
        }
    }

    public String getID()
    {
        return id;
    }


    public boolean hasAttribute(String key)
    {
        return attributes.containsKey(key);
    }
}
