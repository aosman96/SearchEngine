/*

 * To change this license header, choose License Headers in Project Properties.

 * To change this template file, choose Tools | Templates

 * and open the template in the editor.

 */

package searchengine;

public class Domain {

    private String domainHash ;

    private String domainUrl ;



    Domain() {

       

    }

   



    public String getDomainHash() {

        return domainHash;

    }



    public String getDomainUrl() {

        return domainUrl;

    }



    public Domain(String domainHash, String domainUrl) {

        this.domainHash = domainHash;

        this.domainUrl = domainUrl;

    }

    

    

    



}