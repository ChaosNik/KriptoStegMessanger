package net.etfbl.kirz.projektni;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class Slika {
	private int ZAGLAVLJE = 54;
	private int SIRINA = 18;
	private int VISINA = 22;
	private int sirina, visina;
	private byte[] slika;
	private String uri;
	private int id;
	Slika(String uri, String type) throws IOException {
		this.uri = uri;
		otvori();
		setVisina();
		setSirina();
	}
	public void otvori() throws IOException {
		Path putanja = Paths.get(uri);
		slika = Files.readAllBytes(putanja);
	}
	public void spremi() throws IOException {
		Path putanja = Paths.get(uri);
		Files.write(putanja, slika);
	}
	public void setVisina() {
		if (slika == null)
			return;
		byte[] niz = new byte[4];
		niz[0] = slika[visina];
		niz[1] = slika[visina + 1];
		niz[2] = slika[visina + 2];
		niz[3] = slika[visina + 3];
		int visina = ByteBuffer.wrap(niz).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
		this.visina = visina;
	}
	public void setSirina() {
		if (slika == null)
			return;
		byte[] array = new byte[4];
		array[0] = slika[sirina];
		array[1] = slika[sirina + 1];
		array[2] = slika[sirina + 2];
		array[3] = slika[sirina + 3];
		int sirina = ByteBuffer.wrap(array).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
		this.sirina = sirina;
	}
	public int getId() {
		return ByteBuffer.wrap(citajBajtove(4, ZAGLAVLJE + 4 * 8)).getInt();
	}
	public void pisiTekst(byte[] kljuc, byte[] tekst, int id) {
		pisiBajte(ByteBuffer.allocate(4).putInt(tekst.length).array(), ZAGLAVLJE);
		pisiBajte(ByteBuffer.allocate(4).putInt(id).array(), ZAGLAVLJE + 4 * 8);
		pisiBajte(kljuc, ZAGLAVLJE + 2 * 4 * 8);
		pisiBajte(tekst, ZAGLAVLJE + 2 * 4 * 8 + 256 * 8);
	}
	public void pisiBajte(byte[] niz, int zaglavlje) {
		for (int i = 0; i < niz.length * 8; i++) {
			byte bit = -1;
			if ((niz[i / 8] & (byte) Math.pow(2, i % 8)) == 0) {
				bit = -2;
				slika[i + zaglavlje] = (byte) (slika[i + zaglavlje] & bit);
			} else {
				bit = 1;
				slika[i + zaglavlje] = (byte) (slika[i + zaglavlje] | bit);
			}
		}
	}
	public byte[] citajTekst() {
		int duzina = ByteBuffer.wrap(citajBajtove(4, ZAGLAVLJE)).getInt();
		int id = ByteBuffer.wrap(citajBajtove(4, ZAGLAVLJE + 4 * 8)).getInt();
		this.id = id;
		byte[] bajtovi = citajBajtove(duzina, ZAGLAVLJE + 2 * 4 * 8 + 256 * 8);
		return bajtovi;
	}
	public byte[] citajKljuc() {
		return citajBajtove(256, ZAGLAVLJE + 2 * 4 * 8);
	}
	public byte[] citajBajtove(int duzina, int zaglavlje) {
		byte[] niz = new byte[duzina];
		byte temp = 0;
		for (int i = 0; i < duzina * 8; i++) {
			byte rezultat = (byte) (slika[i + zaglavlje] & (byte)1);
			temp += rezultat * Math.pow(2, i % 8);
			if ((i % 8) == 7) {
				niz[i / 8] = temp;
				temp = 0;
			}
		}
		return niz;
	}
	public void brisi()
	{
		try {
			File fajl = new File(uri);
			fajl.delete();
		}catch (Exception e) {
		}
        }
	public int getPovrsina() {
		return sirina * visina;
	}
}
