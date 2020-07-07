package net.etfbl.kirz.projektni;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
public class Prikaz extends JFrame {
	private JPanel sadrzaj;
	private Korisnik korisnik;
	private JTable tabela;
	private String putanjaDoSlike;
	private JLabel labela;
	private Prikaz korisnikPrikaz;
	private List<String> poruke;
	private JPanel panelDesni;
	public Prikaz(Korisnik korisnik) {
		this.korisnikPrikaz = this;
		this.korisnik = korisnik;
		poruke = poruke();
		setBounds(150, 150, 600, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
                setLocationRelativeTo(null);
		sadrzaj = new JPanel();
                sadrzaj.setBackground(Color.CYAN);
		sadrzaj.setBorder(new EmptyBorder(5, 5, 5, 5));
		sadrzaj.setLayout(new BorderLayout(0, 0));
		setContentPane(sadrzaj);
		JSplitPane splitPane = new JSplitPane();
		sadrzaj.add(splitPane, BorderLayout.CENTER);
		panelDesni = new JPanel();
                panelDesni.setBackground(Color.CYAN);
		panelDesni.setMinimumSize(new Dimension(200, 100));
		splitPane.setRightComponent(panelDesni);
		panelDesni.setLayout(new BorderLayout(0, 0));
		JPanel panelLijevi = new JPanel();
                panelLijevi.setBackground(Color.CYAN);
		splitPane.setLeftComponent(panelLijevi);
		panelLijevi.setLayout(null);
		JComboBox korisnici = new JComboBox();
		korisnici.setBounds(50, 10, 200, 30);
		for(Korisnik u : Glavno.korisnici)
		{
			if(korisnik.equals(u))continue;
			korisnici.addItem(u.getIme());
		}
		panelLijevi.add(korisnici);
		JLabel lblZa = new JLabel("Za:");
		lblZa.setBounds(10, 20, 70, 15);
		panelLijevi.add(lblZa);
		JButton slika = new JButton("Slika");
		slika.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				izaberiSliku();
			}
		});
		slika.setBounds(10, 60, 90, 30);
		panelLijevi.add(slika);
		/*putanjaSlike = new JLabel("");
		putanjaSlike.setBounds(120, 70, 260, 15);
		panelLijevi.add(putanjaSlike);*/
		labela = new JLabel("Poruka:");
		labela.setBounds(10, 100, 70, 15);
		panelLijevi.add(labela);
		JTextArea poruka = new JTextArea();
		poruka.setLineWrap(true);
		poruka.setWrapStyleWord(true);
		poruka.setBounds(10, 120, 270, 200);
		panelLijevi.add(poruka);
		JButton posalji = new JButton("Posalji");
		posalji.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int izabrano = korisnici.getSelectedIndex();
				Korisnik temp = null;
				for(Korisnik kor : Glavno.korisnici)
				{
					if(kor.equals(korisnik))continue;
					if(izabrano == 0)
					{
						temp = kor;
						break;
					}
					if(!kor.equals(korisnik))izabrano--;
				}
				try{
					if(korisnik.posalji(temp, putanjaDoSlike, poruka.getText()) == 0)
						labela.setText("Poslato!");
					else
						labela.setText("Nije poslato!");
				}catch (Exception ex) {
					labela.setText("Dogodila se nepredvidjena greska!");
				}
			}
		});
                posalji.setBounds(160, 60, 90, 30);
		panelLijevi.add(posalji);
		osvjezi();
	}
	public void osvjezi()
	{
		String[] naziviKolona = {"Poruke"};
		Object[][] podaci = new Object[poruke.size()][1];
		int i = 0;
		for(String string : poruke)
		{
			podaci[i] = new String[1];
			podaci[i][0] = string;
			i++;
		}
		tabela = new JTable(podaci, naziviKolona){
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};
		tabela.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {
                            int dijalogDugme = JOptionPane.YES_NO_OPTION;
                            int dialogRezultat = JOptionPane.showConfirmDialog(korisnikPrikaz, "Otvori poruku", "", dijalogDugme);
                            if(dialogRezultat == 0) {
                                EventQueue.invokeLater(new Runnable() {
                                    public void run() {
                                        try {
                                            poruke();
                                            String temp=poruke.get(tabela.getSelectedRow());
                                            osvjezi();
                                            Poruka frejm = new Poruka(korisnik, temp, korisnikPrikaz);
                                            frejm.setBackground(Color.CYAN);
                                            frejm.setVisible(true);
                                        } catch (Exception e) {

                                        }
                                    }
                                });
                            } else {
                            }
			}
		});
		JScrollPane pozadina = new JScrollPane(tabela);
		pozadina.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                pozadina.setBackground(Color.CYAN);
		panelDesni.add(pozadina, BorderLayout.CENTER);
	}
	public static void obrisiPutanju(String putanja)
	{
            StringBuilder tekst = new StringBuilder();
            try {
                BufferedReader citac = new BufferedReader(new FileReader("poruke"));
                String linija = null;
                while ((linija = citac.readLine()) != null)
                    if(!putanja.startsWith(linija))
                        tekst.append(linija + System.lineSeparator());
                citac.close();
                FileWriter izlaz = new FileWriter(new File("poruke"));
                izlaz.write(tekst.toString());
                izlaz.close();
            }catch(Exception e) {}
	}
	private void izaberiSliku() {
        FileDialog dijalog = new FileDialog(this, "Izaberite sliku", FileDialog.LOAD);
		dijalog.setFilenameFilter(
				(dir, name) -> (name.endsWith(".bmp")));
		dijalog.show();
		String nazivFajla = dijalog.getFile();
		String putanjaFajla = dijalog.getDirectory();
		if (nazivFajla == null)
			return;
		putanjaDoSlike = putanjaFajla + nazivFajla;
	}
	private List<String> poruke()
	{
		List<String> poruke = new ArrayList<>();
		try {
			BufferedReader citac = new BufferedReader(new FileReader("poruke"));
			String linija = null;
                        boolean flag = checkAll();
                        if(!flag)JOptionPane.showMessageDialog(this, "KOMPROMITOVANI PODACI!!!");
			while ((linija = citac.readLine()) != null)
                            if(zaMene(linija, flag))
                                poruke.add(linija);
			citac.close();
		}catch(Exception e) {}
		poruke = poruke;
		return poruke;
	}
	private boolean zaMene(String putanja, boolean flag)
	{
		try {
			Slika slika = new Slika(putanja, "bmp");
			if(slika.getId() == korisnik.getId())return flag;/*return true*/;
		}catch(Exception e)
		{	
		}
		return false;
	}
        private boolean checkAll()
        {
            boolean flag = true;
            try {
                    BufferedReader citac = new BufferedReader(new FileReader("poruke"));
                    String linija = null;
                    while ((linija = citac.readLine()) != null)
                    {
                        File f = new File(linija);
                        flag = flag & f.exists();
                    }
                    citac.close();
            }catch(Exception e) {}
            return flag;
        }
}
