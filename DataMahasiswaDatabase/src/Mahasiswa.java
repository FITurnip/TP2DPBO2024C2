public class Mahasiswa {
    private String nim;
    private String nama;
    private String jenisKelamin;
    private String kondisiRilis;

    public Mahasiswa(String nim, String nama, String jenisKelamin, String kondisiRilis) {
        this.nim = nim;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.kondisiRilis = kondisiRilis;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public void setKondisiRilis(String kondisiRilis) {
        this.kondisiRilis = kondisiRilis;
    }
    public String getNim() {
        return this.nim;
    }

    public String getNama() {
        return this.nama;
    }

    public String getJenisKelamin() {
        return this.jenisKelamin;
    }

    public String getKondisiRilis() { return this.kondisiRilis; }
}
