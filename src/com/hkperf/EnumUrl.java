package com.hkperf;
// Generated Aug 2, 2013 12:11:08 PM by Hibernate Tools 3.2.4.CR1


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * EnumUrl generated by hbm2java
 */
@Entity
@Table(name="enum_url")
public class EnumUrl  implements java.io.Serializable {


 
    @Id
    
    @Column(name="id", unique=true, nullable=false)
    private int id;
 
    
    @Column(name="url", length=45)
    private String url;
   
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }




}


