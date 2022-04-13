package Okna;

import javax.print.attribute.AttributeSet;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import com.mysql.cj.x.protobuf.MysqlxCrud.DropView;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import java.awt.*;

class Omeji_velikost extends DocumentFilter {
	int velikost;
	public Omeji_velikost(int i) {
	 this.velikost=i;
	}

   public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException  
    {
        if(fb.getDocument().getLength()+string.length()>velikost)
        {
            return;
        }
        fb.insertString(offset, string, (javax.swing.text.AttributeSet) attr);

    }  

   @Override  
   public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException 
   {  
       fb.remove(offset, length);
   }
    
    public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs)throws BadLocationException 
    {  
             if(fb.getDocument().getLength()+text.length()>velikost)
             {
                System.out.println("OK");
                return;
            }

            fb.insertString(offset, text, attrs);
    }
}


 class Generator_obrazca extends MySQL_Okna  {
	 
		 
	 static JTable tabela_obdelava;
	 static JTable tabela_išèi;
	 static int index=0;
	 static String velikost="";
	
	 //Deklaracije glavnega okna in vseh panel
	 
	 JFrame okna_glavni_okvir= new JFrame("Okna");
	 JPanel vsebina= new JPanel();
	 JPanel glavni_meni=new JPanel();
	 
	 JPanel obdelujteelemente=new JPanel();
	 JPanel obdelujteelemente_zgoraj= new JPanel();
	 JPanel obdelujteelemente_spodaj= new JPanel();
	 JScrollPane obdelujteelemente_levo= new JScrollPane();
	 JPanel obdelujteelemente_levo_vsebina=new JPanel();
	 JPanel obdelujteelemente_desno= new JPanel();
	 
	 
	 JPanel išèiteelemente= new JPanel();
	 JPanel išèiteelemente_zgoraj= new JPanel();
	 JPanel išèiteelemente_spodaj= new JPanel();
	 JScrollPane išèiteelemente_sredina= new JScrollPane();
	 JPanel išèiteelemente_sredina_vsebina=new JPanel();
	 JPanel išèiteelemente_desno= new JPanel();
	 JPanel išèiteelemente_levo= new JPanel();
	 
	 
	 JPanel upravljajzbazo= new JPanel();
	 JSplitPane ustvarjanje_tabel=new JSplitPane();
	 JPanel ustvarjanje_tabele_zgoraj= new JPanel();
	 JPanel ustvarjanje_tabele_spodaj_vsebina= new JPanel();
	 JPanel ustvarjanje_tabele_spodaj=new JPanel();
	 JScrollPane ustvarjanje_tabele_scroll=new JScrollPane();
	 
	 
	 static boolean išèi1_pogoj=false;
	 static boolean išèi2_pogoj=false;
	 static String izraz;
	 

	 // Glavni glavni_meni
	 JLabel meninapis1= new JLabel("Dobrodošli v aplikaciji za delo z bazo Okna");
	 JLabel meninapis2= new JLabel("Izberite eno izmed naslednjih možnosti:");
	 
	 JButton gumb_v_obdelavo=new JButton("Obdelava podatkov");
	 JButton gumb_v_iskanje= new JButton("Iskanje elementov");
	 JButton gumb_v_upravljanje= new JButton("Upravljanje z bazo");
	 
	 
	 
	 // Ustvarjanje tabel
	 int stevilo_vrstic=0;
	 JLabel oznakaatributa;
	 JTextField ime_at;
	 String[] tipi_atributov={"Integer","Decimal","Char","Text","Varchar","Date","BOOLEAN"};
	 JComboBox<Object> tip_at;
	 JTextField vel_at;
	 JCheckBox AI_at;
	 JCheckBox PK_at;
	 JCheckBox NN_at;
	 JCheckBox PFK_at;
	 JComboBox<Object> reference_tabela;
	 
	 ArrayList<JLabel> ar_oznaka_at=new ArrayList<JLabel>();
	 ArrayList<JTextField> ar_ime_at=new ArrayList<JTextField>();
	 ArrayList<JComboBox> ar_tip_at=new ArrayList<JComboBox>();
	 ArrayList<JTextField> ar_vel_at=new ArrayList<JTextField>();
	 ArrayList<JCheckBox> ar_AI_at=new ArrayList<JCheckBox>();
	 ArrayList<JCheckBox> ar_PK_at=new ArrayList<JCheckBox>();
	 ArrayList<JCheckBox> ar_NN_at=new ArrayList<JCheckBox>();
	 ArrayList<JCheckBox> ar_PFK_at=new ArrayList<JCheckBox>();
	 ArrayList<JComboBox> ar_reference_tabela=new ArrayList<JComboBox>();
	 
	 JLabel uspensno_ust_tabele=new JLabel("(Tabela je bila ustvarjena)");
	 JLabel uspensno_zbr_tabele=new JLabel("(Tabela je bila zbrisana)");
	 JLabel napaka_ust_tabel=new JLabel("(Tabela ni bila ustvajenja - napaka)");
	 JLabel napaka_zbr_tabel=new JLabel("(Tabela ni bila izbrisana - napaka)");
	 
	 ArrayList<Integer> tujikljuc=new ArrayList<Integer>();
	 
	
	 // Meni izbrisa in kreiranje tabel
	JLabel ustvari_zbrisi= new JLabel("Izberite tabelo, ki jo želite izbrisati ali pa pojdite na ustvarjanje tabel");
	JButton gumb_v_ustvarjanje_tabel= new JButton("Ustvari tabelo");
	JButton gumb_zbrisi_tabelo= new JButton("Zbriši tabelo");
	 

	 JButton gumb_nazaj_iz_K_IN_B_tabel=new JButton("Nazaj na upravljanje z bazami");
	 JButton dodaj_vrstico=new JButton("Dodaj vrstico");
	 JButton zbriši_vrstico=new JButton("Zbriši vrstico");
	 JButton ustvari_tabelo=new JButton("Dodaj tabelo");
	 JLabel ustvari= new JLabel("Dodajte atribute in ime tabele:");
	 
	 JFrame prijava_ad= new JFrame("Prijava administratorja");
	 JPanel prijava_ad_vsebina= new JPanel();
	 JLabel geslo_ad= new JLabel("Geslo:");
	 JPasswordField geslo_vnos_ad= new JPasswordField();
	 JButton gumb_prijava_ad=new JButton("Prijava");
	 JLabel geslo_odgovor_ad= new JLabel("[Napaèno geslo!]");
	 
	 
	 
	 JLabel dodaj_izbira= new JLabel("Izberite tabelo v katero hoèete vstaviti, zbrisati ali posodobiti podatke!");
	 JLabel izberi_iskanje= new JLabel("Izberite nad katerimi tabelami želite izvesti iskanje! :");
	 
	 //Delo s podatki v tabeli
	 
	 ArrayList<JLabel> ar_napisi=new ArrayList<JLabel>();
	 ArrayList<JTextField> ar_vnosi=new ArrayList<JTextField>();
	 ArrayList<JComboBox> ar_vnosi_podani=new ArrayList<JComboBox>();
	 static JComboBox<Object> obrazec_podan;
	 static JTextField obrazec;
	 static JLabel atribut;
	 JButton gumbvnesi_v_tabelo= new JButton("Vnesi v tabelo");
	 JButton osveži= new JButton("Osveži tabelo"); 
	 JButton Vpelji_iskalne_kriterije= new JButton("Išèi po tabeli"); 
	 
	 JButton gumbizbriši_tabelo= new JButton("Izbriši iz tabele");
	 	 
	 JButton navodila_DML=new JButton("Pomoè ?");
	 JButton navodila_DDL=new JButton("Pomoè ?");
	 JButton navodila_DQL=new JButton("Pomoè ?");
	 	
	 
	 JButton gumbposodobi_tabelo= new JButton("Posodobi tabelo");
	 JLabel id_kljuèa=new JLabel("ID starega kljuèa");
	 JTextField id_kljuèa_vnos=new JTextField();
	 
	 JButton gumb_išèi1= new JButton("Vsi podatki o naroèilih");
	 JButton gumb_išèi2= new JButton("Vsi podatki o zaposlenih");
	 
			 
	 JButton gumb_nazaj_na_meni_1=new JButton("Nazaj na glavni_meni");
	 JButton gumb_nazaj_na_meni_2=new JButton("Nazaj na glavni_meni");
	 JButton gumb_nazaj_na_meni_3=new JButton("Nazaj na glavni_meni");
	
	
	
	 static JComboBox<Object> seznam_tabel1;
	 static JComboBox<Object> seznam_tabel2;
	 static JComboBox<Object> seznam_tabel3;
	 static String izbrana_tabela;
	 
	 //Za premikanje med panelami
	 CardLayout cl = new CardLayout();
	 
	 DefaultTableModel modeltabele = new DefaultTableModel();
	 
	 @SuppressWarnings("deprecation")
	 
	public Generator_obrazca(Connection povezava, String uporabnik) throws SQLException {

		
					 pravi_uporabnik=uporabnik;
								vsebina.setLayout(cl);
								
								 glavni_meni.add(meninapis1);
								 glavni_meni.add(meninapis2);
								 glavni_meni.add(gumb_v_obdelavo);
								 glavni_meni.add(gumb_v_iskanje);
								 glavni_meni.add(gumb_v_upravljanje);
								
								 meninapis1.setBounds(250,55,1000,60);
								 meninapis1.setFont(new Font("Calibri", Font.BOLD,55));
								 meninapis2.setBounds(300,140,850,25);
								 meninapis2.setFont(new Font("Times New Roman", Font.BOLD,22));
								 gumb_v_obdelavo.setBounds(310,220,800,50);
								 gumb_v_obdelavo.setFont(new Font("Courier",Font.PLAIN ,20));
								 gumb_v_iskanje.setBounds(310,300,800,55);
								 gumb_v_iskanje.setFont(new Font("Courier", Font.PLAIN,20));
								 gumb_v_upravljanje.setBounds(310,380,800,55);
								 gumb_v_upravljanje.setFont(new Font("Courier", Font.PLAIN,20));
								 
								 ArrayList<String> ar=new ArrayList<String>(); 
									 ar = poišèi_vse_tabele(ar,povezava);
								
								 	 
								
								 Object[] tabela_tabel=ar.toArray();
								 seznam_tabel1=new JComboBox<Object>(tabela_tabel);
								 seznam_tabel2=new JComboBox<Object>(tabela_tabel);
								 seznam_tabel3=new JComboBox<Object>(tabela_tabel);
								 
								 
								 //---------------------------------------------------------------------------------------------------------
								 //Panela obdeluj elemente
								 //---------------------------------------------------------------------------------------------------------
								
								 
								obdelujteelemente.setLayout(new GridBagLayout());
								 GridBagConstraints gbc =new GridBagConstraints();
								 obdelujteelemente_desno.setLayout(new GridBagLayout());
								 obdelujteelemente_levo.setViewportView(obdelujteelemente_levo_vsebina);
								 obdelujteelemente_levo.setSize(new Dimension(800,400));
								 
								 obdelujteelemente_zgoraj.setLayout(new GridBagLayout());
								 obdelujteelemente_spodaj.setLayout(new GridBagLayout());
								 obdelujteelemente_levo_vsebina.setLayout(new GridBagLayout());
								 obdelujteelemente_zgoraj.setBorder(BorderFactory.createLineBorder(Color.black));
								 
								 gbc.gridx=0;
								 gbc.gridy=0;
								 gbc.gridwidth=2;
								 obdelujteelemente.add(obdelujteelemente_zgoraj,gbc);
								 gbc.gridx=0;
								 gbc.gridy=2;
								 gbc.gridwidth=2;
								 obdelujteelemente.add(obdelujteelemente_spodaj,gbc);
								 gbc.gridx=0;
								 gbc.gridy=1;
								 gbc.gridwidth=1;
								 obdelujteelemente.add(obdelujteelemente_levo,gbc);
								 gbc.gridx=1;
								 gbc.gridy=1;
								 obdelujteelemente.add(obdelujteelemente_desno,gbc);
								 
								 
								 gbc.gridx =3;
								 gbc.gridy =0;
								 gbc.insets = new Insets(25,25,50,25);
								 obdelujteelemente_zgoraj.add(gumb_nazaj_na_meni_1,gbc);			 
						
								 gbc.gridx =0;
								 gbc.gridy =0;
								
								 obdelujteelemente_zgoraj.add(dodaj_izbira,gbc);
								 dodaj_izbira.setFont(new Font("Calibri", Font.BOLD,18));
								 
								
								 gbc.gridwidth=1;
								 gbc.gridx =2;
								 gbc.gridy =0;
								 obdelujteelemente_zgoraj.add(seznam_tabel1,gbc);
								 
								 tabela_obdelava = new JTable(modeltabele) {
										@Override
									    public boolean isCellEditable(int row, int column) {
								       return false;
										}
										    @Override
										    public Dimension getPreferredScrollableViewportSize()
										    {
										        return new Dimension(600, 250);
										    }
						
								    };
								   
								    gbc.insets = new Insets(100,100,150,30);
								    gbc.weighty=1;
								    gbc.weightx=1;
								    gbc.gridwidth=1;
								    gbc.gridheight=1;
									gbc.gridx =0;
									gbc.gridy =0;
									
									JScrollPane panela_obdeluj=new JScrollPane(tabela_obdelava);
									obdelujteelemente_desno.add(panela_obdeluj,gbc);
								    panela_obdeluj.setMinimumSize( panela_obdeluj.getPreferredSize() );
								    
								    obdelujteelemente_desno.revalidate();
								    obdelujteelemente_desno.repaint();
								    
								    
									 
									osveži.addActionListener(new ActionListener() {
										 public void actionPerformed(ActionEvent e) {
											 seznam_tabel1.actionPerformed(e);
										 }
									 });
								
									 
								 seznam_tabel1.addActionListener(new ActionListener(){
									 @Override
									 public void actionPerformed(ActionEvent e) {
									 
										 if(e.getSource()==seznam_tabel1) {
											 
											 obdelujteelemente.remove(id_kljuèa);
											 obdelujteelemente.remove(id_kljuèa_vnos);
											 
											 izbrana_tabela=(String) seznam_tabel1.getSelectedItem(); 
											 ArrayList <String> ar_stolpci=Pridobi_atribute(povezava, izbrana_tabela);
											 
											 Nariši_obrazec(ar_napisi,ar_vnosi,ar_vnosi_podani,obrazec_podan,obrazec,atribut,obdelujteelemente_levo_vsebina, ar_stolpci, gbc, 0, 2,true,izbrana_tabela,false,povezava);
											 Nariši_tabelo(povezava, izbrana_tabela, obdelujteelemente_desno, tabela_obdelava);
											 gbc.insets = new Insets(0,10,0,0);
											 gbc.weighty=0.1;
											 gbc.weightx=0.1;
											 gbc.gridx =0;
											 gbc.gridy =1;
											 obdelujteelemente_levo_vsebina.add(id_kljuèa,gbc);
											 gbc.insets = new Insets(10,10,0,0);
											 gbc.gridx =1;
											 gbc.gridy =1;
											 obdelujteelemente_levo_vsebina.add(id_kljuèa_vnos,gbc);
											 
										 }
									 }
								 });
								 
								 gbc.insets = new Insets(0,0,15,350);
								 gbc.gridx =0;
								 gbc.gridy=0;
								 obdelujteelemente_spodaj.add(gumbvnesi_v_tabelo,gbc);
								 gbc.insets = new Insets(0,50,15,120);
								 gbc.gridx=0;
								 obdelujteelemente_spodaj.add(gumbizbriši_tabelo,gbc);
								 gbc.insets = new Insets(0,220,15,0);
								 gbc.gridx=0;
								 obdelujteelemente_spodaj.add(gumbposodobi_tabelo,gbc);
								 gbc.gridx=2;
								 obdelujteelemente_spodaj.add(osveži,gbc);
								 
								 gbc.insets = new Insets(50,0,0,650);
								 gbc.gridx =0;
								 gbc.gridy=1;
								 obdelujteelemente_spodaj.add(navodila_DML,gbc);
													 
								 
								 gumbvnesi_v_tabelo.addActionListener(new ActionListener() {
									 public void actionPerformed(ActionEvent e) {
										 
										String Podatki=""; 
										Boolean prvi_element=true;
										 for(int i=0; i < ar_vnosi.size(); i++) {
											 try {
												
												 if(tujikljuc.contains(i)==true) {
													
													 if(i!=(ar_vnosi_podani.size()-1) || prvi_element==true) {
														 if(!preveri_èe_je_število(i, povezava, izbrana_tabela)) 
															 Podatki+="'"+PridobiID(ar_vnosi_podani.get(i))+"',";
														 else
															 if(preveri_èe_je_AI(i, povezava, izbrana_tabela)==true && PridobiID(ar_vnosi_podani.get(i)).isBlank()) 
																 Podatki+="null,";
															 else 
															 Podatki+=PridobiID(ar_vnosi_podani.get(i))+",";
													 }
													 else {
														 if(!preveri_èe_je_število(i, povezava, izbrana_tabela)) 
															 Podatki+="'"+PridobiID(ar_vnosi_podani.get(i))+"'";
														 else
															 if(preveri_èe_je_AI(i, povezava, izbrana_tabela)==true && PridobiID(ar_vnosi_podani.get(i)).isBlank()) 
																 Podatki+="null";
															 else 
															 Podatki+=PridobiID(ar_vnosi_podani.get(i));
													 }
													
												 }else {
												 
														 if(i!=(ar_vnosi.size()-1) || prvi_element==true) {
															 if(!preveri_èe_je_število(i, povezava, izbrana_tabela)) 
																 Podatki+="'"+ar_vnosi.get(i).getText()+"',";
															 else
																 if(preveri_èe_je_AI(i, povezava, izbrana_tabela)==true && ar_vnosi.get(i).getText().isBlank()) 
																	 Podatki+="null,";
																  else 
																 Podatki+=ar_vnosi.get(i).getText()+",";
														 }
														 else {
															 if(!preveri_èe_je_število(i, povezava, izbrana_tabela)) 
																 Podatki+="'"+ar_vnosi.get(i).getText()+"'";
															 else
																 if(preveri_èe_je_AI(i, povezava, izbrana_tabela)==true && ar_vnosi.get(i).getText().isBlank()) 
																	 Podatki+="null";
																 else 
																 Podatki+=ar_vnosi.get(i).getText();
														 }
														 
												}
												 
												 }catch(Exception f) {
													 System.out.println(f);
												 }
												 prvi_element=false;
											 }
											 
										 
										 System.out.println(Podatki);
										 Vstavi_podatke(povezava, izbrana_tabela, Podatki);
										 seznam_tabel1.actionPerformed(e);
									 }

									 
								  });
								 
								 
								 gumbizbriši_tabelo.addActionListener(new ActionListener() {
									 public void actionPerformed(ActionEvent e) {
										 
										String Podatki=""; 
										Boolean prvi_element=true;
										 for(int i=0; i < ar_vnosi.size(); i++) {
											 try {
												 
												 if(tujikljuc.contains(i)==true) {
													 
													 if((ar_vnosi_podani.get(i).getSelectedItem())=="")	 {
															continue;
														}
														 	 
														 if(i!=0 && prvi_element==false) {
															 if(!preveri_èe_je_število(i, povezava, izbrana_tabela))
																 Podatki+=" , "+ar_napisi.get(i).getText()+" = '"+PridobiID(ar_vnosi_podani.get(i))+"' ";
															 else
																 Podatki+=" , "+ar_napisi.get(i).getText()+" = "+PridobiID(ar_vnosi_podani.get(i))+" ";
														 }
														 else {
															 if(!preveri_èe_je_število(i, povezava, izbrana_tabela))
																 Podatki+=""+ar_napisi.get(i).getText()+" = '"+PridobiID(ar_vnosi_podani.get(i))+"' ";
															 else
																 Podatki+=ar_napisi.get(i).getText()+" = "+PridobiID(ar_vnosi_podani.get(i))+" ";
														 }
													 }
												 else {
									
															if(ar_vnosi.get(i).getText().isEmpty()==true)	 {
																continue;
															}
															 	 
															 if(i!=0 && prvi_element==false) {
																 if(!preveri_èe_je_število(i, povezava, izbrana_tabela))
																	 Podatki+=" , "+ar_napisi.get(i).getText()+" = '"+ar_vnosi.get(i).getText()+"' ";
																 else
																	 Podatki+=" , "+ar_napisi.get(i).getText()+" = "+ar_vnosi.get(i).getText()+" , ";
															 }
															 else {
																 if(!preveri_èe_je_število(i, povezava, izbrana_tabela))
																	 Podatki+=""+ar_napisi.get(i).getText()+" = '"+ar_vnosi.get(i).getText()+"' ";
																 else
																	 Podatki+=ar_napisi.get(i).getText()+" = "+ar_vnosi.get(i).getText()+" ";
															 }
											 }
											 
											 }catch(Exception f) {
												 System.out.println(f);
											 }
											 prvi_element=false;
										 }
										 System.out.println(Podatki);
										 Izbriši_podatke(povezava, izbrana_tabela, Podatki);
										 seznam_tabel1.actionPerformed(e);
									 }
								  });
								 
								 gumbposodobi_tabelo.addActionListener(new ActionListener() {
									 public void actionPerformed(ActionEvent e) {
										 
										String Podatki=""; 
										Boolean prvi_element=true;
										
										 for(int i=0; i < ar_vnosi.size(); i++) {
											 try {
												 
												 if(tujikljuc.contains(i)==true) {
													 
													 if(ar_vnosi_podani.get(i).getSelectedItem()=="")	 {
															continue;
														}
														 	 
														 if(i!=0 && prvi_element==false) {
															 if(!preveri_èe_je_število(i, povezava, izbrana_tabela))
																 Podatki+=" , "+ar_napisi.get(i).getText()+" = '"+PridobiID(ar_vnosi_podani.get(i))+"' ";
															 else
																 Podatki+=" , "+ar_napisi.get(i).getText()+" = "+PridobiID(ar_vnosi_podani.get(i))+" , ";
														 }
														 else {
															 if(!preveri_èe_je_število(i, povezava, izbrana_tabela))
																 Podatki+=""+ar_napisi.get(i).getText()+" = '"+PridobiID(ar_vnosi_podani.get(i))+"' ";
															 else
																 Podatki+=ar_napisi.get(i).getText()+" = "+PridobiID(ar_vnosi_podani.get(i))+" ";
														 }
													 
													 
												 }else {
												 
														if(ar_vnosi.get(i).getText().isEmpty()==true)	 {
															continue;
														}
														 	 
														 if(i!=0 && prvi_element==false) {
															 if(!preveri_èe_je_število(i, povezava, izbrana_tabela))
																 Podatki+=", "+ar_napisi.get(i).getText()+" = '"+ar_vnosi.get(i).getText()+"' ";
															 else
																 Podatki+=", "+ar_napisi.get(i).getText()+" = "+ar_vnosi.get(i).getText()+" ";
														 }
														 else {
															 if(!preveri_èe_je_število(i, povezava, izbrana_tabela))
																 Podatki+=ar_napisi.get(i).getText()+" = '"+ar_vnosi.get(i).getText()+"' ";
															 else
																 Podatki+=ar_napisi.get(i).getText()+" = "+ar_vnosi.get(i).getText()+" ";
														 }
												 }
											 }catch(Exception f) {
												 System.out.println(f);
											 }
											 prvi_element=false;
										 }
										 System.out.println(Podatki);
										 if(!id_kljuèa_vnos.getText().isEmpty()) {
										 String Kljuè=DobiPRimarni(povezava, izbrana_tabela)+" = '"+id_kljuèa_vnos.getText()+"'";
										 Posodobi_podatke(povezava, izbrana_tabela, Podatki, Kljuè);
										 }else {
											 String Kljuè=DobiPRimarni(povezava, izbrana_tabela)+" = '"+ar_vnosi.get(0).getText()+"'";
											 Posodobi_podatke(povezava, izbrana_tabela, Podatki, Kljuè);
										 }
										 seznam_tabel1.actionPerformed(e);
									 }
								  });
								 
								//---------------------------------------------------------------------------------------
								//Išèi po bazi
								//---------------------------------------------------------------------------------------
								
								gbc.fill=GridBagConstraints.NONE;
								išèiteelemente.setLayout(new BorderLayout(25,25));
								
								išèiteelemente_desno.setLayout(new GridBagLayout());
								išèiteelemente_levo.setLayout(new GridBagLayout());
								išèiteelemente_zgoraj.setLayout(new GridBagLayout());
								išèiteelemente_spodaj.setLayout(new GridBagLayout());
								išèiteelemente_zgoraj.setBorder(BorderFactory.createLineBorder(Color.black));
								
								išèiteelemente_sredina.setViewportView(išèiteelemente_sredina_vsebina);
								išèiteelemente_sredina.setMinimumSize(išèiteelemente_sredina_vsebina.getPreferredSize() );
								išèiteelemente_sredina_vsebina.setLayout(new GridBagLayout());
															
								
								 išèiteelemente.add(išèiteelemente_zgoraj,BorderLayout.PAGE_START);
								 išèiteelemente.add(išèiteelemente_spodaj,BorderLayout.PAGE_END);
								 išèiteelemente.add(išèiteelemente_levo,BorderLayout.LINE_START);
								 išèiteelemente.add(išèiteelemente_sredina,BorderLayout.CENTER);
								 išèiteelemente.add(išèiteelemente_desno,BorderLayout.LINE_END);
								 
								gbc.gridwidth=1;
								gbc.gridheight=1;
								gbc.gridx =0;
								gbc.gridy =0;
								
								gbc.insets = new Insets(25,25,0,25);
								išèiteelemente_zgoraj.add(izberi_iskanje,gbc);
								izberi_iskanje.setFont(new Font("Calibri", Font.BOLD,18));
								
								 gbc.gridx =4;
								 gbc.gridy =0;
								 išèiteelemente_zgoraj.add(gumb_nazaj_na_meni_2,gbc);
								 
								 gbc.fill=GridBagConstraints.HORIZONTAL;
								 gbc.gridx =0;
								 gbc.gridy =0;
								 išèiteelemente_levo.add(gumb_išèi1,gbc);
								 gbc.gridx =0;
								 gbc.gridy =1;
								 išèiteelemente_levo.add(gumb_išèi2,gbc);
								 
								 gbc.fill=GridBagConstraints.NONE;
								 gbc.gridx =1;
								 gbc.gridy =0;
								 išèiteelemente_zgoraj.add(seznam_tabel2,gbc);
								 gbc.gridy =0;
								 gbc.gridx=1;
								 išèiteelemente_spodaj.add(Vpelji_iskalne_kriterije,gbc);
								  								
									tabela_išèi = new JTable(modeltabele) {
										@Override
									    public boolean isCellEditable(int row, int column) {
								       //all cells false
								       return false;
										}
							
										    @Override
										    public Dimension getPreferredScrollableViewportSize()
										    {
										        return new Dimension(700, 300);
										    }
						
								    };
								   
								    gbc.insets = new Insets(50,50,150,30);
								  
								    gbc.gridwidth=1;
								    gbc.gridheight=1;
									gbc.gridx =0;
									gbc.gridy =0;
									
									
									JScrollPane panela_išèi=new JScrollPane(tabela_išèi);
									išèiteelemente_desno.add(panela_išèi,gbc);
								    panela_išèi.setMinimumSize( panela_išèi.getPreferredSize() );
								    
								     gbc.insets = new Insets(25,25,25,450);
									 gbc.gridx =0;
									 gbc.gridy=2;
									 išèiteelemente_spodaj.add(navodila_DQL,gbc);
									
									
								 
								 izbrana_tabela=(String) seznam_tabel2.getSelectedItem();
								 
								 seznam_tabel2.addActionListener(new ActionListener(){
									 @Override
									 public void actionPerformed(ActionEvent e) {
									 
										 if(e.getSource()==seznam_tabel2) {
											 išèi1_pogoj=false;
											 išèi2_pogoj=false;
											 
											 izbrana_tabela=(String) seznam_tabel2.getSelectedItem(); 
											 System.out.println(izbrana_tabela);
											 ArrayList <String> ar_stolpci=Pridobi_atribute(povezava, izbrana_tabela);
											
											 Nariši_obrazec(ar_napisi,ar_vnosi,ar_vnosi_podani,obrazec_podan,obrazec,atribut,išèiteelemente_sredina_vsebina, ar_stolpci, gbc, 0 , 0,false,izbrana_tabela,true,povezava);
											 Nariši_tabelo(povezava, izbrana_tabela, išèiteelemente_desno, tabela_išèi);
											
										 }
									 }
								 });
								 
								 Vpelji_iskalne_kriterije.addActionListener(new ActionListener() {
									 public void actionPerformed(ActionEvent e) {
										 String IskaniP=""; 
											Boolean prvi_element=true;
											
											 for(int i=0; i < ar_vnosi.size(); i++) {
												 try {
												if(ar_vnosi.get(i).getText().isEmpty()==true)	 {
													continue;
												}
												 	 
												 if(i!=0 && prvi_element==false) {
													 if(!preveri_èe_je_število(i, povezava, izbrana_tabela))
														 IskaniP+="AND WHERE"+ar_napisi.get(i).getText()+" LIKE '%"+ar_vnosi.get(i).getText()+"%' ";
													 else
														 IskaniP+="AND WHERE"+ar_napisi.get(i).getText()+" LIKE '%"+ar_vnosi.get(i).getText()+"%' ";
												 }
												 else {
													 if(!preveri_èe_je_število(i, povezava, izbrana_tabela))
														 IskaniP+="WHERE "+ar_napisi.get(i).getText()+" LIKE '%"+ar_vnosi.get(i).getText()+"%' ";
													 else
														 IskaniP+="WHERE "+ar_napisi.get(i).getText()+" LIKE '%"+ar_vnosi.get(i).getText()+"%' ";
												 }
												 }catch(Exception f) {
													 System.out.println(f);
												 }
												 prvi_element=false;
											 }
											
											 System.out.println("SELECT * FROM "+ izbrana_tabela+" "+IskaniP);
											 ArrayList<ArrayList<String>> podatki_selekcije;
											 ArrayList <String> atributi_selekcije=new ArrayList<String>();
											 
											 if(išèi1_pogoj==true) {
												 podatki_selekcije=Naredi_selekcijo(povezava, izraz+" "+IskaniP);
												 
												 Object [] stolpci= {"ID_naroèila", "Ime", "Priimek","EMAIL","Podjetje", "Datum","ID_proizvoda","Ime_proizvoda","Dimenzije proizvoda", "Barva", "Število_kosov","Opombe",
														 "Profili","Stekla","Okovje","Železo"};
												    for(int i=0; i < stolpci.length;i++) {
												    	atributi_selekcije.add((String) stolpci[i]);
													}
											 }
											 else {
												 podatki_selekcije=Naredi_selekcijo(povezava, "SELECT * FROM "+ izbrana_tabela+" "+IskaniP);
												 atributi_selekcije=Pridobi_atribute(povezava, izbrana_tabela);
											 }
											 
											 try {
											 
												Object [] stolpci=new Object[atributi_selekcije.size()];
												for(int i=0; i < atributi_selekcije.size();i++) {
													stolpci[i]=atributi_selekcije.get(i);
												}
												Object [][]podatki=new Object[podatki_selekcije.size()][podatki_selekcije.get(1).size()];
														
												int z=0;
												for(int i=0; i < podatki_selekcije.size();i++) {
													for(int j=0; j < podatki_selekcije.get(i).size();j++) {
													podatki[z][j]=podatki_selekcije.get(i).get(j);
													}
													z++;
												}	
												
											 
											 modeltabele.setDataVector(podatki, stolpci);
											 }catch (Exception d) {
												 JOptionPane.showMessageDialog(okna_glavni_okvir,"\"<html> Navodila za iskanje podatkov:<br><br>\"\r\n"
												 		+ "								 		+ \"- Pri iskanju v obrazec vtipkajte podatke po katerih želite izvesti poizvedbo<br>\"\r\n"
												 		+ "								 		+ \"- Na levi imate pripravljene še nekatere integrirane poizvedbe <br><br> </html>\"","Napaka pri iskanju",JOptionPane.INFORMATION_MESSAGE);
											}
											 
											
											
									 }
								 });
								 
								 gumb_išèi1.addActionListener(new ActionListener() {
									 public void actionPerformed(ActionEvent e) {
										 
										izraz="SELECT distinct naroèilo.ID_naroèila, kupec.Ime, kupec.Priimek, kupec.EMAIL, kupec.Podjetje, naroèilo.Datum,\r\n"
												+ "proizvodi.ID_proizvoda ,proizvodi.Ime_proizvoda, CONCAT(proizvodi.Dolžina,'x',proizvodi.Višina,'x',proizvodi.širina) AS Dimenzije, proizvodi.Barva, proizvodi.Število_kosov ,proizvodi.Opombe,\r\n"
												+ " GROUP_CONCAT(distinct obdelava_profilov.Ime_profila,' ', obdelava_profilov.Dolžina,' ', obdelava_profilov.Zakljuèek_profila,' ', obdelava_profilov.Število_kosov) AS Profili, \r\n"
												+ " GROUP_CONCAT(distinct steklitev.Tip_stekla,' ', CONCAT(steklitev.Širina,'x',steklitev.Višina),' ', steklitev.Število_kosov) AS Stekla,\r\n"
												+ "  GROUP_CONCAT(S_okovje.Ime_artikla,' ', okovje.Število_kosov) AS Okovje,\r\n"
												+ "   GROUP_CONCAT(S_železo.Ime_artikla,' ', ojaèitev.Dolžina,' ', ojaèitev.Število_kosov) AS Železo\r\n"
												+ "FROM naroèilo \r\n"
												+ "JOIN kupec ON naroèilo.ID_kupca=kupec.ID_kupca \r\n"
												+ "JOIN proizvodi ON naroèilo.ID_naroèila=proizvodi.ID_naroèila \r\n"
												+ "JOIN steklitev ON proizvodi.ID_proizvoda=steklitev.ID_proizvoda\r\n"
												+ "JOIN obdelava_profilov ON proizvodi.ID_proizvoda=obdelava_profilov.ID_proizvoda\r\n"
												+ "JOIN ojaèitev ON proizvodi.ID_proizvoda=ojaèitev.ID_proizvoda\r\n"
												+ "JOIN okovje ON proizvodi.ID_proizvoda=okovje.ID_proizvoda\r\n"
												+ "JOIN (SELECT * FROM skladišèe NATURAL JOIN okovje WHERE skladišèe.ID_artikla=okovje.ID_artikla) AS S_okovje\r\n"
												+ "JOIN (SELECT * FROM skladišèe NATURAL JOIN ojaèitev WHERE skladišèe.ID_artikla=ojaèitev.ID_artikla) AS S_železo"; 
										
										ArrayList<ArrayList<String>> podatki_selekcije=Naredi_selekcijo(povezava, izraz);
										
										
										Object [] stolpci= {"ID_naroèila", "Ime", "Priimek","EMAIL","Podjetje", "Datum","ID_proizvoda","Ime_proizvoda","Dimenzije proizvoda", "Barva", "Število_kosov","Opombe",
												 "Profili","Stekla","Okovje","Železo"};
										Object [][]podatki=new Object[podatki_selekcije.size()][podatki_selekcije.get(1).size()];
										
										
										
										int z=0;
										for(int i=0; i < podatki_selekcije.size();i++) {
											for(int j=0; j < podatki_selekcije.get(i).size();j++) {
											podatki[z][j]=podatki_selekcije.get(i).get(j);
											//System.out.print(podatki[z][j]);
											
											}
											System.out.println();
											z++;
										}	
										
										 ArrayList <String> ar_stolpci=new ArrayList<String>();
									    for(int i=0; i < stolpci.length-6;i++) {
											ar_stolpci.add((String) stolpci[i]);
										}
									    
									    
										modeltabele.setDataVector(podatki, stolpci);
										
										tabela_išèi = new JTable(modeltabele) {
											@Override
										    public boolean isCellEditable(int row, int column) {
									       //all cells false
									       return false;
											}
									    };
									   
									    Nariši_obrazec(ar_napisi,ar_vnosi,ar_vnosi_podani,obrazec_podan,obrazec,atribut,išèiteelemente_sredina_vsebina, ar_stolpci, gbc, 0 , 0,false,izbrana_tabela,true,povezava);
									    
									    išèiteelemente.repaint();
									    
									    išèi1_pogoj=true;
									 }
								  });
								
								 gumb_išèi2.addActionListener(new ActionListener() {
									 public void actionPerformed(ActionEvent e) {
										 
										izraz="SELECT ID_zaposlenega,Ime,Priimek, Starost, EMAIL, Tel_stevilka, Naziv_zaposlitve, Ime_mesta, Stroj FROM okna.zaposleni JOIN zaposlitev on zaposleni.ID_zaposlitev=zaposlitev.ID_zaposlitev JOIN delovno_mesto ON zaposlitev.ID_delovnega_mesta=delovno_mesto.ID_delovnega_mesta;"; 
										
										ArrayList<ArrayList<String>> podatki_selekcije=Naredi_selekcijo(povezava, izraz);
										
										
										Object [] stolpci= {"ID_zaposlenega","Ime","Priimek", "Starost", "EMAIL", "Tel_stevilka", "Naziv_zaposlitve", "Ime_delovnega_mesta", "Stroj"};
										Object [][]podatki=new Object[podatki_selekcije.size()][podatki_selekcije.get(1).size()];
										
										
										
										int z=0;
										for(int i=0; i < podatki_selekcije.size();i++) {
											for(int j=0; j < podatki_selekcije.get(i).size();j++) {
											podatki[z][j]=podatki_selekcije.get(i).get(j);
											//System.out.print(podatki[z][j]);
											
											}
											System.out.println();
											z++;
										}	
										
										 ArrayList <String> ar_stolpci=new ArrayList<String>();
									    for(int i=0; i < stolpci.length-6;i++) {
											ar_stolpci.add((String) stolpci[i]);
										}
									    
									    
										modeltabele.setDataVector(podatki, stolpci);
										
										tabela_išèi = new JTable(modeltabele) {
											@Override
										    public boolean isCellEditable(int row, int column) {
									       //all cells false
									       return false;
											}
									    };
									   
									    Nariši_obrazec(ar_napisi,ar_vnosi,ar_vnosi_podani,obrazec_podan,obrazec,atribut,išèiteelemente_sredina_vsebina, ar_stolpci, gbc, 0 , 0,false,izbrana_tabela,true,povezava);
									    
									    išèiteelemente.repaint();
									    
									    išèi2_pogoj=true;
									 }
								  });
								 
								
								 tabela_išèi = new JTable(modeltabele) {
										@Override
									    public boolean isCellEditable(int row, int column) {
								       //all cells false
								       return false;
										}
							
										    @Override
										    public Dimension getPreferredScrollableViewportSize()
										    {
										        return new Dimension(600, 250);
										    }
						
								    };
								 
								    
								    išèiteelemente.revalidate();
								    išèiteelemente.repaint();
								 
								//---------------------------------------------------------------------------------------
								//Upravljaj z bazo
								//---------------------------------------------------------------------------------------
								 upravljajzbazo.setLayout(new GridBagLayout());
								 gbc.gridwidth=1;
								 gbc.gridheight=1;
								 gbc.fill=GridBagConstraints.HORIZONTAL;
								 gbc.insets = new Insets(25,20,20,20);
								 gbc.gridx =2;
								 gbc.gridy =0;
								 upravljajzbazo.add(gumb_nazaj_na_meni_3, gbc);
								 gbc.gridx =1;
								 gbc.gridy =0;
								 upravljajzbazo.add(gumb_v_ustvarjanje_tabel, gbc);
								 gbc.gridx =0;
								 gbc.gridy =2;
								 gbc.weighty=2;
								 upravljajzbazo.add(gumb_zbrisi_tabelo, gbc);
								 gbc.gridx =0;
								 gbc.gridy =0;
								 upravljajzbazo.add(ustvari_zbrisi, gbc);
								 ustvari_zbrisi.setFont(new Font("Calibri", Font.BOLD,18));
								 gbc.gridx =0;
								 gbc.gridy =1;
								 upravljajzbazo.add(seznam_tabel3,gbc);
								 gbc.weighty=0;
								 
								 
								 gumb_zbrisi_tabelo.addActionListener(new ActionListener() {
									 public void actionPerformed(ActionEvent e) {
										 String izbrana_tabela=(String) seznam_tabel3.getSelectedItem();
										 
										 upravljajzbazo.remove(uspensno_zbr_tabele);
										 upravljajzbazo.remove(napaka_zbr_tabel);			 
										 
										 boolean uspesno=Zbriši_tabelo(povezava, izbrana_tabela);
										 if(uspesno) {
											 seznam_tabel1.removeItem(izbrana_tabela);
											 seznam_tabel2.removeItem(izbrana_tabela);
											 seznam_tabel3.removeItem(izbrana_tabela);
												 gbc.gridx =0;
												 gbc.gridy =2;
												 upravljajzbazo.add(uspensno_zbr_tabele,gbc);
										 }
										 else {
											 gbc.gridx =0;
											 gbc.gridy =2;
											 upravljajzbazo.add(napaka_zbr_tabel,gbc);
										 }
									 }
								 });
								 
								 gbc.fill=GridBagConstraints.NONE;
								 gbc.insets = new Insets(0,0,0,0);
								 gbc.gridx =0;
								 gbc.gridy=2;
								 upravljajzbazo.add(navodila_DDL,gbc);
								
								 
								 
								//---------------------------------------------------------------------------------------
								//Ustvari tabelo
								//---------------------------------------------------------------------------------------
								 ustvarjanje_tabel.setOrientation(JSplitPane.VERTICAL_SPLIT);  
							     
								 ustvarjanje_tabel.setTopComponent(ustvarjanje_tabele_zgoraj);
								 ustvarjanje_tabel.setBottomComponent(ustvarjanje_tabele_scroll);
								 ustvarjanje_tabele_scroll.setViewportView(ustvarjanje_tabele_spodaj_vsebina);
								 ustvarjanje_tabele_scroll.setSize(new Dimension(500, 400 ));
								 ustvarjanje_tabele_zgoraj.setLayout(new GridBagLayout());  
								 ustvarjanje_tabele_spodaj_vsebina.setLayout(new GridBagLayout());
								 
								 
								 
								 gbc.fill=GridBagConstraints.NONE;
								 gbc.insets = new Insets(25,25,15,25);
								 gbc.gridx =5;
								 gbc.gridy=0;
								 ustvarjanje_tabele_zgoraj.add(navodila_DDL,gbc);
								
									 
								 JLabel ime_tabele=new JLabel("Ime tabele:");	
								 JTextField ime_tabele_vnos=new JTextField(150);
								
								 gbc.gridwidth=1;
								 gbc.gridheight=1;
								 
								 gbc.fill=GridBagConstraints.HORIZONTAL;
								 gbc.gridx =0;
								 gbc.gridy =1;
								 ustvarjanje_tabele_zgoraj.add(ime_tabele, gbc);
								 gbc.gridx =1;
								 gbc.gridy =1;
								 ustvarjanje_tabele_zgoraj.add(ime_tabele_vnos, gbc);
								
								 gbc.gridx =0;
								 gbc.gridy =0;
								 ustvarjanje_tabele_zgoraj.add(ustvari, gbc);
								 ustvari.setFont(new Font("Calibri", Font.BOLD,16));
								
								 gbc.gridx =8;
								 gbc.gridy =0;
								 ustvarjanje_tabele_zgoraj.add(gumb_nazaj_iz_K_IN_B_tabel, gbc);
								 
								 gbc.gridx =2;
								 gbc.gridy =0;
								 ustvarjanje_tabele_zgoraj.add(dodaj_vrstico, gbc);
								 
								 gbc.gridx =3;
								 gbc.gridy =0;
								 ustvarjanje_tabele_zgoraj.add(zbriši_vrstico, gbc);
								 
								 gbc.gridx =4;
								 gbc.gridy =0;
								 ustvarjanje_tabele_zgoraj.add(ustvari_tabelo, gbc);
								 
								 JLabel ime_atributa=new JLabel("Ime atributa:");	
								 gbc.gridx =1;
								 gbc.gridy =0;
								 ustvarjanje_tabele_spodaj_vsebina.add(ime_atributa, gbc);
								 JLabel tip_atributa=new JLabel("Tip atributa:");
								 gbc.gridx =2;
								 gbc.gridy =0;
								 ustvarjanje_tabele_spodaj_vsebina.add(tip_atributa, gbc);
								 JLabel velikost_atributa=new JLabel("Velikost atributa:");
								 gbc.gridx =3;
								 gbc.gridy =0;
								 ustvarjanje_tabele_spodaj_vsebina.add(velikost_atributa, gbc);
								 JLabel Avto_inkrementacija=new JLabel("Avto inkrement:");	
								 gbc.gridx =4;
								 gbc.gridy =0;
								 ustvarjanje_tabele_spodaj_vsebina.add(Avto_inkrementacija, gbc);
								 JLabel Primarni_Kljuè=new JLabel("Primarni kljuè:");	
								 gbc.gridx =5;
								 gbc.gridy =0;
								 ustvarjanje_tabele_spodaj_vsebina.add(Primarni_Kljuè, gbc);
								 JLabel Not_null=new JLabel("Ni nièelna vrednost:");	
								 gbc.gridx =6;
								 gbc.gridy =0;
								 ustvarjanje_tabele_spodaj_vsebina.add(Not_null, gbc);
								 JLabel Tuji_kljuè=new JLabel("Tuji kljuè:");	
								 gbc.gridx =7;
								 gbc.gridy =0;
								 ustvarjanje_tabele_spodaj_vsebina.add(Tuji_kljuè, gbc);
								 JLabel Tuji_kljuè_referenca=new JLabel("Referenca tujega kljuèa:");	
								 gbc.gridx =8;
								 gbc.gridy =0;
								 ustvarjanje_tabele_spodaj_vsebina.add(Tuji_kljuè_referenca, gbc);
								 
								 
								 gumb_nazaj_iz_K_IN_B_tabel.addActionListener(new ActionListener() {
									 public void actionPerformed(ActionEvent e) {
										cl.show(vsebina,"4");
									 }
								 });
								 
								 dodaj_vrstico.addActionListener(new ActionListener() {
									 public void actionPerformed(ActionEvent e) {
										 if(stevilo_vrstic<15) {
										 Zapolni_array_atributov(tabela_tabel, oznakaatributa,ime_at, tip_at, vel_at, AI_at, PK_at, NN_at, PFK_at, reference_tabela); 
										 
										 							 gbc.fill = GridBagConstraints.NONE;
											 gbc.gridy=1+stevilo_vrstic;
											 gbc.gridx=0;
											 ustvarjanje_tabele_spodaj_vsebina.add(ar_oznaka_at.get(stevilo_vrstic),gbc); gbc.gridx=1;
											 ustvarjanje_tabele_spodaj_vsebina.add(ar_ime_at.get(stevilo_vrstic),gbc); gbc.gridx=2;
											 ustvarjanje_tabele_spodaj_vsebina.add(ar_tip_at.get(stevilo_vrstic),gbc); gbc.gridx=3;
											 ustvarjanje_tabele_spodaj_vsebina.add(ar_vel_at.get(stevilo_vrstic),gbc); gbc.gridx=4;
											 ustvarjanje_tabele_spodaj_vsebina.add(ar_AI_at.get(stevilo_vrstic),gbc); gbc.gridx=5;
											 ustvarjanje_tabele_spodaj_vsebina.add(ar_PK_at.get(stevilo_vrstic),gbc); gbc.gridx=6;
											 ustvarjanje_tabele_spodaj_vsebina.add(ar_NN_at.get(stevilo_vrstic),gbc); gbc.gridx=7;
											 ustvarjanje_tabele_spodaj_vsebina.add(ar_PFK_at.get(stevilo_vrstic),gbc); gbc.gridx=8;
											 ustvarjanje_tabele_spodaj_vsebina.add(ar_reference_tabela.get(stevilo_vrstic),gbc);
											 
											 ustvarjanje_tabele_spodaj_vsebina.revalidate();
											 ustvarjanje_tabele_spodaj_vsebina.repaint();
											
											 stevilo_vrstic++;
											 System.out.println(stevilo_vrstic);
											 
											 
										}
									 }
								 });
								 
								 
										 
										 zbriši_vrstico.addActionListener(new ActionListener() {
											 public void actionPerformed(ActionEvent e) {
												 
												 if(stevilo_vrstic>0) {
													 
														try {
																
															ustvarjanje_tabele_spodaj_vsebina.remove(ar_oznaka_at.get(stevilo_vrstic-1));
															ustvarjanje_tabele_spodaj_vsebina.remove(ar_ime_at.get(stevilo_vrstic-1));
															ustvarjanje_tabele_spodaj_vsebina.remove(ar_tip_at.get(stevilo_vrstic-1));
															ustvarjanje_tabele_spodaj_vsebina.remove(ar_vel_at.get(stevilo_vrstic-1));
															ustvarjanje_tabele_spodaj_vsebina.remove(ar_AI_at.get(stevilo_vrstic-1));
															ustvarjanje_tabele_spodaj_vsebina.remove(ar_PK_at.get(stevilo_vrstic-1));
															ustvarjanje_tabele_spodaj_vsebina.remove(ar_NN_at.get(stevilo_vrstic-1));
															ustvarjanje_tabele_spodaj_vsebina.remove(ar_PFK_at.get(stevilo_vrstic-1));
															ustvarjanje_tabele_spodaj_vsebina.remove(ar_reference_tabela.get(stevilo_vrstic-1));
															 
															 	ar_oznaka_at.remove(stevilo_vrstic-1);
																ar_ime_at.remove(stevilo_vrstic-1);
																ar_tip_at.remove(stevilo_vrstic-1);
																ar_vel_at.remove(stevilo_vrstic-1);
																ar_AI_at.remove(stevilo_vrstic-1);
																ar_PK_at.remove(stevilo_vrstic-1);
																ar_NN_at.remove(stevilo_vrstic-1);
																ar_PFK_at.remove(stevilo_vrstic-1);
																ar_reference_tabela.remove(stevilo_vrstic-1);
						
																	
																ustvarjanje_tabele_spodaj_vsebina.revalidate();
																ustvarjanje_tabele_spodaj_vsebina.repaint();
																stevilo_vrstic--;
																			
														}catch (Exception g) {
																System.out.println(g);
														}
														
														 System.out.println(stevilo_vrstic);
												 }
											 }
											 });
						
										
										 ustvari_tabelo.addActionListener(new ActionListener() {
											 public void actionPerformed(ActionEvent e) {
												 String podatki="CREATE TABLE "+ime_tabele_vnos.getText()+"(";
												 
												 ArrayList<String>primarni_kljuci=new ArrayList<String>();
												 for(index=0;index<ar_oznaka_at.size();index++) {
													
													podatki+=(ar_ime_at.get(index).getText()+" ");
													podatki+=(ar_tip_at.get(index).getSelectedItem());
													
											
														switch((String) ar_tip_at.get(index).getSelectedItem()) {
														
														case "Integer": 
															if(ar_AI_at.get(index).isSelected()==false){
															podatki+="("+Je_steviloINT(ar_vel_at.get(index).getText(), podatki)+")";
															};break;
														case "Decimal": 
															podatki+="("+Je_steviloDOUBLE(ar_vel_at.get(index).getText(), podatki)+")";break;			
														case "Char": 
															podatki+="("+Je_steviloINT(ar_vel_at.get(index).getText(), podatki)+")";break;
														case "Varchar": 
															podatki+="("+Je_steviloINT(ar_vel_at.get(index).getText(), podatki)+")";break;
										
														}
													
													if(( ar_NN_at.get(index)).isSelected()==true) {
														podatki+=" NOT NULL ";
													}
													if((ar_AI_at.get(index)).isSelected()==true) {
														podatki+=" AUTO_INCREMENT ";
													}
													if((ar_PK_at.get(index)).isSelected()==true) {
														primarni_kljuci.add((String)(ar_ime_at.get(index)).getText());
													}
													if(( ar_PFK_at.get(index)).isSelected()==true) {
														podatki+=",foreign key ("+(ar_ime_at.get(index)).getText()+")  references "+(ar_reference_tabela.get(index)).getSelectedItem()+"("+ar_ime_at.get(index).getText()+") ";
														if(index!=stevilo_vrstic-1)
															podatki+=",";
													}
													else {
														if(index!=stevilo_vrstic-1)
															podatki+=",";
													
													}
													System.out.println(podatki);
													System.out.println(index);
												 }
												 if(primarni_kljuci.size()!=0) {
													 podatki+=",PRIMARY KEY (";
														 for(int j=0;j<primarni_kljuci.size();j++) {
															podatki+=primarni_kljuci.get(j);
															if(j!=primarni_kljuci.size()-1)
															podatki+=", ";
														 }
													podatki+=")";
												 }
												 
												 podatki+=");";
												 
												 System.out.println(podatki);
												 ustvarjanje_tabele_zgoraj.remove(uspensno_ust_tabele);
												 ustvarjanje_tabele_zgoraj.remove(napaka_ust_tabel);
												 boolean uspesno=Naredi_tabelo(povezava, podatki);
												 
												 
												 if(uspesno) {
												 seznam_tabel1.addItem(ime_tabele_vnos.getText());
												 seznam_tabel2.addItem(ime_tabele_vnos.getText());
												 seznam_tabel3.addItem(ime_tabele_vnos.getText());
												 	
												 	gbc.gridx =2;
												 	gbc.gridy =1;
												 	gbc.insets = new Insets(25,25,15,25);
												 	ustvarjanje_tabele_zgoraj.add(uspensno_ust_tabele,gbc);
												 }
												 else {
													 gbc.gridx =2;
													 gbc.gridy =1;
													 ustvarjanje_tabele_zgoraj.add(napaka_ust_tabel,gbc);
												 }
												 
											 }
						
											private String Je_steviloDOUBLE(String text,String podatki) {
												try {
													double i=Double.parseDouble((ar_vel_at.get(index)).getText());
													 velikost=String.valueOf(i);
													 return(velikost);
												}catch (NumberFormatException t) {
													System.out.println("Ni pravi format");
													podatki="";
													
													JOptionPane.showMessageDialog(okna_glavni_okvir,"V prostor za doloèanje velikosti podatkovnih tipov ste vnesli napaèen tip podatka! "+"\n"+
															 "- Za tip Integer, Char ali Varchar se prièakuje celo stevilo, ki predstavlja stevila mest/èrk tega števila ali besedila,"+"\n"+
															 "- Za tip Decimal je pridvidena deklaracija velikosti v obliki M,N -> M za število šèlenov števil pred decimalno vrednostjo"+"\n"+
															 "in N za število èlenov decimalnih vrednosti"+"\n"+
															 "- Za tipe Text, Date in Boolean ne potrebujejo definicijo velikosti","Napaèni format",JOptionPane.WARNING_MESSAGE);
													return (" ");
												}					
												
											}
						
											private String Je_steviloINT(String text, String podatki) {
												try {
													int i=Integer.parseInt(ar_vel_at.get(index).getText());
													 velikost=String.valueOf(i);
													 return(velikost);
												}catch (NumberFormatException t) {
													System.out.println("Ni pravi format");
													podatki="";
													JOptionPane.showMessageDialog(okna_glavni_okvir,"V prostor za doloèanje velikosti podatkovnih tipov ste vnesli napaèen tip podatka! "+"\n"+
															 "- Za tip Integer, Char ali Varchar se prièakuje celo stevilo, ki predstavlja stevila mest/èrk tega števila ali besedila,"+"\n"+
															 "- Za tip Decimal je pridvidena deklaracija velikosti v obliki M,N -> M za število šèlenov števil pred decimalno vrednostjo"+"\n"+
															 "in N za število èlenov decimalnih vrednosti"+"\n"+
															 "- Za tipe Text, Date in Boolean ne potrebujejo definicijo velikosti","Napaèni format",JOptionPane.WARNING_MESSAGE);
													return (" ");
												}					
												
											}
										 });
									
								 
								 
								//---------------------------------------------------------------------------------------
								//Nastavitve  vse panele
								//---------------------------------------------------------------------------------------
								 gumb_nazaj_na_meni_1.setBounds(225,450,200,25);
								 gumb_nazaj_na_meni_2.setBounds(225,450,200,25);
								 gumb_nazaj_na_meni_3.setBounds(225,450,200,25);
								 
								 
								 vsebina.add(glavni_meni,"1");
								 vsebina.add(obdelujteelemente, "2");
								 vsebina.add(išèiteelemente, "3");
								 vsebina.add(upravljajzbazo, "4");
								 vsebina.add(ustvarjanje_tabel,"5");
								
								 
								 cl.show(vsebina, "1");
								
														 gumb_v_obdelavo.addActionListener(new ActionListener(){
															 
															 @Override
															 public void actionPerformed(ActionEvent e) {
																 
																 try {
																	 seznam_tabel1.setSelectedIndex(0);
																	 seznam_tabel1.actionPerformed(e);
																	 seznam_tabel1.setSelectedIndex(0);
																 }catch (Exception s) {
																	System.out.println(s);
																}
																 
																 cl.show(vsebina,"2");
																 
															 }
														 });
														 
														
														gumb_v_iskanje.addActionListener(new ActionListener(){
															 
															 @Override
															 public void actionPerformed(ActionEvent e) {
																 try {
																	 seznam_tabel2.setSelectedIndex(0);
																	 seznam_tabel2.actionPerformed(e);
																	 seznam_tabel2.setSelectedIndex(0);
																 }catch (Exception s) {
																	System.out.println(s);
																}
																 cl.show(vsebina,"3");
																
															 }
														 });
														gumb_v_upravljanje.addActionListener(new ActionListener(){
															 
															 @Override
															 public void actionPerformed(ActionEvent e) {
																 prijava_ad.setVisible(true);
															 }
														 });
														
														gumb_v_ustvarjanje_tabel.addActionListener(new ActionListener(){
															 
															 @Override
															 public void actionPerformed(ActionEvent e) {
																 cl.show(vsebina,"5");
															 }
														 });
														
								 
														gumb_nazaj_na_meni_1.addActionListener(new ActionListener(){
																	 
																	 @Override
																	 public void actionPerformed(ActionEvent e) {
																		 cl.show(vsebina,"1");
																	 }
																 });
														gumb_nazaj_na_meni_2.addActionListener(new ActionListener(){
															 
															 @Override
															 public void actionPerformed(ActionEvent e) {
																 cl.show(vsebina,"1");
															 }
														 });
														gumb_nazaj_na_meni_3.addActionListener(new ActionListener(){
															 
															 @Override
															 public void actionPerformed(ActionEvent e) {
																 cl.show(vsebina,"1");
															 }
														 });
														
														navodila_DDL.addActionListener(new ActionListener(){
															 @Override
															 public void actionPerformed(ActionEvent e) {
													
																JOptionPane.showMessageDialog(okna_glavni_okvir,"<html> Navodila za brisanje in kreiranje tabel:<br><br>"
																 		+ "- Pri brisu tabel samo izberite tabelo, ki jo želite odstraniti<br>"
																 		+ "- Pri izdelavi se naprej  pomaknite na naslednje okno z gumbom <br>"
																 		+ "- Pri izdelavi se za podatkovni tip Integer, Char ali Varchar prièakuje pod oznako velikost celo stevilo,<br> ki predstavlja stevila mest/èrk tega števila ali besedila. <br>"
																 		+ "- Za tip Decimal je pridvidena deklaracija velikosti v obliki M,N -> M za število šèlenov števil pred decimalno vrednostjo <br>in N za število èlenov decimalnih vrednosti."
																 		+ " Za tipe Text, Date in Boolean ne potrebujejo definicijo velikosti. <br>"
																 		+ " <br><br> </html>","Pomoè pri obdelavi podatkov",JOptionPane.INFORMATION_MESSAGE);
																	 
															 }
														 });
														navodila_DQL.addActionListener(new ActionListener(){
															 @Override
															 public void actionPerformed(ActionEvent e) {
													
																JOptionPane.showMessageDialog(okna_glavni_okvir, "<html> Navodila za iskanje podatkov:<br><br>"
																 		+ "- Pri iskanju v obrazec vtipkajte podatke po katerih želite izvesti poizvedbo<br>"
																 		+ "- Na levi imate pripravljene še nekatere integrirane poizvedbe <br><br> </html>","Pomoè pri iskanju elementov",JOptionPane.INFORMATION_MESSAGE);
																	 
															 }
														 });
														navodila_DML.addActionListener(new ActionListener(){
															 @Override
															 public void actionPerformed(ActionEvent e) {
													
																 JOptionPane.showMessageDialog(okna_glavni_okvir,"<html>Navodila za delo s podatki:<br><br>"
																	 		+ "- V obrazec pri izbrisu ni potrebno vnesti vseh podatkov, vendar je zaradi nataènosti<br>"
																	 		+ "priporoèljivo vnesti veè parametrov, èe se ne išèe po primarnem kljuèu! <br><br>"
																	 		+"- V obrazec je pri popravljanju nujno potrebno vnesti stari ID elementa, ko popravljamo ID elementa! </html>","Pomoè pri upravljanju z bazo",JOptionPane.INFORMATION_MESSAGE);
																		 
															 }
														 });
														
							
								glavni_meni.setLayout(null);
								okna_glavni_okvir.add(vsebina);
								okna_glavni_okvir.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
								okna_glavni_okvir.setVisible(true);
								okna_glavni_okvir.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
								
								
				
								
								prijava_ad_vsebina.setLayout(null);
								prijava_ad.add(prijava_ad_vsebina);
								prijava_ad_vsebina.add(geslo_ad);
								prijava_ad_vsebina.add(geslo_vnos_ad);
								geslo_ad.setBounds(5,5,60,25);
								geslo_vnos_ad.setBounds(70,5,100,25);
								prijava_ad.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
								prijava_ad.setSize(350,180);
								prijava_ad_vsebina.add(gumb_prijava_ad);
								gumb_prijava_ad.setBounds(5,35,120,25);
								geslo_odgovor_ad.setBounds(5,70,120,15);
								
								gumb_prijava_ad.addActionListener(new ActionListener(){
									 @Override
									 public void actionPerformed(ActionEvent e) {
										prijava_ad_vsebina.remove(geslo_odgovor_ad);
										if(geslo_vnos_ad.getText().equals("geslo")) {
											cl.show(vsebina,"4");
											prijava_ad.setVisible(false);
										}else {
											prijava_ad_vsebina.add(geslo_odgovor_ad);
										}
									 }
								 });
								
							
								

								 
				 
				 
	 }


	private void Zapolni_array_atributov(Object[] tabela_tabel, JLabel oznakaatributa,JTextField ime_at, JComboBox<Object> tip_at, JTextField vel_at, JCheckBox AI_at, JCheckBox PK_at, JCheckBox NN_at, JCheckBox PFK_at,JComboBox<Object> reference_tabela2) {
		
		oznakaatributa=new JLabel("Atribut "+(stevilo_vrstic+1));
		ime_at=new JTextField(50);
		tip_at=new JComboBox<Object>(tipi_atributov);
		vel_at=new JTextField(50);
		AI_at=new JCheckBox();
		PK_at=new JCheckBox();
		NN_at=new JCheckBox();
		PFK_at=new JCheckBox();
		reference_tabela2=new JComboBox<Object>(tabela_tabel);
		
		ar_oznaka_at.add(oznakaatributa);
		ar_ime_at.add(ime_at);
		ar_tip_at.add(tip_at);
		ar_vel_at.add(vel_at);
		ar_AI_at.add(AI_at);
		ar_PK_at.add(PK_at);
		ar_NN_at.add(NN_at);
		ar_PFK_at.add(PFK_at);
		ar_reference_tabela.add(reference_tabela2);
		
		 
	}


	protected void Nariši_tabelo(Connection povezava, String izbrana_tabela, JPanel panela, JTable tabela) {
		
		String poizvedba=("SELECT * FROM "+izbrana_tabela);
		
			
		 ArrayList<ArrayList<String>> ar_vrstice=Naredi_selekcijo(povezava,poizvedba);
		 
		 ArrayList <String> ar_stolpci=Pridobi_atribute(povezava, izbrana_tabela);
		 
		 
			Object [] stolpci=new Object[ar_stolpci.size()];
			
			for(int i=0; i < ar_stolpci.size();i++) {
				stolpci[i]=ar_stolpci.get(i);
				//System.out.println(stolpci[i]);
			}
			
			try {
			Object [][]podatki=new Object[ar_vrstice.size()][ar_vrstice.get(0).size()];

			int z=0;
			for(int i=0; i < ar_vrstice.size();i++) {
				
				for(int j=0; j < ar_vrstice.get(i).size();j++) {
				podatki[z][j]=ar_vrstice.get(i).get(j);
				
				
				}
				
				z++;
			}	
			
	
			
			modeltabele.setDataVector(podatki, stolpci);
			
				tabela = new JTable(modeltabele) {
					@Override
				    public boolean isCellEditable(int row, int column) {
			       //all cells false
			       return false;
					}
			    };
			    
			    panela.repaint();
			    
			}catch (Exception e) {
				System.out.println(e);
			}
	}
	
	protected void Nariši_obrazec(ArrayList<JLabel> arnapisov, ArrayList<JTextField> ar,ArrayList<JComboBox> ar_podani,JComboBox<Object> obrazec_podan,JTextField obrazec, JLabel atribut, JPanel panela, ArrayList<String> ar_stolpci, GridBagConstraints gbc, int x_kord, int y_kord, boolean DML, String tabela,boolean iskanje ,Connection povezava) {
		 
		
		
				for(int m=0; m < arnapisov.size(); m++) 
					panela.remove(arnapisov.get(m));
				
				for(int l=0; l < ar.size(); l++) 
					panela.remove(ar.get(l));
				 
				for(int k=0;k<ar_podani.size();k++)
					panela.remove(ar_podani.get(k));
				
				
				
				 arnapisov.clear();
				 ar.clear();
				 ar_podani.clear();
				 tujikljuc.clear();
				 
				 panela.revalidate();
				 panela.repaint();
				 
				 for(int i=0; i < ar_stolpci.size(); i++) {
				
					// System.out.println(ar_stolpci.get(i));
					 atribut= new JLabel(ar_stolpci.get(i));
					 arnapisov.add(atribut);
					 
					 obrazec=new JTextField(25);
					 
					 AbstractDocument d = (AbstractDocument) obrazec.getDocument();
					 d.setDocumentFilter(new Omeji_velikost(DobiVelikost(povezava,tabela,i)));
					 
					 ar.add(obrazec);
					 
					 String [] podane_moznosti;
					 
					 if(JeTujiKljuè(povezava,tabela,i) && iskanje==false) {
						 System.out.println("Je tuji");
						 podane_moznosti=Poizvedba_moznosti(povezava,tabela,ar_stolpci.get(i));
						 
						 obrazec_podan=new JComboBox<Object>(podane_moznosti);
						 
						 ar_podani.add(obrazec_podan);
						 tujikljuc.add(i);
					}else {
						String[] nul= {"n"};
						 obrazec_podan=new JComboBox<Object>(nul);
						 ar_podani.add(obrazec_podan);
						 tujikljuc.add(-1);
					 }
					 
					 panela.repaint();
					 
				 }
				 
				 
				 for(int i=0; i < arnapisov.size(); i++) {
					 gbc.fill = GridBagConstraints.HORIZONTAL;
					 gbc.insets = new Insets(10,20,10,0);
					 
					 gbc.gridwidth=1;
					 gbc.gridheight=1;
					 gbc.gridx =x_kord;
					 gbc.gridy =y_kord+i;
					 gbc.weighty=4;
					 gbc.weightx=5;
					 panela.add(arnapisov.get(i),gbc);
						gbc.insets = new Insets(5,5,0,0);
						gbc.gridx=x_kord+1;
						if(tujikljuc.contains(i)) 
							panela.add(ar_podani.get(i),gbc);
						else
							panela.add(ar.get(i),gbc);
				 }
				 
				 panela.revalidate();
				 panela.repaint();
			 }
		


	private ArrayList<String> poišèi_vse_tabele(ArrayList<String> ar, Connection povezava) {
		
		try {
			
			String tabele="show tables";
			Statement izjava=povezava.createStatement();
			ResultSet rezultat=izjava.executeQuery(tabele);
			
					while(rezultat.next()) {
						ar.add(rezultat.getString(1));
						}
			
			}catch(SQLException e) {
				System.out.println("Tabele niso bile najdene");
			}
		return ar;
	}
	
	 private boolean preveri_èe_je_število(int i, Connection povezava, String tabela) {
		
		ArrayList<String> ar=new ArrayList<String>();
		
		try {
			
		String atributi="show columns from "+tabela;
		Statement izjava=povezava.createStatement();
		ResultSet rezultat=izjava.executeQuery(atributi);
		
				while(rezultat.next()) {
					ar.add(rezultat.getString(2));
					System.out.println(rezultat.getString(2));
					}
		
		}catch(SQLException e) {
			System.out.println("Atributi niso bili najdeni");
		}
		
		if(ar.get(i).equals("int")||ar.get(i).equals("double"))
			return true;
		else
			return false;
	 }
	 
	 private boolean preveri_èe_je_AI(int i, Connection povezava, String tabela) {
			
			ArrayList<String> ar=new ArrayList<String>();
			
			try {
				
			String atributi="show columns from "+tabela;
			Statement izjava=povezava.createStatement();
			ResultSet rezultat=izjava.executeQuery(atributi);
			
					while(rezultat.next()) {
						ar.add(rezultat.getString(6));
						rezultat.getString(6);
						}
			
			}catch(SQLException e) {
				System.out.println("Atributi niso bili najdeni");
			}
			
			if(ar.get(i).equals("auto_increment"))
				return true;
			else
				return false;
		 }
	 		
	 String PridobiID(JComboBox seznam) {
			
			String niz=(String) seznam.getSelectedItem();
			if(niz.contains("|")) {
			niz=niz.substring(0,niz.indexOf("|"));
			return niz;
			}else
			return null;
		}

		
		    


 }
		 


