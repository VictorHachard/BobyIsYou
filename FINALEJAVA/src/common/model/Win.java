package common.model;

import common.exeptions.WinException;
import java.util.List;

/**
 *
 * @author Windows
 */
public class Win extends Rule {

    private List<List<Cell>> listGrid;
    
    public Win(Board board) {
        this.listGrid=board.getListGrid();
    }
    
    
    @Override
    public boolean work(Position pos,Directions dir,TypeElement te) throws WinException {
        if (listGrid.get(pos.y+dir.getDirVer()).get(pos.x+dir.getDirHori()).find(Property.WIN)) {
            Levels.instance().nextLevel();
            throw new WinException();
        }
        return true;
    }

    @Override
    /**
     * Revois la Property de la regle.
     */
    Property getProperty() {
        return Property.WIN;
    }
}