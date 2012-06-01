package net.wellfield.magpie;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;
import javax.xml.namespace.QName;

import layout.SpringUtilities;
import net.wellfield.magpie.gui.PlayerCellRenderer;
import net.wellfield.magpie.gui.PlayerListModel;
import net.wellfield.magpie.gui.PlayerListSelectionListener;
import net.wellfield.magpie.gui.UPnPCellRenderer;
import net.wellfield.magpie.gui.UPnPListModel;
import net.wellfield.magpie.gui.UPnPListSelectionListener;
import net.wellfield.magpie.upnp.beans.Player;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.UPnP;
import org.cybergarage.xml.parser.kXML2Parser;

public class Start extends JFrame implements WindowListener {
    private static final Log log = LogFactory.getLog(Start.class);

    private static final QName SERVICE_NAME = new QName("http://www.upnp-database.info/", "DataService");
    private static final QName PORT_NAME = new QName("http://www.upnp-database.info/", "DataServicePort");
    
    private static Start start;

    private Server server;
    private static Controller controller;
    
//    private JMenuBar menuBar;
    
    private JTabbedPane tabPane;
    
    private JPanel upnpStuff;
    private JScrollPane upnpScroller;
    private JList upnpList;
    private UPnPListModel upnpListModel;
    private JPanel upnpMessageArea;
    private JSplitPane upnpSplitPane;
    private JToolBar upnpToolBar;
    
    private JPanel playerStuff;
    private JScrollPane playerScroller;
    private JList playerList;
    private PlayerListModel playerListModel;
    private JPanel playerMessageArea;
    private JSplitPane playerSplitPane;
    private JToolBar playerToolBar;

    private JButton btnSubmitUPnP;
    private JButton btnSubmitPlayer;
    
    private static String baseUrl = "http://www.upnp-database.info/";
//    private static String baseUrl = "http://localhost:8080/birdsnest/";

    private StringBuffer playerData;
    

    
    public void addPlayer(Player player) {
        if (!containsPlayer(player.getAddress())) {
            playerListModel.addElement(player);
        }
    }
    
    public void removePlayer(String key) {
        playerListModel.removeElement(key);
    }
    
    public boolean containsPlayer(String key) {
        return playerListModel.containsElement(key);
    }
    
    public Player getPlayer(String key) {
        return playerListModel.getElement(key);
    }
    
    public void addDevice(Device device) {
        if (!device.getUDN().equals("123456magpie7890")) {
            upnpListModel.addElement(device);
        }
    }
    
    public void removeDevice(String udn) {
        upnpListModel.removeElement(udn);
    }
    
    public void deviceSelected() {
        upnpMessageArea.removeAll();
        upnpMessageArea.add(new JLabel("Please wait while analysis is running"));

        upnpMessageArea.validate();
    }
    
    public void playerSelected() {
        JTextArea label = (JTextArea)playerMessageArea.getComponent(0);
        
        Player player = (Player)playerList.getSelectedValue();
        label.setText(player.toString());
        try {
            playerData = new StringBuffer("dev=" + URLEncoder.encode(player.toString(), "UTF-8"));
            playerData.append("&header=" + URLEncoder.encode(player.getHeader(), "UTF-8"));
            playerMessageArea.validate();
            
            btnSubmitPlayer.setEnabled(true);
        } catch (UnsupportedEncodingException ex) {
            if (log.isErrorEnabled()) log.error(ex, ex);
        }
    }
    
    public void deviceUnselected() {
    }
    
    private void sendData(Map<String, Object> submitMap, Map<String, byte[]> imgData) {
        Integer deviceId = sendPost(submitMap);
        if (log.isInfoEnabled()) log.info("deviceResult: " + deviceId);
        //setDetailText(text);
        
        //now we know the deviceId from the db and we can upload the imagedata and assign it to the correct metadata
        for(Iterator<String>it=imgData.keySet().iterator();it.hasNext();) {
            String key = it.next();
            byte[] data = imgData.get(key);
            sendImage(data, deviceId, key);
        }
    }
    
    private String sendPlayerData() {
        String result;
        try {
//            if (log.isDebugEnabled()) log.debug("data: " + data);
            
            // Send data
            String path = baseUrl + "submitPlayerController.view";
            if (log.isInfoEnabled()) log.info("submitting to " + path);
            
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(playerData.toString());
            wr.flush();
            
            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer all = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                all.append(line);
            }
            wr.close();
            rd.close();
            result = all.toString();
            if (log.isInfoEnabled()) log.info("player data submitted");
        } catch (Exception e) {
            if (log.isErrorEnabled()) log.error(e.getMessage(), e);
            result = "0";
        }

        return result;
    }
    
    private Integer sendPost(Map<String, Object> submitMap) {

        try {
            // Send data
            
            //ObjectOutputStream
            String path = baseUrl + "submitDataController.view";
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            
            ObjectOutputStream oos = new ObjectOutputStream(conn.getOutputStream());
            
            oos.writeObject(submitMap);
            oos.flush();
            oos.close();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer all = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                all.append(line);
            }
            
            rd.close();

            /*  RMI
            RmiUtil rmi = Configuration.getRmiUtil();
            Integer deviceId = rmi.doIt(submitMap);
            return deviceId;
            */
            
//            String path = baseUrl + "submitDataController.view";
//            if (log.isDebugEnabled()) log.debug("submitting to " + path);
//            URL url = new URL(path);
//            URLConnection conn = url.openConnection();
//            conn.setDoOutput(true);
//            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//            wr.write(submitData.toString());
//            wr.flush();
//        
//            // Get the response
//            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line;
//            StringBuffer all = new StringBuffer();
//            while ((line = rd.readLine()) != null) {
//                all.append(line);
//            }
//            wr.close();
//            rd.close();
//            result = all.toString();
        } catch (Exception e) {
            if (log.isErrorEnabled()) log.error(e.getMessage(), e);
        }

        return null;
    }
    
    @SuppressWarnings("deprecation")
    private void sendImage(byte[] data, Integer deviceId, String imgUrl) {
        try {
            String path = baseUrl + "submitImageController.view?deviceId=" + deviceId + "&url=" + URLEncoder.encode(imgUrl);
            if (log.isDebugEnabled()) log.debug("submitting image " + imgUrl + " to " + path);
            URL url = new URL(path);
            
            HttpURLConnection con =(HttpURLConnection)url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/octet-stream");   
            
            OutputStream os = con.getOutputStream();
            os.write(data);
            os.flush();
            os.close();
            
            InputStream is = con.getInputStream();
            int c;
            while ((c = is.read()) != -1) {
                System.out.write(c);
            }
            System.out.println();
            is.close();

            con.disconnect();
        } catch (Exception e) {
            if (log.isErrorEnabled()) log.error(e.getStackTrace(), e);
        }
    }

    public void enableBtnSubmitUPnP() {
        btnSubmitUPnP.setEnabled(true);
    }
    
    public void updateMessageArea(String message) {
        upnpMessageArea.removeAll();
        upnpMessageArea.add(new JTextArea(message));
        
        upnpMessageArea.validate();
    }

    public Start() {
        this.addWindowListener(this);
        
        try {
            server = new Server("data/description.xml");
            server.start();
        } catch (Exception e) {
            log.error("MediaServer not available");
        }
        
        setTitle("Magpie UPnP Datacollector");
        
//      Debug.on();

        /*
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);
        
        JMenuItem item = new JMenuItem("Exit");
        menu.add(item);
        */
        tabPane = new JTabbedPane();
        
        upnpStuff = new JPanel();
        upnpStuff.setLayout(new BorderLayout(0, 0));
        
        upnpToolBar = new JToolBar(JToolBar.HORIZONTAL);
        upnpToolBar.setFloatable(false);
        upnpToolBar.setRollover(true);
        upnpStuff.add(upnpToolBar, BorderLayout.PAGE_START);

        final JDialog upnpDialog = new JDialog(this, true);
        JPanel dlgPanel = new JPanel();
        dlgPanel.setOpaque(true);
        dlgPanel.setLayout(new BoxLayout(dlgPanel, BoxLayout.Y_AXIS));
        upnpDialog.setContentPane(dlgPanel);
        
        JTextArea upnpHelp = new JTextArea("It is optional to provide any information.\r\n" +
        		"None of the information provided will be given away.\r\n" +
        		"But it will help us to maintain the data more easily.");
        upnpHelp.setEditable(false);
        dlgPanel.add(upnpHelp);
        
        String[] fields = {"Your Name:", "Your E-Mail:", "Optional Message"};
        int fieldLen = fields.length;
        final JPanel upnpInputPanel = new JPanel(new SpringLayout());
        for (int i=0;i<fieldLen;i++) {
            JLabel l = new JLabel(fields[i]);
            upnpInputPanel.add(l);
            JTextField textField = new JTextField();
            l.setLabelFor(textField);
            upnpInputPanel.add(textField);
        }
        
        SpringUtilities.makeCompactGrid(upnpInputPanel, fieldLen, 2, 0, 0, 0, 0);
        dlgPanel.add(upnpInputPanel);

        final JButton btnUPnP = new JButton("Submit Data");
        final JButton btnUPnPClose = new JButton("Close");
        btnUPnP.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                Device dev = (Device)upnpList.getSelectedValue();
                String udn = dev.getUDN();
                Map<String, Object> submitMap = UPnPAnalyzer.getInstance(udn).getSubmitMap();
                Map<String, byte[]> imgData = UPnPAnalyzer.getInstance(udn).getImgData();
                
                submitMap.put("contributor", ((JTextField)upnpInputPanel.getComponent(1)).getText());
                submitMap.put("email", ((JTextField)upnpInputPanel.getComponent(3)).getText());
                submitMap.put("message", ((JTextField)upnpInputPanel.getComponent(5)).getText());

                btnUPnP.setEnabled(false);
                sendData(submitMap, imgData);
                btnUPnPClose.setVisible(true);
                btnUPnP.setVisible(false);
            } 
        });
        dlgPanel.add(btnUPnP);
        
        upnpDialog.pack();
        
        btnUPnPClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                btnUPnP.setEnabled(true);
                btnUPnP.setVisible(true);
                btnUPnPClose.setVisible(false);
                upnpDialog.setVisible(false);
            }
        });
        dlgPanel.add(btnUPnPClose);
        
        btnSubmitUPnP = new JButton("Submit Data");
        btnSubmitUPnP.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (btnSubmitUPnP.isEnabled()) {
                    btnUPnP.setText("Submit Data");
                    btnUPnP.setEnabled(true);
                    upnpDialog.setVisible(true);
                }
            } 
        });
        btnSubmitUPnP.setEnabled(false);
        upnpToolBar.add(btnSubmitUPnP);
        
        upnpListModel = new UPnPListModel();
        upnpList = new JList(upnpListModel);
        upnpList.setCellRenderer(new UPnPCellRenderer());
        upnpList.addListSelectionListener(new UPnPListSelectionListener());
        upnpScroller = new JScrollPane(upnpList);

        upnpMessageArea = new JPanel();
        upnpMessageArea.setLayout(new BoxLayout(upnpMessageArea, BoxLayout.Y_AXIS));
        JTextArea msg = new JTextArea("Select a device to automatically analyze the device.\r\n" +
                "When the analysis is done you will find the raw result here.\r\n" +
                "You can submit your data then by clicking the button on top.");
        msg.setEditable(false);
        msg.setSize(400, 200);
        upnpMessageArea.add(msg);
        
        JScrollPane upnpMessageScrollPane = new JScrollPane(upnpMessageArea);
        
        upnpSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, upnpScroller, upnpMessageScrollPane);
        upnpStuff.add(upnpSplitPane, BorderLayout.CENTER);
        
        
        tabPane.addTab("UPnP Servers & Renderers", upnpStuff);
        
        playerStuff = new JPanel();
        playerStuff.setLayout(new BorderLayout(0, 0));
        
        playerToolBar = new JToolBar(JToolBar.HORIZONTAL);
        playerToolBar.setFloatable(false);
        playerToolBar.setRollover(true);
        playerStuff.add(playerToolBar, BorderLayout.PAGE_START);

        final JDialog playerDialog = new JDialog(this, true);
        JPanel plDlgPanel = new JPanel();
        plDlgPanel.setOpaque(true);
        plDlgPanel.setLayout(new BoxLayout(plDlgPanel, BoxLayout.Y_AXIS));
        playerDialog.setContentPane(plDlgPanel);
        
        JTextArea playerHelp = new JTextArea("It is optional to provide any information.\r\n" +
                "None of the information provided will be given away.\r\n" +
                "But it will help us to maintain the data more easily.");
        playerHelp.setEditable(false);
        plDlgPanel.add(playerHelp);
        
        String[] plFields = {"Your Name:", "Your E-Mail:", "Device name:", "Optional Message"};
        fieldLen = plFields.length;
        final JPanel playerInputPanel = new JPanel(new SpringLayout());
        for (int i=0;i<fieldLen;i++) {
            JLabel l = new JLabel(plFields[i]);
            playerInputPanel.add(l);
            JTextField textField = new JTextField();
            l.setLabelFor(textField);
            playerInputPanel.add(textField);
        }
        
        SpringUtilities.makeCompactGrid(playerInputPanel, fieldLen, 2, 0, 0, 0, 0);
        plDlgPanel.add(playerInputPanel);

        final JButton btnPlayer = new JButton("Submit Data");
        final JButton btnPlayerClose = new JButton("Close");
        btnPlayer.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                try {
                    playerData.append("&contributor=" + URLEncoder.encode(((JTextField)playerInputPanel.getComponent(1)).getText(), "UTF-8"));
                    playerData.append("&email=" + URLEncoder.encode(((JTextField)playerInputPanel.getComponent(3)).getText(), "UTF-8"));
                    playerData.append("&deviceName=" + URLEncoder.encode(((JTextField)playerInputPanel.getComponent(5)).getText(), "UTF-8"));
                    playerData.append("&message=" + URLEncoder.encode(((JTextField)playerInputPanel.getComponent(7)).getText(), "UTF-8"));
                    btnPlayer.setEnabled(false);
                    sendPlayerData();
                    btnPlayer.setVisible(false);
                    btnPlayerClose.setVisible(true);
                } catch (UnsupportedEncodingException ex) {
                    if (log.isErrorEnabled()) log.error(ex, ex);
                }
            } 
        });
        plDlgPanel.add(btnPlayer);
        
        
        btnPlayerClose.addMouseListener(new MouseAdapter() {
           @Override
            public void mouseClicked(MouseEvent e) {
                btnPlayerClose.setVisible(false);
                btnPlayer.setVisible(true);
                btnPlayer.setEnabled(true);
                playerDialog.setVisible(false);
            } 
        });
        btnPlayerClose.setVisible(false);
        plDlgPanel.add(btnPlayerClose);
        
        
        
        btnSubmitPlayer = new JButton("Submit Data");
        btnSubmitPlayer.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (btnSubmitPlayer.isEnabled()) {
                    btnPlayer.setText("Submit Data");
                    btnPlayer.setEnabled(true);
                    playerDialog.setVisible(true);
                }
            } 
        });
        btnSubmitPlayer.setEnabled(false);
        
        playerToolBar.add(btnSubmitPlayer);
        
        playerDialog.pack();
        
        playerListModel = new PlayerListModel();
        playerList = new JList(playerListModel);
        playerList.setCellRenderer(new PlayerCellRenderer());
        playerList.addListSelectionListener(new PlayerListSelectionListener());
        playerScroller = new JScrollPane(playerList);
        
        playerMessageArea = new JPanel();
        playerMessageArea.setLayout(new BoxLayout(playerMessageArea, BoxLayout.Y_AXIS));
        JTextArea msg2 = new JTextArea("Use a device to browse the Magpie MediaServer to get analysis data.\r\n" +
        		"If a player is discovered it will be listed on the left.\r\n" +
        		"You can submit the selected player's data by clicking the button on top.");
        msg2.setEditable(false);
        playerMessageArea.add(msg2);
        
        JScrollPane playerMessageScrollPane = new JScrollPane(playerMessageArea);
        playerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, playerScroller, playerMessageScrollPane);
        playerStuff.add(playerSplitPane, BorderLayout.CENTER);
        
        tabPane.addTab("Players", playerStuff);
        
        this.getContentPane().add(tabPane);
        
        this.setSize(640, 480);
        this.setVisible(true);
    }
    
    public static void main(String[] args) throws Exception {
        
        UPnP.setEnable(UPnP.USE_ONLY_IPV4_ADDR);
        UPnP.setDisable(UPnP.USE_ONLY_IPV6_ADDR);
        UPnP.setXMLParser(new kXML2Parser());
        /*
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                start = new Start();

                controller = new Controller();
                controller.start();

            }
        });
        */
        
        start = new Start();

        controller = new Controller();
        controller.start();
    }
    
    public static Start getInstance() {
        return start;
    }

    public void windowClosing(WindowEvent e) {
        if (server != null && server.isRunning()) {
            server.byebye();
        }
        System.exit(0);
    }
    
    public void windowDeactivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
}
