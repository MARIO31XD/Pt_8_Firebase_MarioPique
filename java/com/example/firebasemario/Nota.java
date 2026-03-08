package com.example.firebasemario;


// creem clase Nota amb els atributs que tindren les notes que afegirem
public class Nota {
      public String titol, contingut;
      public String id;
      public boolean important;
 public Nota () {} // constructor buit

    public Nota (String titol, String contingut, boolean important, String id) {

        this.titol = titol;
        this.contingut = contingut;
        this.important = important;
        this.id = id;
    }

    public String getTitol() {
        return titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public String getId() {
         return id;
    }
    public void setId(String id) {
         this.id = id;
    }
    public String getContingut() {
        return contingut;
    }

    public void setContingut(String contingut) {
        this.contingut = contingut;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }
}
