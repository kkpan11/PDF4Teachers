package fr.clementgre.pdf4teachers.document.render.undoEngine;

import fr.clementgre.pdf4teachers.document.editions.elements.Element;
import fr.clementgre.pdf4teachers.document.editions.elements.GradeElement;
import fr.clementgre.pdf4teachers.interfaces.windows.MainWindow;
import fr.clementgre.pdf4teachers.panel.sidebar.grades.GradeTreeItem;

public class CreateDeleteUndoAction extends UndoAction{
    
    // No weak references, otherwise, deleted elements would be loss
    private Element element;
    private boolean deleted;
    
    public CreateDeleteUndoAction(Element element, boolean deleted, UType undoType){
        super(undoType);
        this.element = element;
        this.deleted = deleted;
    }
    
    @Override
    public boolean undoAndInvert(){
        if(element != null){
            if(deleted){
                System.out.println("Undo: restore " + element.getClass().getSimpleName());
                // Old element is in a deleted state so we can't reuse it.
                element = element.clone();
                restoreElement(element);
            }else{
                System.out.println("Undo: delete " + element.getClass().getSimpleName());
                element.delete(true, UType.NO_UNDO);
            }
            
            // invert
            deleted = !deleted;
            
            return true;
        }
        return false;
    }
    
    private static void restoreElement(Element element){
        int page = element.getPageNumber();
        if(MainWindow.mainScreen.document.getPagesNumber() <= element.getPageNumber()){
            page = MainWindow.mainScreen.document.getPagesNumber() - 1;
            element.setPage(page);
        }
        
        MainWindow.mainScreen.document.getPage(page).addElement(element, true, UType.NO_UNDO);
        
        // if it is a GradeElement, we need to re-index the list
        if(element instanceof GradeElement){
            
            if(((GradeElement) element).getGradeTreeItem().getParent() instanceof GradeTreeItem item){
                item.reIndexChildren();
                item.makeSum(false);
            }
        }
    
        element.select();
        
    }
    
}
