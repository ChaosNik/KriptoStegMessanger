package net.etfbl.kirz.projektni;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
public class Poruka extends JFrame {
	private JPanel sadrzaj;
	private JTextField od;
	private JTextField datum;
	private Korisnik korisnik;
	private String lokacijaSlike;
	public Poruka(Korisnik korisnik, String lokacijaSlike, Prikaz korisnikPrikaz) {
		korisnikPrikaz.osvjezi();
		this.korisnik = korisnik;
		this.lokacijaSlike = lokacijaSlike;
		String poruka = "";
		String korisnikIme = "";
		String saljiDatumFormatiran = "";
		try {
			poruka = procitaj();
			int a = Integer.parseInt(poruka.split(";")[0]);
			for(Korisnik u : Glavno.korisnici)
			{
				if(u.getId() == a)
				{
					korisnikIme = u.getIme();
					break;
				}
			}
			long time = Long.parseLong(poruka.split(";")[1]);
			Date saljiDatum = new Date(time);
			DateFormat datumFormat = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");
			saljiDatumFormatiran = datumFormat.format(saljiDatum);
			poruka = poruka.split(";")[2];
		} catch (Exception e) {
			
		}
		setBounds(100, 100, 548, 396);
		sadrzaj = new JPanel();
		sadrzaj.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(sadrzaj);
		sadrzaj.setLayout(null);
		JLabel labelaForma = new JLabel("Od:");
		labelaForma.setBounds(12, 12, 70, 15);
		sadrzaj.add(labelaForma);
		JLabel labelaVrijeme = new JLabel("Vrijeme:");
		labelaVrijeme.setBounds(12, 51, 106, 15);
		sadrzaj.add(labelaVrijeme);
		JLabel labelaPoruka = new JLabel("Poruka:");
		labelaPoruka.setBounds(12, 78, 70, 15);
		sadrzaj.add(labelaPoruka);
		JTextArea tekstPoruka = new JTextArea();
		tekstPoruka.setEditable(false);
		tekstPoruka.setLineWrap(true);
		tekstPoruka.setWrapStyleWord(true);
		tekstPoruka.setText(poruka);
		JScrollPane skrolbar = new JScrollPane(tekstPoruka);
		skrolbar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		skrolbar.setBounds(12,93,516,258);
		sadrzaj.add(skrolbar);
		od = new JTextField();
		od.setEditable(false);
		od.setText(korisnikIme);
		od.setBounds(100, 0, 428, 29);
		sadrzaj.add(od);
		od.setColumns(10);
		datum = new JTextField();
		datum.setText(saljiDatumFormatiran);
		datum.setEditable(false);
		datum.setBounds(100, 41, 428, 27);
		sadrzaj.add(datum);
		datum.setColumns(10);
                Prikaz.obrisiPutanju(lokacijaSlike);
	}
	public String procitaj() throws Exception
	{
		Slika slika = new Slika(lokacijaSlike, "bmp");
		byte[] simetricniKljuc = slika.citajKljuc();
		byte[] poruka = slika.citajTekst();
		byte[] dekriptovaniSimetricniKljuc = dekriptujSimetricniKljuc(simetricniKljuc, getPrivatniKljuc());
		String dekriptovanaPoruka = dekriptujPoruku(poruka, new SecretKeySpec(Arrays.copyOf(dekriptovaniSimetricniKljuc, 24), "DESede"));
		slika.brisi();
		return dekriptovanaPoruka;
	}
	public RSAPrivateKey getPrivatniKljuc() throws Exception {
		File fajlPrivatnogKljuca = new File("kljucevi"+File.separator+korisnik.getKorisnickoIme()+".p8");
		DataInputStream ulaz;
		// citanje privatnog kljuca iz DER fajla
		ulaz = new DataInputStream(new FileInputStream(fajlPrivatnogKljuca));
		byte[] bajtoviPrivatnogKljuca = new byte[(int) fajlPrivatnogKljuca.length()];
		ulaz.read(bajtoviPrivatnogKljuca);
		ulaz.close();
		KeyFactory kljucevi = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec specifikacija = new PKCS8EncodedKeySpec(bajtoviPrivatnogKljuca);
		RSAPrivateKey privatniKljuc = (RSAPrivateKey) kljucevi.generatePrivate(specifikacija);
		return privatniKljuc;
	}
	private byte[] dekriptujSimetricniKljuc(byte[] simetricniKljuc, Key privatniKljuc) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		Cipher dekripter = Cipher.getInstance("RSA", "BC");
		dekripter.init(Cipher.DECRYPT_MODE, privatniKljuc);
		byte[] bajtovi = dekripter.doFinal(simetricniKljuc);
		return bajtovi;
	}
	private String dekriptujPoruku(byte[] kriptovano, Key simetricniKljuc) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		Cipher decrypter = Cipher.getInstance("DESede/ECB/PKCS7Padding", "BC");
		decrypter.init(Cipher.DECRYPT_MODE, simetricniKljuc);
		byte[] bajtovi = decrypter.doFinal(kriptovano);
		return new String(bajtovi);
	}
}
