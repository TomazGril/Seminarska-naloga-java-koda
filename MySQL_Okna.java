package Okna;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;

import com.mysql.cj.xdevapi.Schema.CreateCollectionOptions;

public class MySQL_Okna {
	
	static String url = "jdbc:mysql://localhost:3306/okna";
	
	public static String pravi_uporabnik="";
	
	static JFrame prijava=new JFrame("Prijava v sistem");
	static JPanel vsebina_prijave=new JPanel();
	static JLabel prijava_uporabnik_napis=new JLabel("Uporabnik:");
	static JLabel prijava_geslo_napis=new JLabel("Geslo: ");
	static JTextField prijava_uporabnik_vnos=new JTextField();
	static JPasswordField prijava_geslo_vnos=new JPasswordField();
	static JButton prijavi_se =new JButton("PRIJAVA");
	static JLabel nepravilno_u_ali_g=new JLabel("[Napaèno uporabniško ime ali geslo]");
	
	MySQL_Okna(){
	prijava.add(vsebina_prijave);
	prijava.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	prijava.setSize(300,220);
	
	prijava.setLocationRelativeTo(null);
	vsebina_prijave.setLayout(null);
	vsebina_prijave.add(prijava_uporabnik_napis);
	vsebina_prijave.add(prijava_geslo_napis);
	vsebina_prijave.add(prijava_uporabnik_vnos);
	vsebina_prijave.add(prijava_geslo_vnos);
	vsebina_prijave.add(prijavi_se);

	prijava_uporabnik_napis.setBounds(10,10,80,25);
	prijava_geslo_napis.setBounds(10,40,80,25);
	prijava_uporabnik_vnos.setBounds(90,10,120,25);
	prijava_geslo_vnos.setBounds(90,40,120,25);
	prijavi_se.setBounds(50,80,100,30);
	
	
	}
	

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		new MySQL_Okna();
		prijava.setVisible(true);
		
		prijavi_se.addActionListener(new ActionListener(){
			 @Override
			 public void actionPerformed(ActionEvent e) {
				
				String uporabnik=prijava_uporabnik_vnos.getText();
				String geslo="";
				char[] geslo_char=prijava_geslo_vnos.getPassword();
				for(int z=0;z<geslo_char.length;z++) {
					geslo+=geslo_char[z];
				}
				
				
				try {
					Connection povezava=DriverManager.getConnection(url, uporabnik, geslo);
					prijava.setVisible(false);
					new Generator_obrazca(povezava, uporabnik);
				}
				catch (Exception g) {
				 	nepravilno_u_ali_g.setBounds(20,120,250,25);
				 	vsebina_prijave.add(nepravilno_u_ali_g);
				 	prijava.repaint();
				 }
			 
				}
			 });
	}

//-------------------------------------------------------------------------------------------------	

	static void Posodobi_podatke(Connection povezava, String tabela, String podatki, String kljuè) {
		
		try {
			String ukaz_popravi="UPDATE "+tabela+" SET "+podatki+" WHERE "+kljuè;
			Zapisi_v_log(ukaz_popravi);
			
			PreparedStatement izjava=povezava.prepareStatement(ukaz_popravi);
			izjava.executeUpdate();
			}catch(SQLException e) {
				System.out.println("Napaka pri popravljanju - "+e);
			}
		
	}

	 static void Izbriši_podatke(Connection povezava, String tabela, String podatki) {
		
		try {
			String ukaz_izbriši="DELETE FROM "+tabela+" WHERE "+podatki;
			Zapisi_v_log(ukaz_izbriši);
			PreparedStatement izjava5=povezava.prepareStatement(ukaz_izbriši);
			izjava5.executeUpdate();
			}catch(SQLException e) {
				System.out.println("Napaka pri brisanju - "+e);
			}
	}
	
	
	static void Vstavi_podatke(Connection povezava, String tabela, String podatki)  {
		try {
		String ukaz_vstavi="INSERT INTO "+tabela+" VALUES("+podatki+");";
		System.out.println(ukaz_vstavi);
		Zapisi_v_log(ukaz_vstavi);
		PreparedStatement izjava=povezava.prepareStatement(ukaz_vstavi);
		izjava.executeUpdate();
		
		}catch(SQLException e) {
			System.out.println("Napaka pri vstavljanju - "+e);
		}
		
	}
//------------------------------------------------------------------------------------------------
	
	static boolean Naredi_tabelo(Connection povezava, String izraz) {
			
			try {
				PreparedStatement izjava2=povezava.prepareStatement(izraz);
				Zapisi_v_log(izraz);
				izjava2.executeUpdate();
				return true;
			}catch(SQLException e) {
				System.out.println("Napaka pri izdelavi tabele");
				return false;
			}
			
		}
	
	static boolean Zbriši_tabelo(Connection povezava, String tabela)  {
		
		try {
		String ukaz_izbriši="DROP TABLE "+tabela;
		Zapisi_v_log(ukaz_izbriši);
				PreparedStatement izjava3=povezava.prepareStatement(ukaz_izbriši);
				izjava3.executeUpdate();
				return true;
		}catch(SQLException e) {
			System.out.println("Napaka pri brisanju tabele");
				return false;
		}
	}
	//-------------------------------------------------------------------------------------------------	
	static  ArrayList<ArrayList<String>> Naredi_selekcijo(Connection povezava,String poizvedba1) {
		Zapisi_v_log(poizvedba1);
		ArrayList<ArrayList<String>> zaposleni_rows= new ArrayList<ArrayList<String>>();
		try {			
					
			//Pripravi poizvedbeno izjavo
			Statement izjava1=povezava.createStatement();
			ResultSet rezultat1=izjava1.executeQuery(poizvedba1);
			
					//Rezultat poizvedbe zapiši v arraylisto arrylistov
					while(rezultat1.next()) {
					ArrayList<String> zaposleni_columns= new ArrayList<String>();	
							try {
								int j=1;
								while(true) { //Zanalašè napaka !!!!
									
									zaposleni_columns.add(rezultat1.getString(j));
									
									j++;
								}
							}catch (Exception e) {
								System.out.println("Narejena je bila selekcija ene vrsitice -> Konec atributov");
							}
					zaposleni_rows.add(zaposleni_columns);
					
			}
			
			izjava1.close();
		
		}catch(SQLException e) {
			System.out.println("Napaka pri iskanju - napaka SQL");
		}
		return zaposleni_rows;
		
	}
		
	//--------------------------------------------------------------------------------------------------------------------------

	static ArrayList<String> Pridobi_atribute(Connection povezava, String tabela)  {
			
			ArrayList<String> ar=new ArrayList<String>();
			
			try {
				
			String atributi="show columns from okna."+tabela;
			Statement izjava=povezava.createStatement();
			ResultSet rezultat=izjava.executeQuery(atributi);
			
					while(rezultat.next()) {
						ar.add(rezultat.getString(1));
						}
			
			}catch(SQLException e) {
				System.out.println("Atributi niso bili najdeni");
			}
			
			return ar;
			
		}
	
	static int DobiVelikost(Connection povezava, String tabela, int id) {
		
		String niz="";
		
		try {
			
		String atributi="show columns from okna."+tabela;
		Statement izjava=povezava.createStatement();
		ResultSet rezultat=izjava.executeQuery(atributi);
		int i=0;
				while(rezultat.next()) {
					if(i==id)
					niz=rezultat.getString(2);
					i++;
					}
		
		}catch(SQLException e) {
			System.out.println("Velikosti niso bile najdene");
		}
		System.out.println(niz);
		if(niz.contains("(")) {
			niz=niz.substring(niz.indexOf("(")+1,niz.indexOf(")"));
		int velikost=Integer.parseInt(niz);
		return velikost;
		}
		else
		return 10;
		
		
	}
	
	 boolean JeTujiKljuè(Connection povezava,String tabela, int atribut) {
		 
		 try {
				
				String atributi="show columns from okna."+tabela;
				Statement izjava=povezava.createStatement();
				ResultSet rezultat=izjava.executeQuery(atributi);
				int i=0;
						while(rezultat.next()) {
							
							if(i==atribut && rezultat.getString(4).equals("MUL"))
								return true;
										
							i++;
							}
				
				}catch(SQLException e) {
					System.out.println("Napaka");
				}
				
		
		return false;
	 }
	 
	 
	static String DobiPRimarni(Connection povezava,String tabela){
		 
		 try {
				
				String atributi="show columns from okna."+tabela;
				Statement izjava=povezava.createStatement();
				ResultSet rezultat=izjava.executeQuery(atributi);
				
						while(rezultat.next()) {
							
							if(rezultat.getString(4).equals("PRI"))
								return rezultat.getString(1);
							}
				
				}catch(SQLException e) {
					System.out.println("Napaka");
				}
				
		
		return "";
		 
	 }
	 
	static String[] Poizvedba_moznosti(Connection povezava,String tabela, String atribut) {
		
		System.out.println(atribut);
		String atributi="SELECT TABLE_NAME,REFERENCED_TABLE_NAME,COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE REFERENCED_TABLE_SCHEMA = 'okna' AND REFERENCED_COLUMN_NAME = '"+atribut+"'";
		String izbrana="";
		try {
			System.out.println(atributi);
			Statement izjava = povezava.createStatement();
			ResultSet rezultat=izjava.executeQuery(atributi);
			
			while(rezultat.next()) {
					izbrana=rezultat.getString(2);
					break;
				}
		
		} catch (SQLException e) {
		System.out.println(e);	
		}
		try {
		System.out.println("Izbrana tabela: "+izbrana);
		ArrayList<ArrayList<String>> ar=Naredi_selekcijo(povezava, ("SELECT * FROM "+izbrana));
		
		String [] vse_mozn=new String[ar.size()+1];
		
		for(int i=0;i<ar.size();i++) {
			String vrstica="";
			for(int j=0;j<ar.get(i).size();j++) {
				
				vrstica+=ar.get(i).get(j)+"| ";
			}
			vse_mozn[i]=vrstica;
			System.out.println(vrstica);
		}
		 vse_mozn[ar.size()]="";
		 return vse_mozn;
		 }catch (Exception e) {
			System.out.println(e);
		}
		return null;
	 }
	 
//-------------------------------------------------------------------------------------------

	public static void Zapisi_v_log(String izraz) {
		
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter("LOG_dejavnosti.txt",true));
			LocalDateTime datum =LocalDateTime.now();
			System.out.println(izraz);
			bw.write(pravi_uporabnik +" (" +datum+ ") - "+izraz+" \n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public static Object pridobi_uporabnika() {
		
		return null;
	}

	
}

