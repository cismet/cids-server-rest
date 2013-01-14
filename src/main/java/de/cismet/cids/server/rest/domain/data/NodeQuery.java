/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.server.rest.domain.data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author thorsten
 */
@XmlRootElement
public class NodeQuery {
    String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    
         
}
