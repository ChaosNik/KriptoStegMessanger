package net.etfbl.kirz.projektni;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
public class Prijava extends JFrame {
	private JPanel sadrzaj;
	private JTextField korisnickoIme;
	private JPasswordField lozinka;
	private Glavno glavno;
	private JFrame frejm;
	public Prijava(Glavno glavno) {
		frejm = this;
		this.glavno = glavno;
		setBounds(0, 0, 250, 170);
                setLocationRelativeTo(null);
		sadrzaj = new JPanel();
                sadrzaj.setBackground(Color.CYAN);
		sadrzaj.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(sadrzaj);
		sadrzaj.setLayout(null);
		JLabel labelaKorisnickoIme = new JLabel("Korisnicko ime:");
		labelaKorisnickoIme.setBounds(10, 10, 100, 25);
		sadrzaj.add(labelaKorisnickoIme);
		korisnickoIme = new JTextField();
		korisnickoIme.setToolTipText("Korisnicko ime");
		korisnickoIme.setBounds(130, 10, 100, 25);
		sadrzaj.add(korisnickoIme);
		korisnickoIme.setColumns(10);
		JLabel labelaLozinka = new JLabel("Lozinka:");
		labelaLozinka.setBounds(10, 50, 100, 25);
		sadrzaj.add(labelaLozinka);
		lozinka = new JPasswordField();
		lozinka.setBounds(130, 50, 100, 25);
		sadrzaj.add(lozinka);
		JButton dugmePrijava = new JButton("Prijava");
		dugmePrijava.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String korIme = korisnickoIme.getText();
				String loz = getMd5Hes(lozinka.getText());
				boolean nadjen = false;
				for(Korisnik u : glavno.korisnici)
				{
					if(u.getKorisnickoIme().equals(korIme) && u.getLozinka().equals(loz))
					{
						nadjen = true;
						frejm.setVisible(false);
						
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								try {
									Prikaz frejm = new Prikaz(u);
									frejm.setVisible(true);
								} catch (Exception e) {
									
								}
							}
						});
					}
				}
				if(!nadjen)
                                {
					korisnickoIme.setText("Netacno");
                                        lozinka.setText("");
                                }
			}
		});
		dugmePrijava.setBounds(35, 85, 150, 35);
		sadrzaj.add(dugmePrijava);
	}
	public String getMd5Hes(String tekst)
	{
            try {
	        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
	        byte[] array = md.digest(tekst.getBytes());
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < array.length; ++i) {
	          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
	       }
	        return sb.toString();
	    } catch (java.security.NoSuchAlgorithmException e) {
	    }
	    return null;
	}
}
