package net.etfbl.kirz.projektni;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.Key;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
public class Korisnik {
	private String putanjaCertifikata;
	private String korisnickoIme;
	private String lozinka;
	private int id;
	private X509Certificate certifikat;
	private boolean validan = false;
        private String ime;
	public Korisnik(int id, String ime, String korisnickoIme, String lozinka, String putanjaCertifikata) {
		super();
		this.putanjaCertifikata = putanjaCertifikata;
		this.korisnickoIme = korisnickoIme;
		this.lozinka = lozinka;
		this.id = id;
		this.ime = ime;
		ucitajCertifikat(this);
	}
	public void ucitajCertifikat(Korisnik korisnik) {
		try {
			CertificateFactory fact = CertificateFactory.getInstance("X.509");
			FileInputStream ulaz = new FileInputStream("certifikati/milan.pem");
			korisnik.certifikat = (X509Certificate) fact.generateCertificate(ulaz);
			Date datum = new Date();
			if (datum.after(certifikat.getNotBefore()) && datum.before(certifikat.getNotAfter())) {
				korisnik.validan = true;
			}
		} catch (Exception e) {
		}
	}
	public int posalji(Korisnik korisnik, String putanjaDoSlike, String poruka) throws Exception {
		ucitajCertifikat(korisnik);
		Slika slika = new Slika(putanjaDoSlike, "bmp");
		Security.addProvider(new BouncyCastleProvider());
		byte[] bajtoviKljuca = new byte[24];
		Key simetricniKljuc = new SecretKeySpec(bajtoviKljuca, "DESede");
		if(!korisnik.validan || korisnik.certifikat == null) return 2;
		PublicKey javniKljuc = korisnik.certifikat.getPublicKey();
		byte[] kriptovaniKljuc = enkriptujSimetricniKljuc(bajtoviKljuca, javniKljuc);
		Date datum = new Date();
		poruka = id + ";" + datum.getTime() + ";" + poruka;
		byte[] kriptovanaPoruka = enkriptujPoruku(poruka, simetricniKljuc);
                if(slika.getPovrsina() < (kriptovanaPoruka.length+256) * 8)
                    slika.pisiTekst(kriptovaniKljuc, kriptovanaPoruka, korisnik.getId());
		slika.spremi();
		slika(putanjaDoSlike);
		return 0;
	}
	private void slika(String putanjaDoSlike)
	{
		try {
			BufferedReader citac = new BufferedReader(new FileReader("poruke"));
			String linija = null;
			while ((linija = citac.readLine()) != null)
			{
				if(linija.equals(putanjaDoSlike))
				{
					citac.close();
					return;
				}
			}
			citac.close();
			putanjaDoSlike = putanjaDoSlike+System.lineSeparator();
			Files.write(Paths.get("poruke"), putanjaDoSlike.getBytes(), StandardOpenOption.APPEND);
		}catch(Exception e) {}
	}
	private byte[] enkriptujSimetricniKljuc(byte[] simetricniKljuc, Key javniKljuc) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		Cipher enkripter = Cipher.getInstance("RSA", "BC");
		enkripter.init(Cipher.ENCRYPT_MODE, javniKljuc);
		byte[] rezultat = enkripter.doFinal(simetricniKljuc);
		return rezultat;
	}
	private byte[] enkriptujPoruku(String poruka, Key simetricniKljuc) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		Cipher enkripter = Cipher.getInstance("DESede/ECB/PKCS7Padding", "BC");
		enkripter.init(Cipher.ENCRYPT_MODE, simetricniKljuc);
		byte[] rezultat = enkripter.doFinal(poruka.getBytes());
		return rezultat;
	}
	public String getIme() {
		return ime;
	}
	public String getKorisnickoIme() {
		return korisnickoIme;
	}
	public String getLozinka() {
		return lozinka;
	}
	public int getId() {
		return id;
	}
	@Override
	public boolean equals(Object obj) {
		return id == ((Korisnik) obj).id;
	}
}
