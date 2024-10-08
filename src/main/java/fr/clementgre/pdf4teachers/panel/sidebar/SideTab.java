/*
 * Copyright (c) 2020-2024. Clément Grennerat
 * All rights reserved. You must refer to the licence Apache 2.
 */

package fr.clementgre.pdf4teachers.panel.sidebar;

import fr.clementgre.pdf4teachers.Main;
import fr.clementgre.pdf4teachers.interfaces.windows.MainWindow;
import fr.clementgre.pdf4teachers.utils.image.ImageUtils;
import fr.clementgre.pdf4teachers.utils.svg.SVGPathIcons;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;

public class SideTab extends Tab {
    
    private final String name;
    
    public SideTab(String name, String iconPath, int maxDimension, double ratio){
        this.name = name;
        
        setClosable(false);
        setGraphic(SVGPathIcons.generateImage(iconPath, "gray", 0, maxDimension, 0, ratio, ImageUtils.defaultGrayColorAdjust));
        setupDragAndDrop(iconPath);
        
        Platform.runLater(() -> {
            if(getTabPane() == null){
                MainWindow.leftBar.getTabs().add(this);
            }
            
            getContent().setOnDragOver((DragEvent e) -> {
                if(MainWindow.filesTab.isValidDragFile(e, true)){
                    if(getTabPane().getTabs().contains(MainWindow.filesTab)){ // Select files tab
                        SideBar.selectTab(MainWindow.filesTab);
                        e.acceptTransferModes(TransferMode.ANY);
                        e.consume();
                    }
                }
            });
        });
        
    }
    
    public void select(){
        SideBar.selectTab(this);
    }
    
    
    public void setupDragAndDrop(String iconPath){
        
        getGraphic().setOnDragDetected(e -> {
            Dragboard dragboard = getGraphic().startDragAndDrop(TransferMode.MOVE);
            Image image = SVGPathIcons.generateNonSvgImage(iconPath, Color.GRAY, ImageUtils.defaultGrayColorAdjust, .06 * Main.settings.zoom.getValue());
            dragboard.setDragView(image);
            
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.put(Main.INTERNAL_FORMAT, SideBar.TAB_DRAG_KEY);
            dragboard.setContent(clipboardContent);
            
            SideBar.draggingTab = this;
            SideBar.showDragSpaces();
            e.consume();
        });
        
        getGraphic().setOnDragDone(e -> {
            SideBar.hideDragSpaces();
        });
        
    }
    
    public String getName(){
        return name;
    }
    
    public static SideTab getByName(String name){
        if(MainWindow.filesTab.getName().equals(name)){
            return MainWindow.filesTab;
        }else if(MainWindow.textTab.getName().equals(name)){
            return MainWindow.textTab;
        }else if(MainWindow.gradeTab.getName().equals(name)){
            return MainWindow.gradeTab;
        }else if(MainWindow.skillsTab.getName().equals(name)){
            return MainWindow.skillsTab;
        }else if(MainWindow.paintTab.getName().equals(name)){
            return MainWindow.paintTab;
        }
        return null;
    }
    
}
