/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.htw.messages;

/**
 *
 * @author stefan.riedel
 */
public class GetMessageOjects {
    
    private String url;
    private String methode;
    private String url_params;
    private String data;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethode() {
        return methode;
    }

    public void setMethode(String methode) {
        this.methode = methode;
    }

    public String getUrl_params() {
        return url_params;
    }

    public void setUrl_params(String url_params) {
        this.url_params = url_params;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    
    
    
}
