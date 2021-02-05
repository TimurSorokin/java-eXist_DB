package tools;

import gui.Gui;
import org.exist.xmldb.EXistResource;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XQueryService;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.transform.OutputKeys;
import java.io.File;

public class bdtools {
    private static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db";
    private static String driver = "org.exist.xmldb.DatabaseImpl";
    private Database database;
    private String user;
    private String pswd;

    public bdtools(){
        this.database = null;
        this.user = "admin";
        this.pswd = "qwerty";
        connect();
        GetTree();
    }
    private void connect() {
        try {
            Class cl = Class.forName(driver);
            Database db = (Database) cl.newInstance();
            DatabaseManager.registerDatabase(db);
            System.out.println("Connection established");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.toString());
            System.out.println("Couldn't establish connection. The program will close now");
        }
    }

    public DefaultTreeModel GetTree(){
        Collection collection = null;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("eXist");
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        try{
            collection =  DatabaseManager.getCollection(URI,"admin","qwerty");

            String[] data = collection.listChildCollections();
            int index =0;
            for(String k: data){
              //  System.out.println("====\nStarting for loop");
              DefaultMutableTreeNode node = new DefaultMutableTreeNode(k);
              treeModel.insertNodeInto(node,root,index);
              //System.out.println("Getting resources of >>> " + k);
            String [] data_res= DatabaseManager.getCollection(URI+"/"+k).listResources();
            int iindex =0;
              for(String jj:data_res){
                 // System.out.println(jj);
                      treeModel.insertNodeInto(new DefaultMutableTreeNode(jj),node,iindex);
                      iindex++;
              }

              index++;
                //System.out.println("====\nFinishing for loop");

            }

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (collection != null) {
                try {
                    collection.close();
                } catch (Exception ff) {
                    JOptionPane.showMessageDialog(null, ff.toString());
                }

            }
        }
        return treeModel;
    }

    public static void addCollection() {
        String s = JOptionPane.showInputDialog("Nombre de collecion");
        if(s!=null)
        {
            Collection collection = null;
            try{
                collection = DatabaseManager.getCollection(URI,"admin","qwerty");
                CollectionManagementService cms = (CollectionManagementService)collection.getService("CollectionManagementService","1.0");
                 cms.createCollection(s);
                System.out.println("Finished");
                Gui.UpdateTree();
                collection.close();
            }catch(Exception e){
                JOptionPane.showMessageDialog(null,e.toString());
            }finally {

                if (collection != null) {
                    try {
                        collection.close();
                    } catch (Exception ff) {
                        JOptionPane.showMessageDialog(null, ff.toString());
                    }

                }
            }

        }


    }
    public static void removeCollection(String node_name) {
            Collection collection = null;
        int opt = JOptionPane.showConfirmDialog(null, "¿Quieres eliminar esta collecion?", "Confirmacion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);


        System.out.println(opt);
        if(opt==0)
        {
            try{
                collection = DatabaseManager.getCollection(URI,"admin","qwerty");
                CollectionManagementService cms = (CollectionManagementService)collection.getService("CollectionManagementService","1.0");
                cms.removeCollection(node_name);
                System.out.println("Finished");
                Gui.UpdateTree();
                collection.close();
            }catch(Exception e){
                e.printStackTrace();
            }finally {
                if (collection != null) {
                    try {
                        collection.close();
                    } catch (Exception f) {
                        JOptionPane.showMessageDialog(null, f.toString());
                    }

                }
            }
        }else{
            JOptionPane.showMessageDialog(null,"Operacion cancelada");
        }
    }

    public static void addResource(String collection_name){
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
        chooser.setFileFilter(filter);
        Collection collection = null;
        File f = null;
        Resource res = null;
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            f = chooser.getSelectedFile();
        }
        try {
            collection = DatabaseManager.getCollection(URI+"/"+collection_name, "admin", "qwerty");
           res =  collection.createResource(f.getName(),"XMLResource");
            res.setContent(f);
            collection.storeResource(res);
            Gui.UpdateTree();
            JOptionPane.showMessageDialog(null,"Recurso: " + f.getName() + " Anadido");
            collection.close();
        }catch(Exception e){
      JOptionPane.showMessageDialog(null,e.toString());
        }finally {
            if (res != null) {
                try {
                    ((EXistResource) res).freeResources();
                } catch (Exception fx) {
                    JOptionPane.showMessageDialog(null, fx.toString());
                }
            }
            if (collection != null) {
                try {
                    collection.close();
                } catch (Exception ff) {
                    JOptionPane.showMessageDialog(null, ff.toString());
                }

            }
        }
    }
    public static void removeResource(String collection_name,String resource_name){
        Collection collection = null;
        Resource res= null;
        try{
           collection = DatabaseManager.getCollection(URI+"/"+collection_name, "admin", "qwerty");
             res = collection.getResource(resource_name);
            if(res!=null)
            {
                collection.removeResource(res);
                JOptionPane.showMessageDialog(null,"Recurso: "+resource_name + " Eliminado");
            }
            Gui.UpdateTree();
        } catch (XMLDBException e) {
            e.printStackTrace();
        } finally {
            if (res != null) {
                try {
                    ((EXistResource) res).freeResources();
                } catch (Exception fx) {
                    JOptionPane.showMessageDialog(null, fx.toString());
                }
            }
            if (collection != null) {
                try {
                    collection.close();
                } catch (Exception f) {
                    JOptionPane.showMessageDialog(null, f.toString());
                }

            }
        }
        System.out.println("Collection: "+collection_name);
        System.out.println("Resource: "+resource_name);
    }

    public static void readResource(String collection_name,String resource_name) throws XMLDBException {
        Gui.current_colection = collection_name;
        Gui.current_resource =  resource_name;

        System.out.println("Statring read resource");
        XMLResource res = null;
        Collection collection = null;
        try {
            collection = DatabaseManager.getCollection(URI + "/" + collection_name, "admin", "qwerty");
            res = (XMLResource) collection.getResource(resource_name);
            System.out.println("Setting new dataoutput");
            Gui.dataOutput(res);
        } finally {
            if (res != null) {
                try {
                    ((EXistResource) res).freeResources();
                } catch (Exception fx) {
                    JOptionPane.showMessageDialog(null, fx.toString());
                }
            }
            if (collection != null) {
                try {
                    collection.close();
                } catch (Exception f) {
                    JOptionPane.showMessageDialog(null, f.toString());
                }

            }
        }
    }


    public static void updateResource(String collection_name,String resource_name,String output) throws XMLDBException {

        System.out.println("Statring  update");
        XMLResource res = null;
        Collection collection = null;
        try {
            collection = DatabaseManager.getCollection(URI + "/" + collection_name, "admin", "qwerty");
            res = (XMLResource) collection.getResource(resource_name);
           res.setContent(output);
           collection.storeResource(res);
           JOptionPane.showMessageDialog(null,"Archivo guardado");
        } finally {
            if (res != null) {
                try {
                    ((EXistResource) res).freeResources();
                } catch (Exception fx) {
                    JOptionPane.showMessageDialog(null, fx.toString());
                }
            }
            if (collection != null) {
                try {
                    collection.close();
                } catch (Exception f) {
                    JOptionPane.showMessageDialog(null, f.toString());
                }

            }
        }
    }


    public ResourceSet executeQuery(String query) throws XMLDBException {
        System.out.println("Starting query execute");
        XMLResource res = null;
        Collection collection = null;
        ResourceSet resourceSet = null;
        try {
            collection = DatabaseManager.getCollection(URI+"/my_db", "admin", "qwerty");
            XQueryService xQueryService = (XQueryService) collection.getService("XQueryService","1.0");
            xQueryService.setProperty(OutputKeys.INDENT,"yes");
            xQueryService.setProperty(OutputKeys.ENCODING,"yes");
            CompiledExpression compiledExpression = xQueryService.compile(query);
            resourceSet = xQueryService.execute(compiledExpression);
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null,ex.toString());
        } finally {
            if (res != null) {
                try {
                    ((EXistResource) res).freeResources();
                } catch (Exception fx) {
                    JOptionPane.showMessageDialog(null, fx.toString());
                }
            }
            if (collection != null) {
                try {
                    collection.close();
                } catch (Exception f) {
                    JOptionPane.showMessageDialog(null, f.toString());
                }

            }
        }
        return resourceSet;
    }

}
