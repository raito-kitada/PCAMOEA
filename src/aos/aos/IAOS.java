/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aos.aos;

import org.moeaframework.core.Algorithm;

/**
 * AOS is the framework using a credit assignment and operator selection strategy 
 * @author nozomihitomi
 */
public interface IAOS extends Algorithm{
    /**
     * Sets the adaptive operator selector's name
     * @param name
     */
    public void setName(String name);
    
    /**
     * Gets the adaptive operator selector's name
     * @return 
     */
    public String getName();

}