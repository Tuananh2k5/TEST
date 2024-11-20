/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Main;

import com.github.lgooddatepicker.components.DatePickerSettings;
import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Lenovo
 */
public class UserDashboard extends javax.swing.JFrame {
    public String AccountName;
    /**
     * Creates new form User_DEMO
     */
    public UserDashboard(String AccountName) {
        this.AccountName = AccountName;
        this.con = Database.getInstance().getConnection();
        initComponents();
        UserGreeting_Load();
    }
    
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
//    public void Connect(){
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//                con = DriverManager.getConnection("jdbc:mysql://localhost:3308/" + Database.DB_Name,Database.DB_UserName,Database.DB_Password);
//        } catch (ClassNotFoundException | SQLException ex) {
//            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    } 
    
//Load UserGreeting in
    private void UserGreeting_Load() {
//        Recent_Issue_Load();
        user_name.setText("Welcome, User " + AccountName);
        account_name.setText(AccountName);
        try {
            String sql_cmd = "SELECT Email,ContactNumber FROM accounts WHERE AccountName = ?";
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1,AccountName);
            rs = pst.executeQuery();
            rs.next();
            email.setText(rs.getString(1));
            contact_number.setText(rs.getString(2));
            Home_page_Load();
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
  
//Initialize Home_page
    private void Home_page_Load() {
        Status_issueLoad();
        View_book_Load();
    }
//Load count of statsus issue
    private void Status_issueLoad(){
        int count_borrowing_book = 0;
        int count_returning_book = 0;
        int count_overtime_book = 0;
        String sql_cmd = "SELECT issuebooks.Status "
                + " FROM issuebooks "
                + " JOIN books ON issuebooks.Book_ID = books.Book_ID "
                + " JOIN accounts ON issuebooks.UserName = accounts.AccountName "
                + " WHERE accounts.AccountName = ? ";
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1, AccountName);
            rs = pst.executeQuery();
            while(rs.next()){
                String status = rs.getString(1);
                if(status.equals("Borrowed") == true){
                    ++ count_borrowing_book;
                }
                else{
                    if(status.equals("Returned") == true){
                        ++ count_returning_book;
                    }
                    else{
                        if(status.equals("OverTime") == true){
                            ++ count_overtime_book;
                        }
                    }
                }
            }
            borrowedNumber.setText("   " + String.valueOf(count_borrowing_book));
            returnedNumber.setText("   " + String.valueOf(count_returning_book));
            overtimeNumber.setText("   " + String.valueOf(count_overtime_book));
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//Load recent_issue_table in Home_page
    private void View_book_Load() {
        String sql_cmd = "SELECT issuebooks.Book_ID,books.Name,issuebooks.IssueDate,issuebooks.DueDate,issuebooks.Status "
                + "FROM issuebooks "
                + " JOIN books ON issuebooks.Book_ID = books.Book_ID "
                + " JOIN accounts ON issuebooks.UserName = accounts.AccountName "
                + "WHERE accounts.AccountName = ?";
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1, AccountName);
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel)view_book_jscroll.getModel();
            model.setNumRows(0);
            while(rs.next()) {
                Object[] obj = {rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)};
                model.addRow(obj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
   
//Turn off all button
    private void TurnOffButtons() {
        Color color = new Color(255,219,150);
        home_page_button.setBackground(color);
        edit_profile_button.setBackground(color);
        change_password_button.setBackground(color);
        myBookShelf_button.setBackground(color);
        goToLibrary_button.setBackground(color);
        logout_button.setBackground(color);
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
    
//Initialize changepassword_panel
    private void Changepassword_panelLoad() {
        Changepassword_buttonLoad();
        Changepassword_TextandComboboxLoad();
    }   
//Load button_status when initialize category_panel
    private void Changepassword_buttonLoad() {
        Submit_button.setEnabled(true);
    }
//Load text and combobox status when initialize category_panel
    private void Changepassword_TextandComboboxLoad() {
        txtpassword.setText("");
        txtpassword1.setText("");
        txtpassword2.setText("");
    }
//Change password where condition is pass
    private void Change_password_Databases(String newPass){
        String sql_cmd = "UPDATE accounts SET PassWord = ? WHERE AccountName = ?";
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1,newPass);
            pst.setString(2,AccountName);
            pst.executeUpdate(); 
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//Function help checking change_password
    private void Change_password(String correctCurPass,String curPass, String newPass, String confirmPass){
        if(correctCurPass.equals("") == true || correctCurPass.equals("") == true || correctCurPass.equals("") == true || correctCurPass.equals("") == true){
            JOptionPane.showMessageDialog(this, "The password field cannot be left empty");
        }
        else{
            if(correctCurPass.equals(curPass) == false){
                JOptionPane.showMessageDialog(this, "IncorrectPassword");
            }
            else{
                if(newPass.equals(confirmPass) == false){
                    JOptionPane.showMessageDialog(this, "newPass and confirmPass is not the same");
                }
                else{
                    if(curPass.equals(confirmPass) == true){
                        JOptionPane.showMessageDialog(this, "Old password must differ from new password");
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "Submit successfully");
                        Change_password_Databases(newPass);
                    }
                }
            }
        }
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
    
//Initialize edit profile panel
    private void Editprofile_panelLoad(){
        Editprofile_buttonLoad();
        Editprofile_TextandComboboxLoad();
    }
//Load button_status when initialize  edit profile panel
    private void Editprofile_buttonLoad() {
        editprofile_saveButton.setEnabled(true);
        editprofile_resetButton.setEnabled(true);
    } 
//Load text and combobox status when initialize edit profile panel
    private void Editprofile_TextandComboboxLoad() {
        account_name_label.setText(AccountName);
        try {
                String sql_cmd = "SELECT ContactNumber,Email FROM accounts WHERE AccountName = ? ";
                pst = con.prepareStatement(sql_cmd);
                pst.setString(1,AccountName);
                rs = pst.executeQuery();
                rs.next();
                contact_number_label.setText(rs.getString(1));
                email_label.setText(rs.getString(2));
                
                contact_number_label.setText(rs.getString(1));
                email_label.setPlaceholder(rs.getString(2));
            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }   
    }
//***************************************************
//Check a string Email is a valid Email.
    private boolean check_validEmail(String email){
        int indexFirst = email.indexOf('@');
        if(email.substring(indexFirst).equals("@gmail.com") == false || Character.isLetter(email.charAt(0)) == false || indexFirst == -1 || email.length() < 10){
            return false;
        }
        else{
            return true;
        }
    }
//Check a string contact number is a valid number.
    private boolean check_validContactNumber(String contactNumber){
        for(int i=0;i<contactNumber.length();++i){
            if(Character.isDigit(contactNumber.charAt(i)) == false){
                return false;
            }
        }
        return true;
    }
//Change CONTACT NUMBER indatabases when edit profile
    private void change_contactNumber_DB(String old_contactNumber, String new_contactNumber){
        if(check_validContactNumber(new_contactNumber) == false || old_contactNumber.equals(new_contactNumber) == true || new_contactNumber.equals("") == true) return;
        else{
            try {
                String sql_cmd = "UPDATE accounts SET ContactNumber = ? WHERE AccountName = ? ";
                pst = con.prepareStatement(sql_cmd);
                pst.setString(1,new_contactNumber);
                pst.setString(2,AccountName);
                pst.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
//Change EMAIL in databases when edit profile
    private void change_email_DB(String old_mail, String new_email){
        if(check_validEmail(new_email) == false || old_mail.equals(new_email) == true) return;
        else{
            try {
                String sql_cmd = "UPDATE accounts SET Email = ? WHERE AccountName = ? ";
                pst = con.prepareStatement(sql_cmd);
                pst.setString(1,new_email);
                pst.setString(2,AccountName);
                pst.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
//Check and edit information
    private boolean check_and_edit_information(String old_contactNumber, String new_contactNumber, String old_email, String new_email){
        if(new_contactNumber.equals("") == true || new_email.equals("") == true || check_validEmail(new_email) == false || check_validContactNumber(new_contactNumber) == false) return false;
        else{
            change_email_DB(old_email,new_email);
            change_contactNumber_DB(old_contactNumber,new_contactNumber);
            return true;
        }
    }
    
//---------------------------------------------------------------------------------------------------------------------------------------------
//  0. Load greetingSearchPanel
    private void SearchBook_greetingLoad(){
        id_text.setText("");
        bookName_text.setText("");
        ngayMuon_date.setDate(LocalDate.of(2000,1,1));
        ngayTra_date.setDate(LocalDate.of(2100,12,31));
        trangThai_cbbox.setSelectedIndex(0);
    }
//Initialize BOOK SHELF - SEARCH BOOK
    private void Searchbook_panelLoad() {
        SearchBook_settingFormat();
        SearchBook_panel_Load();
        SearchBook_view_bookJSC_Load();
    }
//  1.1 Load All Setting format
    private void SearchBook_settingFormat() {
        DatePickerSettings dateSettings1 = new DatePickerSettings();// Khởi tạo 1 biến setting
        DatePickerSettings dateSettings2 = new DatePickerSettings();// Khởi tạo 1 biến setting
        dateSettings1.setFormatForDatesCommonEra("dd/MM/yyyy");     // Đặt định dạng cho DatePicker
        dateSettings2.setFormatForDatesCommonEra("dd/MM/yyyy");     // Đặt định dạng cho DatePicker
        ngayMuon_date.setSettings(dateSettings1);
        ngayTra_date.setSettings(dateSettings2);
    }
//  1.2 Load buttons, text and comboBox of Search panel
    private void SearchBook_panel_Load() {
        SearchBook_buttonLoad();
        SearchBook_textLoad();
    }
//  1.3 Load jscrool which review book's information
    private void SearchBook_view_bookJSC_Load() {
        String sql_cmd = " SELECT issuebooks.Book_ID,books.Name,issuebooks.IssueDate,issuebooks.DueDate,issuebooks.Status "
                + " FROM issuebooks "
                + " JOIN books ON issuebooks.Book_ID = books.Book_ID "
                + " JOIN accounts ON issuebooks.UserName = accounts.AccountName "
                + " WHERE accounts.AccountName = ? AND issuebooks.Book_ID LIKE ? AND books.Name LIKE ? AND issuebooks.IssueDate >= ? AND issuebooks.DueDate <= ? ";
        String cur_Status = (String) trangThai_cbbox.getSelectedItem();
        if ((cur_Status).equals("All") == false) {
            String extraString = " AND issuebooks.Status = ?";
            sql_cmd += extraString;
        }
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1, AccountName);
            pst.setString(2, "%" + id_text.getText() + "%");
            pst.setString(3, "%" + bookName_text.getText() + "%");
            pst.setString(4, String.format("%d-%d-%d", ngayMuon_date.getDate().getYear(), ngayMuon_date.getDate().getMonthValue(), ngayMuon_date.getDate().getDayOfMonth()));
            pst.setString(5, String.format("%d-%d-%d", ngayTra_date.getDate().getYear(), ngayTra_date.getDate().getMonthValue(), ngayTra_date.getDate().getDayOfMonth()));
            if ((cur_Status).equals("All") == false) {
                pst.setString(6, cur_Status);
            }
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) view_book_search_jscroll.getModel();
            model.setNumRows(0);
            while (rs.next()) {
                Object[] obj = {rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)};
                model.addRow(obj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//  1.2.1 Load buttons of search panel
    private void SearchBook_buttonLoad() {
        traSach_button.setEnabled(true);
        timKiem_button.setEnabled(true);
        toanBo_button.setEnabled(true);
    }

//  1.2.2 Load text od search panel
    private void SearchBook_textLoad() {

    }
//******************************************************************************
    //  *** Return Book 
//  1. Check What Row is chosen
    private int indexOfRow() {// trả về thứ tự của dòng trong bảng bắt đầu từ 0, nếu không có dòng nào được chọn thì trả về - 1
        return view_book_jscroll.getSelectedRow();
    }
    //  2. Return Book
    private boolean returnBook() {
        int indexBook = indexOfRow();

        if (indexOfRow() == -1 || view_book_jscroll.getValueAt(indexBook, 4).equals("Returned")) {
            return false;
        }

        String sql_cmd = "UPDATE issuebooks SET issuebooks.Status = ? WHERE issuebooks.Book_ID = ? ";
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1, "Returned");
            pst.setString(2, (String) view_book_jscroll.getValueAt(indexBook, 0));
            if(pst.executeUpdate() > 0){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
//*****************************************************************************
//Check valid Date -- Condition : ngayMuon >= ngayTra && ngayMuon >= 01/01/2000 && ngayTra <= 31/12/2100
    private boolean Check_searchDate() {
        if (ngayMuon_date.getDate().isBefore(LocalDate.of(2000, 01, 01)) == true || ngayTra_date.getDate().isAfter(LocalDate.of(2100, 12, 31)) == true || ngayMuon_date.getDate().isAfter(ngayTra_date.getDate()) == true) {
            return false;
        } else return true;
    }
//--------------------------------------------------------------------------------------------------------------------------------------
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        user_name = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        edit_profile_button = new javax.swing.JButton();
        home_page_button = new javax.swing.JButton();
        logout_button = new javax.swing.JButton();
        change_password_button = new javax.swing.JButton();
        myBookShelf_button = new javax.swing.JButton();
        goToLibrary_button = new javax.swing.JButton();
        Parent_panel = new javax.swing.JPanel();
        home_page_panel = new javax.swing.JPanel();
        JPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        returnedNumber = new javax.swing.JLabel();
        JLabel = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        borrowedNumber = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        overtimeNumber = new javax.swing.JLabel();
        j = new javax.swing.JLabel();
        Jlabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        view_book_jscroll = new rojeru_san.complementos.RSTableMetro();
        jPanel4 = new javax.swing.JPanel();
        icon_member = new javax.swing.JLabel();
        member_infor_label = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        account_name = new javax.swing.JLabel();
        contact_number = new javax.swing.JLabel();
        email = new javax.swing.JLabel();
        changepassword_panel = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        txtpassword = new app.bolivia.swing.JCTextField();
        txtpassword1 = new app.bolivia.swing.JCTextField();
        txtpassword2 = new app.bolivia.swing.JCTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        Submit_button = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        editprofile_panel = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        editprofile_saveButton = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        contact_number_label = new app.bolivia.swing.JCTextField();
        email_label = new app.bolivia.swing.JCTextField();
        jPanel10 = new javax.swing.JPanel();
        account_name_label = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        editprofile_resetButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        searchbook_panel = new javax.swing.JPanel();
        view_search_book_jscroll = new javax.swing.JScrollPane();
        view_book_search_jscroll = new rojeru_san.complementos.RSTableMetro();
        search_panel = new javax.swing.JPanel();
        tim_kiem_panel = new javax.swing.JLabel();
        book_id_label = new javax.swing.JLabel();
        book_name_label = new javax.swing.JLabel();
        trang_thai_panel = new javax.swing.JLabel();
        id_text = new app.bolivia.swing.JCTextField();
        bookName_text = new app.bolivia.swing.JCTextField();
        ngayMuon_label = new javax.swing.JLabel();
        ngayTra_label = new javax.swing.JLabel();
        ngayTra_date = new com.github.lgooddatepicker.components.DatePicker();
        ngayMuon_date = new com.github.lgooddatepicker.components.DatePicker();
        timKiem_button = new javax.swing.JButton();
        traSach_button = new javax.swing.JButton();
        trangThai_cbbox = new javax.swing.JComboBox<>();
        toanBo_button = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(73, 101, 128));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/menu_50px.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, -1, 50));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Library Management System");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 310, 30));

        user_name.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        user_name.setForeground(new java.awt.Color(255, 255, 255));
        user_name.setText("Welcome, User ");
        jPanel2.add(user_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 10, 150, 30));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/member_off_off_50px.png"))); // NOI18N
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 0, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tw Cen MT", 1, 36)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/cancel_red_50px.png"))); // NOI18N
        jLabel6.setText("X");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 10, 40, 30));

        jPanel3.setBackground(new java.awt.Color(73, 101, 128));
        jPanel3.setForeground(new java.awt.Color(51, 51, 51));

        edit_profile_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/change_off_50.png"))); // NOI18N
        edit_profile_button.setText("    Edit Profile");
        edit_profile_button.setBackground(new java.awt.Color(255, 219, 150));
        edit_profile_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        edit_profile_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        edit_profile_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_profile_buttonActionPerformed(evt);
            }
        });

        home_page_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/home_50px.png"))); // NOI18N
        home_page_button.setText("   Home Page");
        home_page_button.setBackground(new java.awt.Color(255, 219, 150));
        home_page_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        home_page_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        home_page_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                home_page_buttonActionPerformed(evt);
            }
        });

        logout_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/logout_50px.png"))); // NOI18N
        logout_button.setText("     Logout");
        logout_button.setBackground(new java.awt.Color(255, 219, 150));
        logout_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        logout_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        logout_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logout_buttonActionPerformed(evt);
            }
        });

        change_password_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/change_pass_50px.png"))); // NOI18N
        change_password_button.setText("Change Password");
        change_password_button.setBackground(new java.awt.Color(255, 219, 150));
        change_password_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        change_password_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        change_password_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                change_password_buttonActionPerformed(evt);
            }
        });

        myBookShelf_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/book_shelf_50px.png"))); // NOI18N
        myBookShelf_button.setText("My BookShelf");
        myBookShelf_button.setBackground(new java.awt.Color(255, 219, 150));
        myBookShelf_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        myBookShelf_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        myBookShelf_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myBookShelf_buttonActionPerformed(evt);
            }
        });

        goToLibrary_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/library_2_50px.png"))); // NOI18N
        goToLibrary_button.setText("Go to library");
        goToLibrary_button.setBackground(new java.awt.Color(255, 219, 150));
        goToLibrary_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        goToLibrary_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        goToLibrary_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToLibrary_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 19, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(edit_profile_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(home_page_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(change_password_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(logout_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(myBookShelf_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(goToLibrary_button, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 19, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(home_page_button, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(edit_profile_button, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(change_password_button, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(myBookShelf_button, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(goToLibrary_button, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(logout_button, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        Parent_panel.setLayout(new java.awt.CardLayout());

        home_page_panel.setBackground(new java.awt.Color(186, 221, 255));
        home_page_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        JPanel.setBackground(new java.awt.Color(186, 221, 255));

        jPanel8.setBackground(new java.awt.Color(186, 221, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        returnedNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/return_book_50px.png"))); // NOI18N
        returnedNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        returnedNumber.setForeground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(returnedNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(returnedNumber)
                .addGap(26, 26, 26))
        );

        JLabel.setText("Sách đang mượn");
        JLabel.setBackground(new java.awt.Color(186, 221, 255));
        JLabel.setFont(new java.awt.Font("Segoe UI", 3, 20)); // NOI18N

        jPanel9.setBackground(new java.awt.Color(186, 221, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        borrowedNumber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        borrowedNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/book_50px.png"))); // NOI18N
        borrowedNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        borrowedNumber.setForeground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(borrowedNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(borrowedNumber)
                .addGap(26, 26, 26))
        );

        jPanel11.setBackground(new java.awt.Color(186, 221, 255));
        jPanel11.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        overtimeNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/over_time_50px.png"))); // NOI18N
        overtimeNumber.setBackground(new java.awt.Color(186, 221, 255));
        overtimeNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        overtimeNumber.setForeground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(overtimeNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(overtimeNumber)
                .addGap(16, 16, 16))
        );

        j.setText("Sách quá hạn");
        j.setBackground(new java.awt.Color(186, 221, 255));
        j.setFont(new java.awt.Font("Segoe UI", 3, 20)); // NOI18N

        Jlabel.setText("Sách đã trả");
        Jlabel.setBackground(new java.awt.Color(186, 221, 255));
        Jlabel.setFont(new java.awt.Font("Segoe UI", 3, 20)); // NOI18N

        view_book_jscroll.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "BookID", "BookName", "NgayMuon", "NgayTra", "TrangThai"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        view_book_jscroll.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        view_book_jscroll.setRowHeight(50);
        view_book_jscroll.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        view_book_jscroll.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(view_book_jscroll);
        if (view_book_jscroll.getColumnModel().getColumnCount() > 0) {
            view_book_jscroll.getColumnModel().getColumn(0).setResizable(false);
            view_book_jscroll.getColumnModel().getColumn(1).setResizable(false);
            view_book_jscroll.getColumnModel().getColumn(2).setResizable(false);
            view_book_jscroll.getColumnModel().getColumn(3).setResizable(false);
            view_book_jscroll.getColumnModel().getColumn(4).setResizable(false);
        }

        jPanel4.setBackground(new java.awt.Color(186, 221, 255));

        icon_member.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/member_off_50px.png"))); // NOI18N
        icon_member.setForeground(new java.awt.Color(102, 102, 102));

        member_infor_label.setText("Member Information");
        member_infor_label.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N

        jLabel3.setText("AccountName :");
        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel4.setText("ContactNumber :");
        jLabel4.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel10.setText("Email :");
        jLabel10.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        account_name.setText("_accountname_");
        account_name.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        contact_number.setText("_contactmnumber_");
        contact_number.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        email.setText("_email_");
        email.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(icon_member)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(member_infor_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(62, 62, 62))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contact_number, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(account_name, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(email, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(6, 6, 6))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(icon_member)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(member_infor_label, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(account_name))
                .addGap(17, 17, 17)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(contact_number))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(email)
                    .addComponent(jLabel10))
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout JPanelLayout = new javax.swing.GroupLayout(JPanel);
        JPanel.setLayout(JPanelLayout);
        JPanelLayout.setHorizontalGroup(
            JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(JLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Jlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(j, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(JPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 876, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 31, Short.MAX_VALUE))
        );
        JPanelLayout.setVerticalGroup(
            JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanelLayout.createSequentialGroup()
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(Jlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(j, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(JLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE))
        );

        home_page_panel.add(JPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 880, 640));

        Parent_panel.add(home_page_panel, "card2");

        changepassword_panel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/quenmatkhau.png"))); // NOI18N

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/change.png"))); // NOI18N

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        txtpassword.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtpassword.setPlaceholder("Enter Password .....");

        txtpassword1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtpassword1.setPlaceholder("Enter New Password");

        txtpassword2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtpassword2.setPlaceholder("Confirm Password");

        jLabel14.setText("Current password");
        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel15.setText("* ");
        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(223, 25, 25));

        jLabel16.setText("Confirm password");
        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel17.setText("* ");
        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(223, 25, 25));

        jLabel18.setText("New password");
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel19.setText("* ");
        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(223, 25, 25));

        Submit_button.setBackground(new java.awt.Color(127, 186, 0));
        Submit_button.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        Submit_button.setForeground(new java.awt.Color(255, 255, 255));
        Submit_button.setText("Submit");
        Submit_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Submit_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Submit_buttonActionPerformed(evt);
            }
        });

        jLabel28.setText("Change password");
        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N

        jLabel29.setText("Update password for enhanced account security");
        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(121, 121, 121));

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/reset_pass_50px.png"))); // NOI18N

        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/password_icon_50px.png"))); // NOI18N

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/key_50px.png"))); // NOI18N

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/idea_50px.png"))); // NOI18N

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/confirm_50px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtpassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtpassword1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtpassword2, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel29)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel28)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel31))))))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(Submit_button, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel30)
                        .addGap(48, 48, 48))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jLabel28))
                            .addComponent(jLabel31))
                        .addGap(8, 8, 8)
                        .addComponent(jLabel29)
                        .addGap(43, 43, 43)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtpassword, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtpassword1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtpassword2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(Submit_button, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout changepassword_panelLayout = new javax.swing.GroupLayout(changepassword_panel);
        changepassword_panel.setLayout(changepassword_panelLayout);
        changepassword_panelLayout.setHorizontalGroup(
            changepassword_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changepassword_panelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(changepassword_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        changepassword_panelLayout.setVerticalGroup(
            changepassword_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changepassword_panelLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel20)
                .addGap(66, 66, 66))
            .addGroup(changepassword_panelLayout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(87, Short.MAX_VALUE))
        );

        Parent_panel.add(changepassword_panel, "card3");

        editprofile_panel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setText("AccountName ( Can't change ) : ");
        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel11.setText("ContactNumber : ");
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel12.setText("Email : ");
        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        editprofile_saveButton.setBackground(new java.awt.Color(127, 186, 0));
        editprofile_saveButton.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        editprofile_saveButton.setForeground(new java.awt.Color(255, 255, 255));
        editprofile_saveButton.setText("Save");
        editprofile_saveButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        editprofile_saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editprofile_saveButtonActionPerformed(evt);
            }
        });

        jLabel13.setText("Edit profile");
        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/email_50px.png"))); // NOI18N

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/member_off_off_50px.png"))); // NOI18N

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/call_50px.png"))); // NOI18N

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/edit_pro5_64px.png"))); // NOI18N

        jLabel26.setText("Update contactnumber and email for better security");
        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(121, 121, 121));

        contact_number_label.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        contact_number_label.setPlaceholder("Contact Number . . .");

        email_label.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        email_label.setPlaceholder("Email . . . ");

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        account_name_label.setText("_AccountName_");
        account_name_label.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(account_name_label)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(account_name_label, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/boyreadingbook_490.png"))); // NOI18N

        editprofile_resetButton.setBackground(new java.awt.Color(127, 186, 0));
        editprofile_resetButton.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        editprofile_resetButton.setForeground(new java.awt.Color(255, 255, 255));
        editprofile_resetButton.setText("Reset");
        editprofile_resetButton.setActionCommand("Reset\n");
        editprofile_resetButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        editprofile_resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editprofile_resetButtonActionPerformed(evt);
            }
        });

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/girl_120.png"))); // NOI18N

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/idea_girl_50px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jLabel23))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel26)))
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addGap(155, 155, 155))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel12Layout.createSequentialGroup()
                                    .addGap(19, 19, 19)
                                    .addComponent(editprofile_saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(48, 48, 48)
                                    .addComponent(editprofile_resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(email_label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(contact_number_label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE))))
                        .addGap(16, 16, 16)
                        .addComponent(jLabel35)
                        .addGap(12, 12, 12))))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel26))
                            .addComponent(jLabel8))
                        .addGap(46, 46, 46)
                        .addComponent(jLabel9))
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel21)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contact_number_label, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel23)
                                    .addComponent(email_label, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGap(97, 97, 97)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(editprofile_resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(editprofile_saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jLabel35))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout editprofile_panelLayout = new javax.swing.GroupLayout(editprofile_panel);
        editprofile_panel.setLayout(editprofile_panelLayout);
        editprofile_panelLayout.setHorizontalGroup(
            editprofile_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        editprofile_panelLayout.setVerticalGroup(
            editprofile_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editprofile_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        Parent_panel.add(editprofile_panel, "card4");

        searchbook_panel.setBackground(new java.awt.Color(186, 221, 255));

        view_book_search_jscroll.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "BookID", "BookName", "NgayMuon", "NgayTra", "TrangThai"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        view_book_search_jscroll.setColorFilasBackgound2(new java.awt.Color(255, 204, 153));
        view_book_search_jscroll.setRowHeight(50);
        view_book_search_jscroll.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        view_search_book_jscroll.setViewportView(view_book_search_jscroll);

        search_panel.setBackground(new java.awt.Color(255, 255, 255));

        tim_kiem_panel.setText("Search");
        tim_kiem_panel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N

        book_id_label.setText("Book_ID :");
        book_id_label.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        book_name_label.setText("Book_Name :");
        book_name_label.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        trang_thai_panel.setText("TrangThai :");
        trang_thai_panel.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        id_text.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 112, 192), 2));
        id_text.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        id_text.setPlaceholder("ID . . .");

        bookName_text.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 112, 192), 2));
        bookName_text.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        bookName_text.setPlaceholder("Name . . .");

        ngayMuon_label.setText("IssueDate :");
        ngayMuon_label.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        ngayTra_label.setText("DueDate :");
        ngayTra_label.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        ngayTra_date.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        ngayMuon_date.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        timKiem_button.setText("Search");
        timKiem_button.setBackground(new java.awt.Color(0, 112, 192));
        timKiem_button.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        timKiem_button.setForeground(new java.awt.Color(255, 255, 255));
        timKiem_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timKiem_buttonActionPerformed(evt);
            }
        });

        traSach_button.setText("Return ");
        traSach_button.setBackground(new java.awt.Color(0, 112, 192));
        traSach_button.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        traSach_button.setForeground(new java.awt.Color(255, 255, 255));
        traSach_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                traSach_buttonActionPerformed(evt);
            }
        });

        trangThai_cbbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Borrowed", "Returned", "OverTime", " " }));
        trangThai_cbbox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 112, 192), 2));
        trangThai_cbbox.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        toanBo_button.setText("All books");
        toanBo_button.setBackground(new java.awt.Color(0, 112, 192));
        toanBo_button.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        toanBo_button.setForeground(new java.awt.Color(255, 255, 255));
        toanBo_button.setToolTipText("");
        toanBo_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toanBo_buttonActionPerformed(evt);
            }
        });

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/search_50px.png"))); // NOI18N

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/book_search_50px.png"))); // NOI18N

        javax.swing.GroupLayout search_panelLayout = new javax.swing.GroupLayout(search_panel);
        search_panel.setLayout(search_panelLayout);
        search_panelLayout.setHorizontalGroup(
            search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(search_panelLayout.createSequentialGroup()
                .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(search_panelLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36))
                    .addGroup(search_panelLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(tim_kiem_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(13, 13, 13)
                .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(book_id_label, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(trang_thai_panel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(book_name_label, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(search_panelLayout.createSequentialGroup()
                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(bookName_text, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(trangThai_cbbox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(search_panelLayout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(ngayTra_label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ngayTra_date, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(search_panelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(toanBo_button, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(timKiem_button, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(traSach_button, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, search_panelLayout.createSequentialGroup()
                        .addComponent(id_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(ngayMuon_label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ngayMuon_date, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        search_panelLayout.setVerticalGroup(
            search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, search_panelLayout.createSequentialGroup()
                .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(search_panelLayout.createSequentialGroup()
                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(search_panelLayout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(book_id_label)
                                    .addComponent(id_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(search_panelLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(tim_kiem_panel)))
                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(search_panelLayout.createSequentialGroup()
                                .addGap(18, 65, Short.MAX_VALUE)
                                .addComponent(jLabel22))
                            .addGroup(search_panelLayout.createSequentialGroup()
                                .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(search_panelLayout.createSequentialGroup()
                                        .addGap(27, 27, 27)
                                        .addComponent(jLabel36))
                                    .addGroup(search_panelLayout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(bookName_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(book_name_label))))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(search_panelLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ngayMuon_date, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ngayMuon_label))
                        .addGap(33, 33, 33)
                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ngayTra_date, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ngayTra_label))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(trangThai_cbbox, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(trang_thai_panel)
                            .addComponent(toanBo_button, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(timKiem_button, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(traSach_button, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout searchbook_panelLayout = new javax.swing.GroupLayout(searchbook_panel);
        searchbook_panel.setLayout(searchbook_panelLayout);
        searchbook_panelLayout.setHorizontalGroup(
            searchbook_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(view_search_book_jscroll)
            .addComponent(search_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        searchbook_panelLayout.setVerticalGroup(
            searchbook_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchbook_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(search_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(view_search_book_jscroll, javax.swing.GroupLayout.PREFERRED_SIZE, 448, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Parent_panel.add(searchbook_panel, "card5");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1140, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addGap(0, 267, Short.MAX_VALUE)
                    .addComponent(Parent_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 873, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addGap(0, 56, Short.MAX_VALUE)
                    .addComponent(Parent_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 668, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jLabel6MouseClicked

    private void edit_profile_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_profile_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(editprofile_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Editprofile_panelLoad();
        TurnOffButtons();
        edit_profile_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_edit_profile_buttonActionPerformed

    private void home_page_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_home_page_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(home_page_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Home_page_Load();
        TurnOffButtons();
        home_page_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_home_page_buttonActionPerformed

    private void logout_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logout_buttonActionPerformed
        // TODO add your handling code here:
        this.dispose();
        new Login().setVisible(true);
    }//GEN-LAST:event_logout_buttonActionPerformed

    private void change_password_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_change_password_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(changepassword_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Changepassword_panelLoad();
        TurnOffButtons();
        change_password_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_change_password_buttonActionPerformed

    private void Submit_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Submit_buttonActionPerformed
        // TODO add your handling code here:
        String curPass = txtpassword.getText();
        String newPass = txtpassword1.getText();
        String confirmPass = txtpassword2.getText();
        
        try {
            String sql_cmd;
            sql_cmd = "SELECT PassWord FROM accounts WHERE AccountName = ? ";
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1,AccountName);
            rs = pst.executeQuery();
            rs.next();
            String correctPass = rs.getString(1);
            Change_password(correctPass,curPass,newPass,confirmPass);
            
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Submit_buttonActionPerformed

    private void editprofile_saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editprofile_saveButtonActionPerformed
        // TODO add your handling code here:
        String old_contact = null;
        String old_email = null;
        try {
            String sql_cmd;
            sql_cmd = "SELECT ContactNumber,Email FROM accounts WHERE AccountName = ? ";
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1,AccountName);
            rs = pst.executeQuery();
            rs.next();
            old_contact = rs.getString(1);
            old_email = rs.getString(2);
            if(check_and_edit_information(old_contact,contact_number_label.getText(),old_email,email_label.getText())){
                JOptionPane.showMessageDialog(this, "Edit successfully");
            }
            else{
                JOptionPane.showMessageDialog(this, "Error");
                contact_number_label.setText(old_contact);
                email_label.setText(old_email);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_editprofile_saveButtonActionPerformed

    private void editprofile_resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editprofile_resetButtonActionPerformed
        // TODO add your handling code here:
        String old_contact = null;
        String old_email = null;
        try {
            String sql_cmd;
            sql_cmd = "SELECT ContactNumber,Email FROM accounts WHERE AccountName = ? ";
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1,AccountName);
            rs = pst.executeQuery();
            rs.next();
            old_contact = rs.getString(1);
            old_email = rs.getString(2);
            contact_number_label.setText(old_contact);
            email_label.setText(old_email);
            contact_number_label.setPlaceholder(old_contact);
            email_label.setPlaceholder(old_email);
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_editprofile_resetButtonActionPerformed

    private void timKiem_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timKiem_buttonActionPerformed
        // TODO add your handling code here:
        if (!Check_searchDate()) {
            JOptionPane.showMessageDialog(this, "Error in Date");
        } else {
            Searchbook_panelLoad();
        }
    }//GEN-LAST:event_timKiem_buttonActionPerformed

    private void traSach_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_traSach_buttonActionPerformed
        // TODO add your handling code here:
        if(!returnBook()){
            JOptionPane.showMessageDialog(this,"Index = -1 OR this book is returned");
        }
        else JOptionPane.showMessageDialog(this,"Return successfully");
        Searchbook_panelLoad();
    }//GEN-LAST:event_traSach_buttonActionPerformed

    private void toanBo_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toanBo_buttonActionPerformed
        // TODO add your handling code here:
        SearchBook_greetingLoad();
        Searchbook_panelLoad();
    }//GEN-LAST:event_toanBo_buttonActionPerformed

    private void myBookShelf_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myBookShelf_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(searchbook_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        SearchBook_greetingLoad();
        Searchbook_panelLoad();
        TurnOffButtons();
        myBookShelf_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_myBookShelf_buttonActionPerformed

    private void goToLibrary_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToLibrary_buttonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_goToLibrary_buttonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UserDashboard("user").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel JLabel;
    private javax.swing.JPanel JPanel;
    private javax.swing.JLabel Jlabel;
    private javax.swing.JPanel Parent_panel;
    private javax.swing.JButton Submit_button;
    private javax.swing.JLabel account_name;
    private javax.swing.JLabel account_name_label;
    private app.bolivia.swing.JCTextField bookName_text;
    private javax.swing.JLabel book_id_label;
    private javax.swing.JLabel book_name_label;
    private javax.swing.JLabel borrowedNumber;
    private javax.swing.JButton change_password_button;
    private javax.swing.JPanel changepassword_panel;
    private javax.swing.JLabel contact_number;
    private app.bolivia.swing.JCTextField contact_number_label;
    private javax.swing.JButton edit_profile_button;
    private javax.swing.JPanel editprofile_panel;
    private javax.swing.JButton editprofile_resetButton;
    private javax.swing.JButton editprofile_saveButton;
    private javax.swing.JLabel email;
    private app.bolivia.swing.JCTextField email_label;
    private javax.swing.JButton goToLibrary_button;
    private javax.swing.JButton home_page_button;
    private javax.swing.JPanel home_page_panel;
    private javax.swing.JLabel icon_member;
    private app.bolivia.swing.JCTextField id_text;
    private javax.swing.JLabel j;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton logout_button;
    private javax.swing.JLabel member_infor_label;
    private javax.swing.JButton myBookShelf_button;
    private com.github.lgooddatepicker.components.DatePicker ngayMuon_date;
    private javax.swing.JLabel ngayMuon_label;
    private com.github.lgooddatepicker.components.DatePicker ngayTra_date;
    private javax.swing.JLabel ngayTra_label;
    private javax.swing.JLabel overtimeNumber;
    private javax.swing.JLabel returnedNumber;
    private javax.swing.JPanel search_panel;
    private javax.swing.JPanel searchbook_panel;
    private javax.swing.JButton timKiem_button;
    private javax.swing.JLabel tim_kiem_panel;
    private javax.swing.JButton toanBo_button;
    private javax.swing.JButton traSach_button;
    private javax.swing.JComboBox<String> trangThai_cbbox;
    private javax.swing.JLabel trang_thai_panel;
    private app.bolivia.swing.JCTextField txtpassword;
    private app.bolivia.swing.JCTextField txtpassword1;
    private app.bolivia.swing.JCTextField txtpassword2;
    private javax.swing.JLabel user_name;
    private rojeru_san.complementos.RSTableMetro view_book_jscroll;
    private rojeru_san.complementos.RSTableMetro view_book_search_jscroll;
    private javax.swing.JScrollPane view_search_book_jscroll;
    // End of variables declaration//GEN-END:variables
}
