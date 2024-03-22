import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Menu extends JFrame{
    public static void main(String[] args) {
        // buat object window
        Menu window = new Menu();

        // atur ukuran window
        window.setSize(480, 560);
        // letakkan window di tengah layar
        window.setLocationRelativeTo(null);
        // isi window
        window.setContentPane(window.mainPanel);
        // ubah warna background
        window.getContentPane().setBackground(Color.white);
        // tampilkan window
        window.setVisible(true);
        // agar program ikut berhenti saat window diclose
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // index baris yang diklik
    private int selectedIndex = -1;
    private String selectedNimMahasiswa = "";
    // list untuk menampung semua mahasiswa
    private ArrayList<Mahasiswa> listMahasiswa;
    private Database database;

    private JPanel mainPanel;
    private JTextField nimField;
    private JTextField namaField;
    private JTable mahasiswaTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox jenisKelaminComboBox;
    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel nimLabel;
    private JLabel namaLabel;
    private JLabel jenisKelaminLabel;
    private JRadioButton yaRilisRadioButton;
    private JRadioButton tidakRilisRadioButton;
    private JLabel RilisLabel;

    // constructor
    public Menu() {
        // inisialisasi listMahasiswa
        listMahasiswa = new ArrayList<>();

        // buat object mahasiswa
        database = new Database();

        // isi tabel mahasiswa
        mahasiswaTable.setModel(setTable());

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        // atur isi combo box
        String[] jenisKelaminData = {"", "Laki-laki", "Perempuan"};
        jenisKelaminComboBox.setModel(new DefaultComboBoxModel(jenisKelaminData));

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });
        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex >= 0) {
                    deleteData();
                }
            }
        });
        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // saat tombol
                clearForm();
            }
        });
        // saat salah satu baris tabel ditekan
        mahasiswaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = mahasiswaTable.getSelectedRow();
                selectedNimMahasiswa = mahasiswaTable.getModel().getValueAt(selectedIndex, 1).toString();

                // simpan value textfield dan combo box
                String selectedNim = selectedNimMahasiswa;
                String selectedNama = mahasiswaTable.getModel().getValueAt(selectedIndex, 2).toString();
                String selectedJenisKelamin = mahasiswaTable.getModel().getValueAt(selectedIndex, 3).toString();
                String selectedRilis = mahasiswaTable.getModel().getValueAt(selectedIndex, 4).toString();

                // ubah isi textfield dan combo box
                nimField.setText(selectedNim);
                namaField.setText(selectedNama);
                jenisKelaminComboBox.setSelectedItem(selectedJenisKelamin);
                if(selectedRilis.compareTo("Ya") == 0) {
                    yaRilisRadioButton.setSelected(true);
                    tidakRilisRadioButton.setSelected(false);
                } else {
                    yaRilisRadioButton.setSelected(false);
                    tidakRilisRadioButton.setSelected(true);
                }

                // ubah button "Add" menjadi "Update"
                addUpdateButton.setText("Update");
                // tampilkan button delete
                deleteButton.setVisible(true);
            }
        });
    }

    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] column = {"No", "NIM", "Nama", "Jenis Kelamin", "Rilis"};

        // buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel temp = new DefaultTableModel(null, column);

        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM mahasiswa");
            int i = 0;
            while(resultSet.next()) {
                Object[] row = new Object[5];

                row[0] = i + 1;
                row[1] = resultSet.getString("nim");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getString("jenis_kelamin");
                row[4] = resultSet.getString("rilis");

                temp.addRow(row);
                i++;
            }
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }

        return temp;
    }

    public boolean isUniqueNim(String nim) {
        ResultSet resultSet = database.selectQuery("SELECT nim FROM mahasiswa");
        boolean isUnique = true;

        try {
            while(resultSet.next() && isUnique) {
                String curNim = resultSet.getString("nim");
                if(curNim.compareTo(nim) == 0) {
                    isUnique = false;
                }
            }
        } catch (SQLException e) {
            isUnique = false;
            throw new RuntimeException(e);
        }

        return isUnique;
    }

    public boolean isValidInput(String nim, String nama, String jenisKelamin, String kondisiRilis) {
        boolean isValid = true;
        if(nim.isEmpty() || nim.isBlank()) {
            isValid = false;
        }

        if(isValid) {
            if(nama.isEmpty() || nama.isBlank()) {
                isValid = false;
            }
        }

        if(isValid) {
            if(jenisKelamin.isEmpty() || jenisKelamin.isBlank()) {
                isValid = false;
            }
        }

        if(isValid) {
            if(kondisiRilis.isEmpty() || kondisiRilis.isBlank()) {
                isValid = false;
            }
        }

        return isValid;
    }

    public void insertData() {
        // ambil value dari textfield dan combobox
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String kondisiRilis = "";
        if(yaRilisRadioButton.isSelected()) {
            kondisiRilis = "Ya";
        } else if(tidakRilisRadioButton.isSelected()) {
            kondisiRilis = "Tidak";
        }

        if(isValidInput(nim, nama, jenisKelamin, kondisiRilis)) {
            // tambahkan data ke dalam list
            String insSql = "INSERT INTO mahasiswa VALUES(null, '" + nim + "', '" + nama + "', '" + jenisKelamin + "', '" + kondisiRilis + "');";
            if(isUniqueNim(nim)) {
                database.insertUpdateDeleteQuery(insSql);

                // update tabel
                mahasiswaTable.setModel(setTable());

                // bersihkan form
                clearForm();

                // feedback
                System.out.println("Insert berhasil!");
                JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
            } else {
                // feedback
                System.out.println("Insert gagal! NIM tidak unik");
                JOptionPane.showMessageDialog(null, "NIM tidak unik");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Data tidak terisi penuh");
        }
    }

    public void updateData() {
        // ambil data dari form
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String kondisiRilis = "";
        if(yaRilisRadioButton.isSelected()) {
            kondisiRilis = "Ya";
        } else if(tidakRilisRadioButton.isSelected()) {
            kondisiRilis = "Tidak";
        }

        if(isValidInput(nim, nama, jenisKelamin, kondisiRilis)) {
            String uptSql = "UPDATE mahasiswa SET nama = '" + nama + "', jenis_kelamin='" + jenisKelamin + "', rilis='" + kondisiRilis + "' WHERE nim = '" + selectedNimMahasiswa + "';";

            boolean makeUpdate = true;
            if(nim.compareTo(selectedNimMahasiswa) != 0) {
                if(isUniqueNim(nim)) {
                    uptSql = "UPDATE mahasiswa SET nim = '" + nim + "', nama = '" + nama + "', jenis_kelamin='" + jenisKelamin + ", rilis='" + kondisiRilis+ "' WHERE nim = '" + selectedNimMahasiswa + "';";
                } else {
                    makeUpdate = false;
                }
            }

            if(makeUpdate) {
                // ubah data mahasiswa di list
                database.insertUpdateDeleteQuery(uptSql);

                // update tabel
                mahasiswaTable.setModel(setTable());

                // bersihkan form
                clearForm();

                // feedback
                System.out.println("Update Berhasil!");
                JOptionPane.showMessageDialog(null, "Data berhasil diubah!");
            } else {

                // feedback
                System.out.println("Update Gagal!");
                JOptionPane.showMessageDialog(null, "NIM tidak unik!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Data tidak terisi penuh");
        }
    }

    public void deleteData() {
        // hapus data dari list
        String delSql = "DELETE FROM mahasiswa WHERE nim = '" + selectedNimMahasiswa + "'";
        database.insertUpdateDeleteQuery(delSql);

        // update tabel
        mahasiswaTable.setModel(setTable());

        // bersihkan form
        clearForm();

        // feedback
        System.out.println("Delete berhasil!");
        JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
    }

    public void clearForm() {
        // kosongkan semua texfield dan combo box
        nimField.setText("");
        namaField.setText("");
        jenisKelaminComboBox.setSelectedItem("");

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");
        // sembunyikan button delete
        deleteButton.setVisible(false);
        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
    }
}
