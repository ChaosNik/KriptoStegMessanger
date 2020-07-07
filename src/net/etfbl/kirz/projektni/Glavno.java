package net.etfbl.kirz.projektni;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
public class Glavno {
	public static List<Korisnik> korisnici = new ArrayList<>();
	Prijava frejm;
	Glavno glavno;
	public static void main(String[] args) throws IOException {
	
		Glavno glavno = new Glavno();
	}
	Glavno()
	{
		glavno = this;
		try {
			BufferedReader reader = new BufferedReader(new FileReader("korisnici"));
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				try {
					korisnici.add(new Korisnik(Integer.parseInt(line.split(";")[0]),line.split(";")[1],line.split(";")[2],line.split(";")[3],line.split(";")[4]));
				}catch(Exception e) {}
			}
			reader.close();
		}catch(Exception e) {}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					try {
						for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
							if (info.getName().equals("Nimbus")) {
								UIManager.setLookAndFeel(info.getClassName());
								UIDefaults defaults = UIManager.getLookAndFeelDefaults();
								defaults.put("Table.showGrid", true);
								defaults.put("ScrollBar.minimumThumbSize", new Dimension(50, 50));
								defaults.put("ScrollBar.opaque", true);
								defaults.put("ScrollBar.thumbHeight", 10);
								break;
							}
						}
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
						UIManager.getLookAndFeelDefaults().put("ScrollBar.minimumThumbSize", new Dimension(35, 35));
					} catch (Exception e) {
					}	
					frejm = new Prijava(glavno);
					frejm.setVisible(true);
				} catch (Exception e) {
					
				}
			}
		});
	}
}
