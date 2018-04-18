package common.model;

import java.util.List;

/**
 *
 * @author Glaskani
 */
public class Sink extends Rule {
    
    private List<List<Placement>> listGrid;
    
    public Sink(Board board) {
        this.listGrid=board.getListGrid();
    }
    
    @Override
    public boolean work(Position pos,Directions dir,TypeElement te) {
        if (listGrid.get(pos.y+dir.getDirVer()).get(pos.x+dir.getDirHori()).find(Property.SINK)) {
            listGrid.get(pos.y).get(pos.x).removeElement(te);
            return false;
        }
        return true;
    }
    
    @Override
    boolean workPush(Position pos,Directions dir,TypeElement te) {
        if (listGrid.get(pos.y+dir.getDirVer()).get(pos.x+dir.getDirHori()).find(Property.SINK)
                && (te.getType()==Type.BLOCK)) {
                listGrid.get(pos.y).get(pos.x).removeElement(te);
            for (Element e:listGrid.get(pos.y+dir.getDirVer()).get(pos.x+dir.getDirHori()).getZ())
            if (e.isRule(Property.SINK))
                listGrid.get(pos.y+dir.getDirVer()).get(pos.x+dir.getDirHori()).removeElement(e.getTypeElement());
            listGrid.get(pos.y+dir.getDirVer()).get(pos.x+dir.getDirHori()).removeElement(te);
            return false;
        }
        return true;
    }

    @Override
    /**
     * Revois la Property de la regle.
     */
    Property getProperty() {
        return Property.SINK;
    }
}

