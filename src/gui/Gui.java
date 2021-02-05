package gui;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import tools.onClickMenu;
import tools.bdtools;
import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Gui {
    public static String current_colection;
  public static String current_resource;
    static JFrame frame;
    static bdtools bd = new bdtools();
    static JSplitPane splitPane;
    public static JTextArea area;

    public Gui()
    {
        frame = new JFrame("Practica eXist-db");
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("Archivo");

        JButton jbuton = new JButton("Consultas");
        jbuton.addActionListener(e -> xquery(JOptionPane.showInputDialog(null,"Nombre de collecion")));

        menuBar.add(file);
        menuBar.add(jbuton);


        JMenuItem create_resource,create_collection,delete_collection,delete_resource;
        create_collection = new JMenuItem("Crear collecion");
        create_resource = new JMenuItem("Crear recurso");
        delete_resource = new JMenuItem("Eliminar recurso");
        delete_resource.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog("Nombre de collecion");
                String ss = JOptionPane.showInputDialog("Nombre de RECURSO");
                bdtools.removeResource(s,ss);
            }
        });
        create_resource.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog("Nombre de collecion");
                bdtools.addResource(s);
            }
        });
        create_collection.addActionListener(e -> bdtools.addCollection());
        delete_collection = new JMenuItem("Eliminar collecion");
        delete_collection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog("Nombre de collecion");
                bdtools.removeCollection(s);
            }
        });
        file.add(create_collection);
        file.add(create_resource);
        file.add(delete_resource);
        file.add(delete_collection);
        //insertar datos

        JTree tree = new JTree(bd.GetTree());
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        tree.addMouseListener(new onClickMenu());



        JMenuItem save_data,read_data,modify_data;
        save_data = new JMenuItem("Guardar");
        save_data.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Current collection >> "+current_colection);
                System.out.println("Current resource >> "+current_resource);
                try {
                    bdtools.updateResource(current_colection,current_resource,area.getText());
                } catch (XMLDBException xmldbException) {
                    xmldbException.printStackTrace();
                }

            }
        });
        read_data = new JMenuItem("Leer recurso");
        read_data.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog("Nombre de collecion");
                String ss = JOptionPane.showInputDialog("Nombre de RECURSO");
                try {
                    bdtools.readResource(s,ss);
                } catch (XMLDBException xmldbException) {
                    xmldbException.printStackTrace();
                }

            }
        });
        modify_data = new JMenuItem("Modificar recurso");
        file.add(read_data);
        file.add(modify_data);
        file.add(save_data);

        //frame stuff
        frame.setLayout(new BorderLayout());
        frame.setJMenuBar(menuBar);


        area = new JTextArea("data here");
        JPanel jpdata = new JPanel();
       JScrollPane jScrollpane = new JScrollPane(area);
        jScrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        jpdata.setLayout(new BorderLayout());
        jpdata.add(jScrollpane);
       splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,tree,jpdata);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(190);

        frame.add(splitPane);
        frame.setSize(800,600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static void dataOutput(XMLResource resource)
    {
        System.out.println("dataoutput()");
        try {
            System.out.println("Setting area");
            area.setText((String) resource.getContent());
            frame.revalidate();
            frame.repaint();
            frame.setVisible(true);
        } catch (XMLDBException e) {
            e.printStackTrace();
        }
    }
    public static void UpdateTree(){
            frame.remove(splitPane);
            JTree tree = new JTree(bd.GetTree());
            tree.getSelectionModel().setSelectionMode(
                    TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            tree.addMouseListener(new onClickMenu());
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,tree,area);
        splitPane.setDividerLocation(190);
            frame.add(splitPane);
            frame.setVisible(true);

    }
    public static  void xquery(String collection_name){
        JFrame f= new JFrame();
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel left = new JPanel();
        JPanel right = new JPanel();
        JTextArea xquery = new JTextArea();
        JTextArea output = new JTextArea();
        JButton go_query = new JButton("Execute Query");


        JLabel xqueryhere = new JLabel("Xquery here");
        JLabel outputhere = new JLabel("output here");


        JScrollPane scolloutput = new JScrollPane(output);
        scolloutput.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scolloutput.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        JScrollPane scrollxquery = new JScrollPane(xquery);
        scrollxquery.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollxquery.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);


        left.setLayout(new BorderLayout());
        left.add(xqueryhere,BorderLayout.NORTH);
        left.add(scrollxquery,BorderLayout.CENTER);
        left.add(go_query,BorderLayout.SOUTH);
        left.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));



        right.setLayout(new BorderLayout());
        right.add(outputhere,BorderLayout.NORTH);
        right.add(scolloutput,BorderLayout.CENTER);
        right.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));
        go_query.addActionListener(e -> {
            ResourceSet s = null;
            try {
                s = bd.executeQuery(xquery.getText());

            } catch (XMLDBException xmldbException) {
                xmldbException.printStackTrace();
            }
            if(s!=null)
            {

                try {
                    ResourceIterator resourceIterator = s.getIterator();
                    if (!resourceIterator.hasMoreResources()) {
                        System.out.println("No results");
                    } else {
                        while (resourceIterator.hasMoreResources()) {
                            Resource result = resourceIterator.nextResource();
                            System.out.println((String) result.getContent());
                            output.append((String) result.getContent() + "\n");
                        }
                    }
                }
                catch(Exception ex){
                    JOptionPane.showMessageDialog(null,ex.toString());
                    }
            }

        });

        f.setLayout(new BorderLayout());
        f.add(left,BorderLayout.WEST);
        f.add(right,BorderLayout.CENTER);
        f.setSize(460,320);

        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

}