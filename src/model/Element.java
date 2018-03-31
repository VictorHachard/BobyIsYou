package model;

import java.util.ArrayList;

/**
 *
 * @author Glaskani
 */
public class Element {
    
    protected final TypeElement typeElement;
    protected ArrayList<Property> ltr; //liste de regle
    protected Directions direction;
    
    /**
     * 
     * @param typeElement TypeElement
     */
    public Element(TypeElement typeElement) {
        this.typeElement = typeElement;
        this.ltr = new ArrayList<>();
        this.direction = Directions.RIGHT;
    }
    
    /**
     * 
     * @param typeElement TypeElement
     * @param tr Property
     */
    Element(TypeElement typeElement,Property tr) {
        this.typeElement = typeElement;
        this.ltr = new ArrayList<>();
        this.direction = Directions.RIGHT;
        ltr.add(tr);
    }
    
    /**
     * 
     * @param typeElement
     * @param tr
     * @param direction 
     */
    Element(TypeElement typeElement,Property tr,Directions direction) {
        this.typeElement = typeElement;
        this.ltr = new ArrayList<>();
        this.direction = direction;
        ltr.add(tr);
    }
    
    /**
     * 
     * @param typeElement TypeElement
     * @param direction Directions
     */
    protected Element(TypeElement typeElement,Directions direction) {
        this.typeElement = typeElement;
        this.ltr = new ArrayList<>();
        this.direction = direction;
    }
    
    /**
     * 
     * @param e 
     */
    Element(Element e) {
        this.ltr = new ArrayList<>(e.ltr);
        this.typeElement = e.typeElement;
        this.direction = e.direction;
    }
    
    public boolean equals(Object obj){
        if (obj instanceof Element) {
            Element e = (Element) obj;
            return (e.getTypeElements() == this.getTypeElements() && e.getDirections() == this.getDirections());
        } else {
            return false;
        }
    }
    
    //Getters
    
    /**
     * Revois le type de l'element.
     * @return TypeElement
     */
    public TypeElement getTypeElements() {
        return this.typeElement;
    }
    
    /**
     * Revois le type de l'element.
     * @return TypeElement
     */
    public TypeElement getText() {
        return this.typeElement.getText();
    }
    
    /**
     * Revois le type de l'element.
     * @return TypeElement
     */
    public TypeTypeElement getTypeTypeElements() {
        return this.typeElement.getType();
    }
    
    /**
     * Revois une liste des régles de l'element.
     * @return ArrayListProperty
     */
    public ArrayList<Property> getTypeRule() {
        return new ArrayList<>(this.ltr);
    }
    
    /**
     * Revois la direction de l'element.
     * @return Direction
     */
    Directions getDirections(){
        return this.direction;
    }
    
    /**
     * Revois la direction de l'element.
     */
    void setDirections(Directions dir){
        this.direction = dir;
    }
    
    /**
     * Supprime la régle tr de la liste des régles de l'element.
     * @param tr Property
     */
    void deleteRule(Property tr){
        ltr.remove(tr);
    }
    
    /**
     * Ajoute la régle tr de la liste des régles de l'element.
     * @param tr Property
     */
    void addRule(Property tr){
        for (Property p:ltr)
            if (p==tr)
                return;
        ltr.add(tr);
    }
    
    /**
     * revois true si la régle est bien dans l'element.
     * @param listRule Property
     * @return true ou false.
     */
    boolean isRule(Property listRule) {
        return ltr.contains(listRule);
    }
    
}
