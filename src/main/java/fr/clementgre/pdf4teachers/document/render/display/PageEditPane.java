/*
 * Copyright (c) 2020-2024. Clément Grennerat
 * All rights reserved. You must refer to the licence Apache 2.
 */

package fr.clementgre.pdf4teachers.document.render.display;

import fr.clementgre.pdf4teachers.components.menus.NodeMenuItem;
import fr.clementgre.pdf4teachers.document.editions.undoEngine.UType;
import fr.clementgre.pdf4teachers.interfaces.windows.MainWindow;
import fr.clementgre.pdf4teachers.interfaces.windows.language.TR;
import fr.clementgre.pdf4teachers.interfaces.windows.margin.MarginWindow;
import fr.clementgre.pdf4teachers.utils.panes.PaneUtils;
import fr.clementgre.pdf4teachers.utils.svg.SVGPathIcons;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class PageEditPane extends VBox {
    
    private final Button ascendButton = getCustomButton(SVGPathIcons.FORWARD_ARROWS, TR.tr("document.pageActions.moveUp.tooltip"), -90);
    private final Button descendButton = getCustomButton(SVGPathIcons.FORWARD_ARROWS, TR.tr("document.pageActions.moveDown.tooltip"), 90);
    private final Button rotateLeftButton = getCustomButton(SVGPathIcons.UNDO, TR.tr("document.pageActions.rotateLeft.tooltip"));
    private final Button rotateRightButton = getCustomButton(SVGPathIcons.REDO, TR.tr("document.pageActions.rotateRight.tooltip"));
    private final Button deleteButton = getCustomButton(SVGPathIcons.PLUS, TR.tr("document.pageActions.delete.tooltip"), 45);
    private final Button newButton = getCustomButton(SVGPathIcons.PLUS, TR.tr("document.pageActions.addPages.tooltip"));
    private final Button captureButton = getCustomButton(SVGPathIcons.FULL_SCREEN, TR.tr("document.pageActions.capture.tooltip"));
    private final Button cropButton = getCustomButton(SVGPathIcons.CROP, TR.tr("document.pageActions.crop.tooltip"));
    
    private ContextMenu menu = new ContextMenu();
    
    private PageRenderer page;
    
    public PageEditPane(PageRenderer page){
        this.page = page;
        
        ascendButton.setOnAction((e) -> MainWindow.mainScreen.document.pdfPagesRender.editor.ascendPage(page));
        
        descendButton.setOnAction((e) -> MainWindow.mainScreen.document.pdfPagesRender.editor.descendPage(page));
        
        rotateLeftButton.setOnAction((e) -> MainWindow.mainScreen.document.pdfPagesRender.editor.rotatePage(page, false, UType.PAGE, true));
        
        rotateRightButton.setOnAction((e) -> MainWindow.mainScreen.document.pdfPagesRender.editor.rotatePage(page, true, UType.PAGE, true));
        
        deleteButton.setOnAction((e) -> MainWindow.mainScreen.document.pdfPagesRender.editor.deletePage(page));
        
        newButton.setOnMouseClicked((e) -> {
            menu.hide();
            menu.getItems().clear();
            menu.getItems().addAll(getNewPageMenu(page.getPage(), page.getPage(), page.getPage()+1, page.getPage() == 0, false));
            NodeMenuItem.setupMenu(menu);
            menu.show(newButton, e.getScreenX(), e.getScreenY());
        });
        
        captureButton.setOnMouseClicked((e) -> {
            menu.hide();
            menu.getItems().clear();
            menu.getItems().addAll(getCaptureMenu(page, false));
            NodeMenuItem.setupMenu(menu);
            menu.show(captureButton, e.getScreenX(), e.getScreenY());
        });
        
        cropButton.setOnMouseClicked((e) -> {
            menu.hide();
            menu.getItems().clear();
            menu.getItems().addAll(getCropMenu(page, false));
            NodeMenuItem.setupMenu(menu);
            menu.show(cropButton, e.getScreenX(), e.getScreenY());
        });
        
        getChildren().addAll(ascendButton, descendButton, rotateLeftButton, rotateRightButton, deleteButton, newButton, cropButton, captureButton);
        
        updateVisibility();
        updatePosition();
        page.getChildren().add(this);
        
    }
    
    public static ArrayList<MenuItem> getNewPageMenu(int page, int indexBefore, int indexAfter, boolean askBefore, boolean vanillaMenu){
        ArrayList<MenuItem> menus = new ArrayList<>();
        
        if(askBefore){
            MenuItem addTopBlank = getMenuItem(TR.tr("document.pageActions.addPages.blank.above"), vanillaMenu);
            MenuItem addTopConvert = getMenuItem(TR.tr("document.pageActions.addPages.converted.above"), vanillaMenu);
            MenuItem addTopPdf = getMenuItem(TR.tr("document.pageActions.addPages.pdf.above"), vanillaMenu);
            menus.add(addTopBlank);
            menus.add(addTopConvert);
            menus.add(addTopPdf);
            menus.add(new SeparatorMenuItem());
            
            addTopBlank.setOnAction(ignored -> MainWindow.mainScreen.document.pdfPagesRender.editor.newBlankPage(page, indexBefore));
            addTopConvert.setOnAction(ignored -> MainWindow.mainScreen.document.pdfPagesRender.editor.newConvertPage(page, indexBefore));
            addTopPdf.setOnAction(ignored -> MainWindow.mainScreen.document.pdfPagesRender.editor.newPdfPage(page));
        }
        
        MenuItem addBlank = getMenuItem(TR.tr("document.pageActions.addPages.blank"), vanillaMenu);
        MenuItem addConvert = getMenuItem(TR.tr("document.pageActions.addPages.converted"), vanillaMenu);
        MenuItem addTopPdf = getMenuItem(TR.tr("document.pageActions.addPages.pdf"), vanillaMenu);
        menus.add(addBlank);
        menus.add(addConvert);
        menus.add(addTopPdf);
        
        addBlank.setOnAction(ignored -> MainWindow.mainScreen.document.pdfPagesRender.editor.newBlankPage(page, MainWindow.mainScreen.document.pdfPagesRender.editor.clampAddPageIndex(indexAfter)));
        addConvert.setOnAction(ignored -> MainWindow.mainScreen.document.pdfPagesRender.editor.newConvertPage(page, MainWindow.mainScreen.document.pdfPagesRender.editor.clampAddPageIndex(indexAfter)));
        addTopPdf.setOnAction(ignored -> MainWindow.mainScreen.document.pdfPagesRender.editor.newPdfPage(MainWindow.mainScreen.document.pdfPagesRender.editor.clampAddPageIndex(indexAfter)));
        
        return menus;
    }
    
    public static ArrayList<MenuItem> getCaptureMenu(PageRenderer page, boolean vanillaMenu){
        ArrayList<MenuItem> menus = new ArrayList<>();
        
        MenuItem capturePage = getMenuItem(TR.tr("document.pageActions.capture.wholePage"), vanillaMenu);
        menus.add(capturePage);
        capturePage.setOnAction(ignored -> {
            page.quitVectorEditMode();
            MainWindow.mainScreen.document.pdfPagesRender.editor.capture(page.getPage(), false, false, null);
        });
        
        
        MenuItem captureSelection = getMenuItem(TR.tr("document.pageActions.capture.selectArea"), vanillaMenu);
        menus.add(captureSelection);
        captureSelection.setOnAction(ignored -> {
            page.quitVectorEditMode();
            PageZoneSelector recorder = page.getPageZoneSelector();
            recorder.setSelectionZoneType(PageZoneSelector.SelectionZoneType.PDF_ON_DARK);
            recorder.setupSelectionZoneOnce(positionDimensions -> {
                MainWindow.mainScreen.document.pdfPagesRender.editor.capture(page.getPage(), false, false, positionDimensions);
            });
            recorder.setDoShow(true);
        });
        
        
        if(MainWindow.mainScreen.document.numberOfPages != 1){
            
            boolean selectionCapture = MainWindow.mainScreen.isEditPagesMode() && MainWindow.mainScreen.hasDocument(false) && MainWindow.mainScreen.document.getSelectedPages().size() > 1;
            
            MenuItem captureDocument = getMenuItem(selectionCapture ? TR.tr("document.pageActions.capture.selectedPages") : TR.tr("document.pageActions.capture.allDocument"), vanillaMenu);
            menus.add(captureDocument);
            
            captureDocument.setOnAction(ignored -> {
                page.quitVectorEditMode();
                MainWindow.mainScreen.document.pdfPagesRender.editor.capture(-1, selectionCapture, !selectionCapture, null);
            });
        }
        
        return menus;
    }
    public static ArrayList<MenuItem> getCropMenu(PageRenderer page, boolean vanillaMenu){
        ArrayList<MenuItem> menus = new ArrayList<>();
        
        MenuItem cropPage = getMenuItem(TR.tr("document.pageActions.crop.crop"), vanillaMenu);
        menus.add(cropPage);
        cropPage.setOnAction(ignored -> {
            page.quitVectorEditMode();
            PageZoneSelector recorder = page.getPageZoneSelector();
            recorder.setSelectionZoneType(PageZoneSelector.SelectionZoneType.PDF_ON_DARK);
            recorder.setupSelectionZoneOnce(positionDimensions -> {
                float marginLeft = -(float) (positionDimensions.getX() * 100d / page.getWidth());
                float marginTop = -(float) (positionDimensions.getY() * 100d / page.getWidth());
                float marginRight = -(float) ((page.getWidth() - positionDimensions.getX() - positionDimensions.getWidth()) * 100d / page.getWidth());
                float marginBottom = -(float) ((page.getHeight() - positionDimensions.getY() - positionDimensions.getHeight()) * 100d / page.getWidth());
                MainWindow.mainScreen.document.pdfPagesRender.editor.setPageMargin(page.getPage(),
                        marginTop, marginRight, marginBottom, marginLeft,
                        true, false, UType.PAGE);
            });
            recorder.setDoShow(true);
        });
        
        MenuItem marginPage = getMenuItem(TR.tr("document.pageActions.crop.marginPage"), vanillaMenu);
        menus.add(marginPage);
        marginPage.setOnAction(ignored -> {
            page.quitVectorEditMode();
            new MarginWindow(page.getPage());
        });
        if(MainWindow.mainScreen.document.numberOfPages != 1){
            
            boolean selectionCapture = MainWindow.mainScreen.isEditPagesMode() && MainWindow.mainScreen.hasDocument(false) && MainWindow.mainScreen.document.getSelectedPages().size() > 1;
            
            MenuItem captureDocument = getMenuItem(selectionCapture ? TR.tr("document.pageActions.crop.marginSelected") : TR.tr("document.pageActions.crop.marginAll"), vanillaMenu);
            menus.add(captureDocument);
            
            captureDocument.setOnAction(ignored -> {
                page.quitVectorEditMode();
                if(selectionCapture)
                    new MarginWindow(MainWindow.mainScreen.document.getSelectedPages().stream().sorted().toArray(Integer[]::new));
                else
                    new MarginWindow(MainWindow.mainScreen.document.getPages().stream().map(PageRenderer::getPage).toArray(Integer[]::new));
            });
        }
        
        return menus;
    }
    
    private static MenuItem getMenuItem(String title, boolean vanillaItem){
        if(vanillaItem) return new MenuItem(title);
        else return new NodeMenuItem(title, false);
    }
    
    private Button getCustomButton(String path, String text){
        return getCustomButton(path, text, 0);
    }
    
    private Button getCustomButton(String path, String text, int rotate){
        Button button = new Button();
        button.setStyle("-fx-background-color: white;");
        PaneUtils.setHBoxPosition(button, 30, 30, 0);
        button.setCursor(Cursor.HAND);
        button.setGraphic(SVGPathIcons.generateImage(path, "#dc3e3e", 3, 30, rotate));
        button.setTooltip(PaneUtils.genWrappedToolTip(text));
        return button;
    }
    
    public void updatePosition(){
        if(this.page == null) return;
        
        int buttonNumber = 8;
        double factor = .7 / MainWindow.mainScreen.getZoomFactor();
        double height = (30 * buttonNumber) * (factor - 1);
        double width = 30 * (factor - 1);

        setLayoutY(height / 2d);
        setLayoutX(page.getWidth() + width / 2d);

        setScaleX(factor);
        setScaleY(factor);
    }
    
    public void delete(){
        this.page = null;
        this.menu = null;
        
        ascendButton.setOnAction(null);
        descendButton.setOnAction(null);
        rotateLeftButton.setOnAction(null);
        rotateRightButton.setOnAction(null);
        deleteButton.setOnAction(null);
        newButton.setOnMouseClicked(null);
        cropButton.setOnMouseClicked(null);
        captureButton.setOnMouseClicked(null);
        
    }
    
    public void updateVisibility(){
        ascendButton.setDisable(page.getPage() == 0);
        descendButton.setDisable(page.getPage() == MainWindow.mainScreen.document.numberOfPages - 1);
        deleteButton.setDisable(MainWindow.mainScreen.document.numberOfPages == 1);
    }
    
    // Hide pane but only if no menu are visible.
    public void checkMouseExited(){
        if(!menu.isShowing()){
            setVisible(false);
        }
    }
}
