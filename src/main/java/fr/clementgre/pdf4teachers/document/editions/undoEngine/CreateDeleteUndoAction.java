package fr.clementgre.pdf4teachers.document.editions.undoEngine;

import fr.clementgre.pdf4teachers.document.editions.elements.Element;
import fr.clementgre.pdf4teachers.document.editions.elements.GradeElement;
import fr.clementgre.pdf4teachers.interfaces.windows.MainWindow;
import fr.clementgre.pdf4teachers.interfaces.windows.language.TR;
import fr.clementgre.pdf4teachers.panel.sidebar.grades.GradeTreeItem;

public class CreateDeleteUndoAction extends UndoAction{
    
    // No weak references, otherwise, deleted elements would be loss
    private final Element element;
    private boolean deleted;
    private final boolean originallyDeteted;
    
    public CreateDeleteUndoAction(Element element, boolean deleted, UType undoType){
        super(undoType);
        this.element = element;
        this.deleted = deleted;
        this.originallyDeteted = deleted;
    }
    
    @Override
    public boolean undoAndInvert(){
        if(element != null){
            if(deleted){
                restoreElement(element);
            }else{
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
    
        // Do not add the element if it already has a parent.
        if(MainWindow.mainScreen.document.getPage(page).getElements().contains(element) || element.getParent() != null) return;
        
        
        MainWindow.mainScreen.document.getPage(page).addElement(element, true, UType.NO_UNDO);
        element.restoredToDocument();
        
        // if it is a GradeElement, we need to re-index the list
        if(element instanceof GradeElement){
            
            if(((GradeElement) element).getGradeTreeItem().getParent() instanceof GradeTreeItem item){
                item.reIndexChildren();
                item.makeSum(false);
            }
        }
        element.select();
        
    }
    
    public String toString(){
        if(originallyDeteted){
            return TR.tr("actions.delete") + " " + element.getElementName(false);
        }else{
            return TR.tr("actions.create") + " " + element.getElementName(false);
        }
        
    }
    
}
