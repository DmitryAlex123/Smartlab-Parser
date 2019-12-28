import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

    private static ArrayList<Papiren> papirenList = new ArrayList<>();
    private static ArrayList<Papiren> savePapirenList = new ArrayList<>();
    private static DefaultTableModel tableModel;

    public static void main(String[] args) throws IOException {
        parsingSmartlab();
        createGUI();
    }


    public static void createGUI(){
        JFrame window = new JFrame();
        window.setSize(760, 420);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("ММВБ Парсер 16М");
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setLayout(null);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Название ");
        tableModel.addColumn("Тикер ");
        tableModel.addColumn("Стоимость");
        tableModel.addColumn("Капитализация ");
        JTable table = new JTable(tableModel);
        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBounds(10, 10, 735, 330);
        window.add(scrollTable);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(10, 340, 735, 40);
        window.add(buttonPanel);

        JButton button1 = new JButton("       Добавить ЦБ      ");
        buttonPanel.add(button1);

        JButton button2 = new JButton("   Обновить данные   ");
        buttonPanel.add(button2);

        JButton button3 = new JButton("   Экспорт настроек   ");
        buttonPanel.add(button3);

        JButton button4 = new JButton("   Импорт настроек   ");
        buttonPanel.add(button4);

        window.setVisible(true);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog addWindow = new JDialog();
                addWindow.setTitle("Добавить Ценную Бумагу");
                addWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                addWindow.setSize(360, 200);
                addWindow.setModal(true);
                addWindow.setLocationRelativeTo(null);
                addWindow.setResizable(false);
                addWindow.setLayout(null);

                DefaultComboBoxModel boxModel = new DefaultComboBoxModel();
                for(int i = 0; i < papirenList.size(); i++){

                    boxModel.addElement(papirenList.get(i));

                }
                JComboBox comboBox = new JComboBox(boxModel);
                comboBox.setBounds(20, 20, 320, 24);
                addWindow.add(comboBox);

                JButton buttonOK = new JButton("Добавить...");
                buttonOK.setBounds(130, 100, 100, 24);
                addWindow.add(buttonOK);


                buttonOK.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Papiren p = (Papiren)comboBox.getSelectedItem();
                        System.out.println();
                        System.out.println(p.print());
                        //сохранялка добавленных
                        savePapirenList.add(p);
                        //in to gui table
                        String[] str = {p.getName(), p.getTicker(), p.getPrice(), p.getCap_price()};
                        tableModel.addRow(str);



                    }
                });

                addWindow.setVisible(true);

            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0);
                try{
                    parsingSmartlab();
                }catch (IOException ex){
                    System.out.println(ex);
                }


                for (int i = 0; i < savePapirenList.size(); i++){
                    for(int j = 0; j < papirenList.size(); j++){
                        Papiren pSave = savePapirenList.get(i);
                        Papiren p = papirenList.get(j);
                        if (p.getName().equalsIgnoreCase(pSave.getName())){
                            String[] str = {p.getName(), p.getTicker(), p.getPrice(), p.getCap_price()};
                            tableModel.addRow(str);
                            break;
                        }
                    }
                }
            }
        });

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serializeInFile();
            }
        });

        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deserializeFromFile();
            }
        });

    }

    public static void parsingSmartlab() throws IOException{
        Document document = Jsoup.connect("https://smart-lab.ru/q/shares/").get();
        Element table = document.select("table").first();
        Elements rows = table.select("tr");


        for (int i = 2; i < 18; i++){
            Element row = rows.get(i);
            Elements cols = row.select("td");
            System.out.print(cols.get(2).text());
            System.out.print("   ");
            System.out.print(cols.get(3).text());
            System.out.print("   ");
            System.out.print(cols.get(6).text());
            System.out.print("   ");
            System.out.print(cols.get(13).text());
            System.out.println();
            papirenList.add(new Papiren(cols.get(2).text(), cols.get(3).text(), cols.get(6).text(), cols.get(13).text()));
        }
    }

    public static void serializeInFile(){
        try{
            FileOutputStream fos_str = new FileOutputStream("file.myFile");
            ObjectOutputStream objOut_str = new ObjectOutputStream(fos_str);
            objOut_str.writeObject(savePapirenList);
            objOut_str.close();
            fos_str.close();

        }catch (IOException exc){
            System.out.println(exc);
        }

    }

    public static void deserializeFromFile(){
        try{
            FileInputStream fIN_str = new FileInputStream("file.myFile");
            ObjectInputStream objINP_str = new ObjectInputStream(fIN_str);
            savePapirenList = (ArrayList) objINP_str.readObject();
            objINP_str.close();
            fIN_str.close();

            tableModel.setRowCount(0);
            for(int i = 0; i < savePapirenList.size(); i++){
                Papiren p = savePapirenList.get(i);
                String[] s = {p.getName(), p.getTicker(), p.getPrice(), p.getCap_price()};
                tableModel.addRow(s);
            }

        }catch (ClassNotFoundException nf_exception){
            System.out.println(nf_exception);
        }catch (IOException ex){
            System.out.println(ex);
        }

    }
}
