package com.pdambaru;

/*
 * Kamus.java 
 * adalah Model data
 * para prinsipnya Model disini sama dengan konsep MVC
 * model kamus disini terdiri dari idkamus,kata1,kata2 dan kata3
 * model ini bersifat general sehingga bisa dipakai oleh 3 vairasi kamus 
 * 
 */
public class Pelanggan {
	
	private String no;
	private String nosamw;
	private String nama;
	private String alamat;
	private String keterangan;
	private String status;
	private int met_l;


	private int met_k;
	private int pakai;
	

	
	public Pelanggan(String nomor,String nos, String na, String al,
			String ket,String stat) {
		no=nomor;
		nosamw=nos;
		nama=na;
		alamat=al;
		keterangan=ket;
		status=stat;

	}
	public Pelanggan(String nomor,String nos, String na, String al,
			String ket,String stat,int l,int k,int p) {
		no=nomor;
		nosamw=nos;
		nama=na;
		alamat=al;
		keterangan=ket;
		status=stat;
		met_l=l;
		met_k=k;
		pakai=p;

	}
	
	
	public int getMet_l() {
		return met_l;
	}
	public void setMet_l(int met_l) {
		this.met_l = met_l;
	}
	public int getMet_k() {
		return met_k;
	}
	public void setMet_k(int met_k) {
		this.met_k = met_k;
	}
	public int getPakai() {
		return pakai;
	}
	public void setPakai(int pakai) {
		this.pakai = pakai;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getNosamw() {
		return nosamw;
	}

	public void setNosamw(String nosamw) {
		this.nosamw = nosamw;
	}

	public String getNama() {
		return nama;
	}

	public void setNama(String nama) {
		this.nama = nama;
	}

	public String getAlamat() {
		return alamat;
	}

	public void setAlamat(String alamat) {
		this.alamat = alamat;
	}

	public String getKeterangan() {
		return keterangan;
	}

	public void setKeterangan(String keterangan) {
		this.keterangan = keterangan;
	}

	public String tampilUkur(){
		return ""+met_k+" | "+met_l+" | "+pakai;
	}
	
	

}
