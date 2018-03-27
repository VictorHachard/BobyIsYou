package model;

import exeptions.TypeElementNotFoundException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * //TODO ergerister le dernier deplacment pour la save direction
 * @author Glaskani
 */
public class Board extends Subject {
    
    private List<Element> listAllElement;
    private TypeElement player;
    private List<Position> is;
    private List<List<Placement>> listGrid;
    private int x;
    private int y;
    private Placement unplayable = new Placement(new Unplayable());
    private Element empty = new Empty();
    private MusicHashMap music;
    private List<ElementRule> listRule;
    private HashMap<Property, Rule> rule;
    
    private static Board INSTANCE = null;
    
    /**
     * 
     * @param map
     * @return
     * @throws TypeElementNotFoundException
     * @throws IOException 
     */
    public static Board getInstance(Maps map) throws TypeElementNotFoundException, IOException {           
        if (INSTANCE == null)
            INSTANCE = new Board(map);
        return INSTANCE;
    }
    
    /**
     * 
     * @return 
     */
    public static Board getInstance() {           
        return INSTANCE;
    }
    
    /**
     * 
     */
    public static void ReloadInstance() {           
        INSTANCE = null;
    }
    
    private Tp tp;
    private Ice ice;
    private Kill kill;
    private Sink sink;
    private Move move;
    private Melt melt;
    private Win win;
    
    /**
     * 
     * @param map
     * @throws TypeElementNotFoundException 
     */
    private Board(Maps map) throws TypeElementNotFoundException, IOException {
        this.listGrid = new ArrayList<>();
        this.listRule = new ArrayList<>();
        this.listAllElement = map.getListAllElement();  
        music = new MusicHashMap();
        
        this.x = map.getSizeX();
        this.y = map.getSizeY();
        
        generateGrid(x-2,y-2);

        for(int i=1;i<y-1;i++){
            for(int j=1;j<x-1;j++){
                List<Element> te =  map.getListElement(j,i);
                for(int k=0;k<te.size();k++){
                    //ne re load pas les EMPTY
                if (!(te.get(k).getTypeElements()==TypeElement.EMPTY)) {
                    addPlacement(j,i,te.get(te.size()-k));    
                    //Ajoute les pushs sur les texte et les texte regles.
                    if (te.get(k).typeElement==TypeElement.MONSTER)
                        listGrid.get(i).get(j).getListeContenu().get(k).addRule(Property.MOVE);
                    if (te.get(k).getTypeTypeElements()==TypeTypeElement.IS ||
                            te.get(k).getTypeTypeElements()==TypeTypeElement.TEXT ||
                            te.get(k).getTypeTypeElements()==TypeTypeElement.RULE)
                    listGrid.get(i).get(j).getListeContenu().get(k).addRule(Property.PUSH);                        
                }
                }
            }
        }       
               
        tp = new Tp(this);
        ice = new Ice(this);
        kill = new Kill(this);
        sink = new Sink(this);
        move = new Move(this);
        melt = new Melt(this);
        win = new Win(this);
        
        getIs();
        for (Position p:is)
            rule(p,Directions.NONE,TypeTypeElement.IS);
    }   
    
    private void deleteAllRule() {
        for (Element e:this.listAllElement)
            if (!(e.getTypeTypeElements()==TypeTypeElement.IS||e.getTypeTypeElements()==TypeTypeElement.RULE||e.getTypeTypeElements()==TypeTypeElement.TEXT))
                if (!(e.getTypeRule().isEmpty()))
                    for (int i=0;i<e.getTypeRule().size();i++)
                        e.deleteRule(e.getTypeRule().get(i));       
    }
    
    /**
     * Ajout a obsMap les observer et les Positions
     */
    private void getIs() {
        List<Position> lp = getPositionOf(TypeElement.IS);
        is = new ArrayList<>();
        for (Position p:lp) {
            this.is.add(p);
        }
    }
    
    private void rule(Position pos, Directions dir, TypeTypeElement ty) throws TypeElementNotFoundException {
        /*if (ty==TypeTypeElement.IS) {
            //removeObs(pos);
            pos = new Position(pos.x+dir.getDirHori(),pos.y+dir.getDirVer());
            //Rule r = new Rule();
            //addObs(pos,r);
        }*/
        if (listGrid.get(pos.y).get(pos.x-1).findTypeType(TypeTypeElement.TEXT)
                && listGrid.get(pos.y).get(pos.x+1).findTypeType(TypeTypeElement.RULE)) 
            addRule(listGrid.get(pos.y).get(pos.x-1).findTypeElement(TypeTypeElement.TEXT),
                    listGrid.get(pos.y).get(pos.x+1).findTypeElement(TypeTypeElement.RULE));
        
        else if (listGrid.get(pos.y-1).get(pos.x).findTypeType(TypeTypeElement.TEXT)
                && listGrid.get(pos.y+1).get(pos.x).findTypeType(TypeTypeElement.RULE)) {
            addRule(listGrid.get(pos.y-1).get(pos.x).findTypeElement(TypeTypeElement.TEXT),
                    listGrid.get(pos.y+1).get(pos.x).findTypeElement(TypeTypeElement.RULE));
            
        }
        else if (listGrid.get(pos.y-1).get(pos.x).findTypeType(TypeTypeElement.TEXT)
                && listGrid.get(pos.y+1).get(pos.x).findTypeType(TypeTypeElement.TEXT)) {
            changeType(listGrid.get(pos.y-1).get(pos.x).findTypeElement(TypeTypeElement.TEXT),
                    listGrid.get(pos.y+1).get(pos.x).findTypeElement(TypeTypeElement.TEXT));}
                
        else if (listGrid.get(pos.y).get(pos.x-1).findTypeType(TypeTypeElement.TEXT)
                && listGrid.get(pos.y).get(pos.x+1).findTypeType(TypeTypeElement.TEXT)) {
            changeType(listGrid.get(pos.y).get(pos.x-1).findTypeElement(TypeTypeElement.TEXT),
                    listGrid.get(pos.y).get(pos.x+1).findTypeElement(TypeTypeElement.TEXT));}
    }
    
    /**
     * BUGGER
     * @param text
     * @param text2
     * @throws TypeElementNotFoundException 
     */
    private void changeType(TypeElement text,TypeElement text2) throws TypeElementNotFoundException { //e1 a mettre e a enlever
        List<Element> listAllElement2 = new ArrayList(listAllElement);
        List<Element> listDelete = new ArrayList<>();
        for(Element e4:listAllElement)
            listDelete.add(e4);
        for(Element e1:listAllElement) {
            if (e1.getTypeElements()==text2.getText()) {
                for(Element e:listAllElement2) {
                    if (e.getTypeElements()==text.getText()) {
                        for(int i=1;i<this.y-1;i++){
                            for(int j=1;j<this.x-1;j++){
                                for(int k=0;k<listGrid.get(i).get(j).getListeContenu().size();k++){
                                    if (listGrid.get(i).get(j).getListeContenu().get(k).getTypeElements()==e.getTypeElements()) {
                                        addPlacement(j,i,e1);  
                                        listGrid.get(i).get(j).removeElement(e.getTypeElements());  
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for(Element e5:listAllElement2)
            if (listDelete.contains(e5))
                listAllElement.remove(e5);
    }
    
    private void addRule(TypeElement text,TypeElement rule) {
        listRule.add(new ElementRule(text.getText(), rule.getRule()));
        /*if (text.getText()==TypeElement.PLAYER1) {
            System.out.println(text.getText());
            player = text.getText();
        }*/
        for(Element e:listAllElement)
            if (e.getTypeElements()==text.getText())
                e.addRule(rule.getRule());
    }
    
    /**
     * Crée le Board de taille x,y et ajoute les elements injouable et EMPTY.
     * @param x int
     * @param y int
     */
    private void generateGrid(int x,int y) {
        for(int j=0;j<y+2;j++) {
            listGrid.add(new ArrayList<>());
            for(int i=0;i<x+2;i++)
                if(i==0 || j==0 || j==y+1 || i==x+1)
                    listGrid.get(j).add(this.unplayable);
                else
                    listGrid.get(j).add(new Placement (this.empty));
        }
    }
       
    List<ElementRule> getElementRule() {
        return this.listRule;
    }
    
    /**
     * Revois la taille du tableau board en abscisse.
     * @return int
     */
    public int getSizeX() {
        return this.x;
    }
    
    /**
     * Revois la taille du tableau board en ordonnée.
     * @return int
     */
    public int getSizeY() {
        return this.y;
    }
    
    /**
     * Revois la liste contenant Placement.
     * @return ListListPlacement
     */
    public List<List<Placement>> getListGrid() {
        return this.listGrid;
    }
    
    /**
     * Revois la liste contenant Placement.
     * @return ListListPlacement
     */
    public List<Element> getListAllElement() {
        return this.listAllElement;
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param object
     * @throws TypeElementNotFoundException 
     */
    private void addPlacement(int x, int y, Element object) throws TypeElementNotFoundException {
        listGrid.get(y).get(x).addElement(object);
    }
    
    /**
     * Revois une chaine de charactére du Board.
     * @return String
     */
    public String Affichage(){
        StringBuilder  sb = new StringBuilder();
        
        for(List<Placement> lp:this.listGrid){
            for(Placement p:lp) {
                sb.append(p.getListeContenu().get(p.getListeContenu().size()-1).getTypeElements().getLetter());
                sb.append("|");
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    
    /**
     * Revois une chaine de charactére du Board en adresse memoire.
     * @return String
     */
    public String AffichageAdresse(){
        StringBuilder  sb = new StringBuilder();
        
        for(List<Placement> lp:this.listGrid){
            for(Placement p:lp) {
                sb.append(p.getListeContenu().get(p.getListeContenu().size()-1));
                sb.append("|");
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    
    /**
     * 
     * @param te
     * @return 
     */
    List<Position> getPositionOf(TypeElement te){
        List<Position> lp = new ArrayList<>();
        
        for(int i=0;i<this.y;i++)
            for(int j=0;j<this.x;j++)
                if(this.listGrid.get(i).get(j).findElements(te))
                    lp.add(new Position(j,i));
        return lp;
    }
    
    /**
     * 
     * @return 
     */
    private TypeElement getPlayerType(){
        return TypeElement.PLAYER1;
    }
    
    /**
     * 
     * @param pos
     * @param direction
     * @param element 
     */
    void editPlacement(Position pos, Directions direction, TypeElement element) {
        listGrid.get(pos.y+direction.getDirVer()).get(pos.x+direction.getDirHori())
                .addElement(listGrid.get(pos.y).get(pos.x).getElements(element));
        listGrid.get(pos.y).get(pos.x).removeElement(element);
    }
    
    /**
     * 
     * @param direction 
     */
    public void movePlayer(Directions direction) throws TypeElementNotFoundException, IOException{
        
        TypeElement player = getPlayerType();
        List<Position> lp = getPositionOf(player);
        
        for(Position pos:lp)
            if(pos.y+direction.getDirVer() < y && pos.x+direction.getDirHori() < x) {
                
                //regle a la con
                if (this.win.check(pos, direction, player))
                    return;
                if (this.tp.check(pos, direction, player))
                    return;
                if (this.melt.check(pos, direction, player))
                    return;
                if (this.kill.check(pos, direction, player))
                    return;
                if (this.sink.check(pos, direction, player))
                    return;
                if (this.move.check(pos, direction, player))
                    return;
                
                //else if (this.ice.check(pos, direction, player))
                 //   return;
                
                //Depalcement ADD
                else if (listGrid.get(pos.y+direction.getDirVer()).get(pos.x+direction.getDirHori()).canAdd()){ //verifie si il peut add la case suivante
                    //Depalcement ADD
                    editPlacement(pos,direction,player);
                    this.music.play(Music.ADD);
                }
                //Depalcement PUSH
                else if (listGrid.get(pos.y+direction.getDirVer()).get(pos.x+direction.getDirHori()).canPush()) { //verifie si il peut push la case suivante
                    if (push(new Position(pos.x+direction.getDirHori(),pos.y+direction.getDirVer()),direction))
                        editPlacement(pos,direction,player);
                }
            }
        deleteAllRule();
        getIs();
        for (Position p:is)
            rule(p,Directions.NONE,TypeTypeElement.IS);
    }
    
    /**
     * Methode recurcive qui deplace un TypeElement d'un Elements dans le sens
     * de la direction.
     * @param pos Position, de l'element initial
     * @param direction Directions, sens du déplacemnt 
     * @return true ou flase
     */
    boolean push(Position pos,Directions direction) throws TypeElementNotFoundException {
        if(pos.y+direction.getDirVer() < y && pos.x+direction.getDirHori() < x){
            if (listGrid.get(pos.y).get(pos.x).canPush()){
                if(push(new Position(pos.x+direction.getDirHori(),pos.y+direction.getDirVer()),direction)){
                    for(Element e:listGrid.get(pos.y).get(pos.x).getElementsOf(Property.PUSH)){
                        editPlacement(pos,direction,e.getTypeElements());
                            if (this.sink.checkPush(pos, direction, TypeElement.ANNI)) { //ajouter sink a push
                                listGrid.get(pos.y+direction.getDirVer()).get(pos.x+direction.getDirHori()).removeElement(e.getTypeElements());
                                                    return true;
                                    }
                        /*if (e.getTypeTypeElements()==TypeTypeElement.IS) {
                                                        System.out.println("enter IS");
                            notifierObservateurs(pos,direction,TypeTypeElement.IS);

                        }
                        if (e.getTypeTypeElements()==TypeTypeElement.TEXT) {
                                                        System.out.println("enter TEXT");
                            notifierObservateurs(pos,direction,TypeTypeElement.TEXT);
                        }
                        if (e.getTypeTypeElements()==TypeTypeElement.RULE) {
                            System.out.println("enter RULE");
                            notifierObservateurs(pos,direction,TypeTypeElement.RULE);
                        }*/
                    }
                    return true;
                }
            } else if (listGrid.get(pos.y).get(pos.x).canAdd()) {
                return true;
            }
        }
        return false;    
    }
    
    /**
     * Sauvegarde la partie actuel.
     * @throws IOException 
     */
    public void save() throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        save(dateFormat.format(new Date()) +".txt");     
    }
    
    /**
     * 
     * @param fileName
     * @throws IOException 
     */
    public void save(String fileName) throws IOException {   
    try {
        BufferedWriter save = new BufferedWriter(new FileWriter(new File(fileName)));
        //si le fichier n'existe pas, il est crée à la racine du projet.
        //save la taille du Board.
        int x1 = this.x-2;
        int y1 = this.y-2;
        save.write(x1 + " " + y1);
        
        //save chaque element.
        for(int i=1;i<y-1;i++){
            for(int j=1;j<x-1;j++){
                List<Element> te =  listGrid.get(i).get(j).getListeContenu();
                for(int k=0;k<te.size();k++){
                    //ne save pas les EMPTY
                    if (!(te.get(k).getTypeElements()==TypeElement.EMPTY)) {
                        save.newLine();
                        int j1 = j-1;
                        int i1 = i-1;
                        String name = te.get(te.size()-k).getTypeElements().getElements().toLowerCase();
                        int dir = te.get(te.size()-k).getDirections().getDir();
                        if (dir == 0)
                            save.write(name + " " + j1 + " " + i1);
                        else save.write(name + " " + j1 + " " + i1 + " " + dir);
                    }
                }
            }
        }
            
        save.close();
    }
    catch (IOException e) {
    }   
    }

}
