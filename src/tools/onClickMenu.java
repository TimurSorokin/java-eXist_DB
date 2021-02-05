package tools;

import gui.Gui;
import org.xmldb.api.base.XMLDBException;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class onClickMenu extends MouseAdapter {
    private void PopupEvent(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        JTree tree = (JTree) e.getSource();
        TreePath path = tree.getPathForLocation(x, y);
        if (path == null)
            return;
        DefaultMutableTreeNode rightClickedNode = (DefaultMutableTreeNode) path
                .getLastPathComponent();

        TreePath[] selectionPaths = tree.getSelectionPaths();

        boolean isSelected = false;
        if (selectionPaths != null) {
            for (TreePath selectionPath : selectionPaths) {
                if (selectionPath.equals(path)) {
                    isSelected = true;
                }
            }
        }
        if (!isSelected) {
            tree.setSelectionPath(path);
        }

        // Switch-case would be better option:
       if(rightClickedNode.isLeaf() && rightClickedNode.getParent()!=null  && !rightClickedNode.getParent().equals(rightClickedNode.getRoot())){
            JPopupMenu popup = new JPopupMenu();
            final JMenuItem  delete_resource= new JMenuItem("Eliminar");
            final JMenuItem read_resource = new JMenuItem("Leer/Insertar/Modificar");
            read_resource.addActionListener(e1 -> {
                try {
                    bdtools.readResource(rightClickedNode.getParent().toString(),rightClickedNode.getUserObject().toString());
                } catch (XMLDBException xmldbException) {
                    xmldbException.printStackTrace();
                }
            });
          final JMenuItem save_resource = new JMenuItem("Guardar");
           save_resource.addActionListener(e1 -> {
               System.out.println("Current collection >> "+ Gui.current_colection);
               System.out.println("Current resource >> "+Gui.current_resource);
               try {
                   bdtools.updateResource(Gui.current_colection,Gui.current_resource,Gui.area.getText());
               } catch (XMLDBException xmldbException) {
                   xmldbException.printStackTrace();
               }
           });


           delete_resource.addActionListener(ev->bdtools.removeResource(rightClickedNode.getParent().toString(),rightClickedNode.getUserObject().toString()));
           popup.add(read_resource);
           popup.add(save_resource);
           popup.add(delete_resource);
            popup.show(tree, x, y);
        }else if(rightClickedNode.getParent()==null){
            JPopupMenu popup = new JPopupMenu();
            final JMenuItem  add_collection= new JMenuItem("Anadir colecion");

           add_collection.addActionListener(ev->bdtools.addCollection());
            popup.add(add_collection);

            popup.show(tree, x, y);
        }
        else
        {
            JPopupMenu popup = new JPopupMenu();
            final JMenuItem  add_resource= new JMenuItem("Anadir recurso");
            final JMenuItem xquery_collection = new JMenuItem("Xquery");
            xquery_collection.addActionListener(e1 -> Gui.xquery(rightClickedNode.getUserObject().toString()));

            final JMenuItem delete_col = new JMenuItem("Eliminar");
            add_resource.addActionListener(ev->bdtools.addResource(rightClickedNode.getUserObject().toString()));
            delete_col.addActionListener(e1 -> bdtools.removeCollection(rightClickedNode.getUserObject().toString()));
            popup.add(add_resource);
            popup.add(xquery_collection);
            popup.add(delete_col);
            popup.show(tree, x, y);
        }
    }

    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger())
            PopupEvent(e);
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
            PopupEvent(e);
    }
}