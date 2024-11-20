/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Main;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Admin
 */

public class AdminDashboard extends javax.swing.JFrame {
    private String AccountName;
    private static final int NO_SELECTION = -1;
    class Table_status {
        public int Previous_category_selected;
        public int Previous_author_selected;
        public int Previous_publisher_selected;
        public int Previous_book_selected;
        public Table_status() {
            this.Previous_category_selected = NO_SELECTION;
            this.Previous_author_selected = NO_SELECTION;
            this.Previous_publisher_selected = NO_SELECTION;
            this.Previous_book_selected = NO_SELECTION;
        }        
    }
    Table_status table_status = new Table_status();
    /**
     * Creates new form AdminDashboard
     */
    public AdminDashboard(String AccountName) {
        this.con = Database.getInstance().getConnection();
        initComponents();
        this.AccountName = AccountName;
        AdminGreeting_Load();
        Home_page_Load();
    }
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    private void AdminGreeting_Load() {
        admin_name.setText("Welcome, Admin " + AccountName);
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
//Initialize Home_page    
    private void Home_page_Load() {
        Recent_Issue_Load();
        Most_Issued_BookLoad();
        Combination_Load();
    }
//Load recent_issue_table in Home_page
    private void Recent_Issue_Load() {
        String sql_cmd = "SELECT UserName, Name as BookName, IssueDate, Status "
                        + "FROM issuebooks ib JOIN books b ON ib.Book_ID = b.Book_ID ORDER BY ib.Issue_ID DESC LIMIT 5";
        try {
            pst = con.prepareStatement(sql_cmd);
            rs = pst.executeQuery();            
            ResultSetMetaData rsd = rs.getMetaData();
            int columns = rsd.getColumnCount();
            DefaultTableModel model = (DefaultTableModel)recent_issue.getModel();
            model.setNumRows(0);
            while(rs.next()) {
                Object[] obj = new Object[columns];
                for(int i = 0 ; i < columns ; i++){
                    obj[i] = rs.getString(i+1);
                }
                model.addRow(obj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//Load most-issued_book_table in Home_page
    private void Most_Issued_BookLoad() {
        String sql_cmd = "SELECT b.Book_ID, b.Name, c.Name, COUNT(*) as NoOfIssues "
                + "FROM issuebooks ib JOIN books b ON ib.Book_ID = b.Book_ID JOIN categories c ON c.Category_ID "
                + "GROUP BY Book_ID ORDER BY NoOfIssues DESC, ib.Issue_ID DESC LIMIT 5";
        try {
            pst = con.prepareStatement(sql_cmd);
            rs = pst.executeQuery();            
            ResultSetMetaData rsd = rs.getMetaData();
            int columns = rsd.getColumnCount();
            DefaultTableModel model = (DefaultTableModel)most_issue_books_table.getModel();
            model.setNumRows(0);
            while(rs.next()) {
                Object[] obj = new Object[columns];
                for(int i = 0 ; i < columns ; i++){
                    obj[i] = rs.getString(i+1);
                }
                model.addRow(obj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//Load combination Book_User_IssueBook in Home_page
    private void Combination_Load(){
        try {
            pst = con.prepareStatement("SELECT count(*) FROM Books UNION ALL SELECT count(*) FROM Accounts WHERE Role = \"User\" UNION ALL SELECT count(*) FROM Issuebooks");
            rs = pst.executeQuery();
            rs.next();
            BookNumber.setText("    " + rs.getString(1));
            rs.next();
            UserNumber.setText("    " + rs.getString(1));
            rs.next();
            IssueNumber.setText("    " + rs.getString(1));
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
//Turn off all button
    private void TurnOffButtons() {
        Color color = new Color(51,51,51);
        home_page_button.setBackground(color);
        manage_categories_button.setBackground(color);
        manage_authors_button.setBackground(color);
        manage_publishers_button.setBackground(color);
        manage_issues_button.setBackground(color);
        manage_books_button.setBackground(color);
        manage_users_button.setBackground(color);
        logout_button.setBackground(color);
        edit_profile_button.setBackground(color);
        change_password_button.setBackground(color);
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
//Initialize category_panel
    private void Category_panelLoad() {
        Category_IDLoad();
        Category_TableLoad();
        Category_buttonLoad();
        Category_TextandComboboxLoad();
    }   
//Load button_status when initialize category_panel
    private void Category_buttonLoad() {
        category_addButton.setEnabled(true);
        category_updateButton.setEnabled(false);
        category_deleteButton.setEnabled(false);
    }
//Load text and combobox status when initialize category_panel
    private void Category_TextandComboboxLoad() {
//        category_id.setSelectedIndex(-1);
        category_name.setText("");
        category_status.setSelectedIndex(-1);
        category_book_count.setText("");
    }
// Load category_table in category_panel
    private void Category_TableLoad(){
            
            try {
                pst = con.prepareStatement("select * from categories");
                rs = pst.executeQuery();

                ResultSetMetaData rsd = rs.getMetaData();
                int columns = rsd.getColumnCount();

                DefaultTableModel model = (DefaultTableModel)category_table.getModel();
                model.setRowCount(0);

                while(rs.next()){
                    Object[] obj = new Object[columns];
                    for(int i = 1 ; i <= columns ; i++){
                        obj[i-1] = rs.getString(i);
                    }
                    model.addRow(obj);
                }

            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }       
        }
//Load category id into category_id combobox
    private void Category_IDLoad(){
        try {
            pst = con.prepareStatement("Select * from Categories");
            rs = pst.executeQuery();
            category_id.removeAllItems();
            category_id.addItem("----");
            category_id.setSelectedIndex(0);
            while(rs.next()){
                category_id.addItem(rs.getString("Category_ID"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }           
    } 
//---------------------------------------------------------------------------------------------------------------------------------------------
//Initialize author_panel
    private void Author_panelLoad() {
        Author_TableLoad();
        Author_IDLoad();
        Author_buttonLoad();
        Author_TextandComboboxLoad();
    }   
//Load button_status when initialize author_panel
    private void Author_buttonLoad() {
        author_addButton.setEnabled(true);
        author_updateButton.setEnabled(false);
        author_deleteButton.setEnabled(false);
    }
//Load text and combobox status when initialize author_panel
    private void Author_TextandComboboxLoad() {
//        author_id.setSelectedIndex(-1);
        author_name.setText("");
        author_city.setText("");
        author_book_count.setText("");
    }
// Load author_table in author_panel
    private void Author_TableLoad(){
            try {
                pst = con.prepareStatement("select * from Authors");
                rs = pst.executeQuery();

                ResultSetMetaData rsd = rs.getMetaData();
                int columns = rsd.getColumnCount();

                DefaultTableModel model = (DefaultTableModel)author_table.getModel();
                model.setRowCount(0);

                while(rs.next()){
                    Object[] obj = new Object[columns];
                    for(int i = 1 ; i <= columns ; i++){
                        obj[i-1] = rs.getString(i);
                    }
                    model.addRow(obj);
                }

            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }       
        }
//Load author id into author_id combobox
    private void Author_IDLoad(){
        try {
            pst = con.prepareStatement("Select * from Authors");
            rs = pst.executeQuery();
            author_id.removeAllItems();
            author_id.addItem("----");
            while(rs.next()){
                author_id.addItem(rs.getString("Author_ID"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }           
    } 
    
//---------------------------------------------------------------------------------------------------------------------------------------------
//Initialize AdminDashboard
    private void Publisher_panelLoad() {
        Publisher_TableLoad();
        Publisher_IDLoad();
        Publisher_buttonLoad();
        Publisher_TextandComboboxLoad();
    }       
//Load button_status when initialize AdminDashboard
    private void Publisher_buttonLoad() {
        publisher_addButton.setEnabled(true);
        publisher_updateButton.setEnabled(false);
        publisher_deleteButton.setEnabled(false);
    }
//Load text and combobox status when initialize AdminDashboard
    private void Publisher_TextandComboboxLoad() {
//        publisher_id.setSelectedIndex(-1);
        publisher_name.setText("");
        publisher_address.setText("");
        publisher_book_count.setText("");
    }
// Load publisher_table in AdminDashboard
    private void Publisher_TableLoad(){
        try {
            pst = con.prepareStatement("select * from Publishers");
            rs = pst.executeQuery();

            ResultSetMetaData rsd = rs.getMetaData();
            int columns = rsd.getColumnCount();

            DefaultTableModel model = (DefaultTableModel)publisher_table.getModel();
            model.setRowCount(0);

            while(rs.next()){
                Object[] obj = new Object[columns];
                for(int i = 1 ; i <= columns ; i++){
                    obj[i-1] = rs.getString(i);
                }
                model.addRow(obj);
            }

        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
//Load publisher id into publisher_id combobox
    private void Publisher_IDLoad(){
        try {
            pst = con.prepareStatement("Select * from Publishers");
            rs = pst.executeQuery();
            publisher_id.removeAllItems();
            publisher_id.addItem("----");
            while(rs.next()){
                publisher_id.addItem(rs.getString("Publisher_ID"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }           
    } 
//---------------------------------------------------------------------------------------------------------------------------------------------    
    private void Issue_PanelLoad(){
        Issue_Status_LoadItem();
        Issue_TextandComboboxLoad();
        Issue_SQLQuery();
    }
    private void Issue_TextandComboboxLoad(){
        issue_searchby_book.setText("");
        issue_searchby_user.setText("");
        issue_searchby_issuedate.setDate(null);
        issue_searchby_status.setSelectedIndex(0);
        issue_sorted.setSelectedIndex(0);
    }
    private void Issue_Status_LoadItem(){
        issue_searchby_status.addItem("----");
        issue_searchby_status.addItem("Issued");
        issue_searchby_status.addItem("Returned");
        issue_searchby_status.addItem("Overdue");
    }
//MySQL Method in Issue_panel
    private void Issue_SQLQuery() {
        String baseQuery = "Select ib.Issue_ID, b.Name, ib.UserName, ib.IssueDate, ib.DueDate, ib.ActualDueDate, ib.Status "
                + "from IssueBooks ib Join Books b On ib.Book_ID = b.Book_ID Where 1=1";
        ArrayList<String> conditions = new ArrayList<>();
        ArrayList<Object> parameters = new ArrayList<>();
        boolean sortByNewestToOldest = true;
        boolean filterByUserName = true;
        boolean filterByBookName = true;
        boolean filterByIssueDate = true;
        boolean filterByStatus = true;
        
        if(issue_sorted.getSelectedItem().toString().equals("Oldest to Newest")) sortByNewestToOldest = false;
        if(issue_searchby_user.getText().equals("")) filterByUserName = false;
        if(issue_searchby_book.getText().equals("")) filterByBookName = false;
        if(issue_searchby_issuedate.getDate() == null) filterByIssueDate = false;
        if(issue_searchby_status.getSelectedIndex() == 0) filterByStatus = false;
        
        if(filterByUserName) {
            conditions.add("UserName = ? ");
            parameters.add(issue_searchby_user.getText());
        }
        if(filterByBookName) {
            conditions.add("Name = ? ");
            parameters.add(issue_searchby_book.getText());
        }
        if(filterByIssueDate) {
            conditions.add("IssueDate = ? ");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(issue_searchby_issuedate.getDate());
            parameters.add(formattedDate);
        }
        if(filterByStatus) {
            conditions.add("Status = ?");
            parameters.add(issue_searchby_status.getSelectedItem().toString());
        }
        
        for(String condition : conditions) {
            baseQuery += " AND " + condition;
        }
        
        if(sortByNewestToOldest) {
            baseQuery += " Order By Issue_ID DESC";
        } else {
            baseQuery += " Order By Issue_ID ASC";
        }

        try {
            pst = con.prepareStatement(baseQuery);
            for (int i = 0 ; i < parameters.size() ; i++) {
                pst.setObject(i+1, parameters.get(i));
            }
            rs = pst.executeQuery();
            int columns = rs.getMetaData().getColumnCount();
            DefaultTableModel model = (DefaultTableModel)issue_table.getModel();
            model.setRowCount(0);
            while(rs.next()) {
                Object[] obj = new Object[columns];
                for(int i = 0 ; i < columns ; i++) {
                    obj[i] = rs.getString(i+1);
                }
                model.addRow(obj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//---------------------------------------------------------------------------------------------------------------------------------------------    
////Initialize Book_panel
    private void Book_panelLoad() {
//        book_combobox_check.reset();
        Book_TableLoad();
        Book_ComboboxDataLoad();
        Book_buttonLoad();
        Book_TextandComboboxLoad();
    }       
//Load button_status when initialize AdminDashboard
    private void Book_buttonLoad() {
        book_addButton.setEnabled(true);
        book_updateButton.setEnabled(false);
        book_deleteButton.setEnabled(false);
//        book_category.setEnabled(false);
//        book_author.setEnabled(false);
//        book_publisher.setEnabled(false);
    }
//Load text and combobox status when initialize AdminDashboard
    private void Book_TextandComboboxLoad() {
        book_id_search.setSelectedIndex(0);
        book_name.setText("");
        book_quantity.setText("");
        book_category.setSelectedIndex(-1);
        book_author.setSelectedIndex(-1);
        book_publisher.setSelectedIndex(-1);
        book_category_search.setSelectedIndex(0);
        book_author_search.setSelectedIndex(0);
        book_publisher_search.setSelectedIndex(0);        
    }
// Load publisher_table in AdminDashboard
    private void Book_TableLoad(){
            try {
                pst = con.prepareStatement("SELECT b.Book_ID, b.Name,c.Category_ID ,c.Name as Category,a.Author_ID,a.Name as Author,p.Publisher_ID,p.Name as Publisher, b.Quantity FROM `books`b "
                        + "JOIN categories c ON b.Category_ID = c.Category_ID "
                        + "JOIN authors a ON b.Author_ID = a.Author_ID "
                        + "JOIN publishers p ON b.Publisher_ID = p.Publisher_ID");
                rs = pst.executeQuery();
                int columns = 6;
                DefaultTableModel model = (DefaultTableModel)book_table.getModel();
                model.setRowCount(0);
                while(rs.next()){
                    Object[] obj = new Object[columns];
                    obj[0] = rs.getString("Book_ID");
                    obj[1] = rs.getString("Name");
                    obj[2] = new CategoryItem(rs.getInt("Category_ID"), rs.getString("Category"));
                    obj[3] = new AuthorItem(rs.getInt("Author_ID"), rs.getString("Author"));
                    obj[4] = new PublisherItem(rs.getInt("Publisher_ID"), rs.getString("Publisher"));
                    obj[5] = rs.getString("Quantity");
                    model.addRow(obj);
                }

            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }       
        }
//Load publisher id into publisher_id combobox
    private void Book_ComboboxDataLoad(){
        try {
            book_id_search.removeAllItems();        
            book_category.removeAllItems();
            book_author.removeAllItems();
            book_publisher.removeAllItems();
            book_category_search.removeAllItems();
            book_author_search.removeAllItems();
            book_publisher_search.removeAllItems();            
            //Load book_id
            pst = con.prepareStatement("Select Book_ID from Books b");
            book_id_search.addItem("All");
//            book_combobox_check.book_id_check = true;
            rs = pst.executeQuery();
            while(rs.next()){
                book_id_search.addItem(rs.getString("Book_ID"));
            }
            //Load book_category
            pst = con.prepareStatement("SELECT Category_ID, Name FROM categories");
            rs = pst.executeQuery();
            book_category_search.addItem(new CategoryItem(-1,"All"));
//            book_combobox_check.book_category_check = true;
            while(rs.next()){
                book_category.addItem(new CategoryItem(rs.getInt("Category_ID"), rs.getString("Name")));
                book_category_search.addItem(new CategoryItem(rs.getInt("Category_ID"), rs.getString("Name")));
            }
            //Load book_author
            pst = con.prepareStatement("SELECT Author_ID, Name FROM authors");
            rs = pst.executeQuery();
            book_author_search.addItem(new AuthorItem(-1,"All"));
//            book_combobox_check.book_author_check = true;
            while(rs.next()){
                book_author.addItem(new AuthorItem(rs.getInt("Author_ID"), rs.getString("Name")));
                book_author_search.addItem(new AuthorItem(rs.getInt("Author_ID"), rs.getString("Name")));
            }
            //Load book_publisher
            pst = con.prepareStatement("SELECT Publisher_ID, Name FROM publishers");
            rs = pst.executeQuery();
            book_publisher_search.addItem(new PublisherItem(-1,"All"));
//            book_combobox_check.book_publisher_check = true;
            while(rs.next()){
                book_publisher.addItem(new PublisherItem(rs.getInt("Publisher_ID"),rs.getString("Name")));
                book_publisher_search.addItem(new PublisherItem(rs.getInt("Publisher_ID"),rs.getString("Name")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }           
    } 
//---------------------------------------------------------------------------------------------------------------------------------------------     
//Update,Delete Book_count for categories, authors and publishers
    private void UpdateBookCountForCategories(int Category_ID) {
        try {
            pst = con.prepareStatement("Update Categories SET Book_count = Book_count + 1 Where Category_ID = " + Category_ID);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void UpdateBookCountForAuthors(int Author_ID) {
        try {
            pst = con.prepareStatement("Update Authors SET Book_count = Book_count + 1 Where Author_ID = " + Author_ID);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void UpdateBookCountForPublishers(int Publisher_ID) {
        try {
            pst = con.prepareStatement("Update Publishers SET Book_count = Book_count + 1 Where Publisher_ID = " + Publisher_ID);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  
    private void DeleteBookCountForCategories(int Category_ID, int count) {
        try {
            pst = con.prepareStatement("Update Categories SET Book_count = Book_count - " + count + " Where Category_ID = " + Category_ID);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void DeleteBookCountForAuthors(int Author_ID, int count) {
        try {
            pst = con.prepareStatement("Update Authors SET Book_count = Book_count - " + count + " Where Author_ID = " + Author_ID);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void DeleteBookCountForPublishers(int Publisher_ID, int count) {
        try {
            pst = con.prepareStatement("Update Publishers SET Book_count = Book_count - " + count + " Where Publisher_ID = " + Publisher_ID);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }     
//---------------------------------------------------------------------------------------------------------------------------------------------
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
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        admin_name = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        Logout_panel = new javax.swing.JPanel();
        logout_button = new javax.swing.JButton();
        manage_categories_button = new javax.swing.JButton();
        home_page_button = new javax.swing.JButton();
        manage_authors_button = new javax.swing.JButton();
        manage_publishers_button = new javax.swing.JButton();
        manage_issues_button = new javax.swing.JButton();
        manage_books_button = new javax.swing.JButton();
        manage_users_button = new javax.swing.JButton();
        edit_profile_button = new javax.swing.JButton();
        change_password_button = new javax.swing.JButton();
        Parent_panel = new javax.swing.JPanel();
        home_page_panel = new javax.swing.JPanel();
        JPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        UserNumber = new javax.swing.JLabel();
        JLabel = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        BookNumber = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        IssueNumber = new javax.swing.JLabel();
        j = new javax.swing.JLabel();
        Jlabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        recent_issue = new rojeru_san.complementos.RSTableMetro();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        most_issue_books_table = new rojeru_san.complementos.RSTableMetro();
        jLabel21 = new javax.swing.JLabel();
        category_panel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        category_id = new javax.swing.JComboBox<>();
        category_name = new javax.swing.JTextField();
        category_status = new javax.swing.JComboBox<>();
        category_book_count = new javax.swing.JTextField();
        category_addButton = new javax.swing.JButton();
        category_updateButton = new javax.swing.JButton();
        category_deleteButton = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        category_table = new rojeru_san.complementos.RSTableMetro();
        author_panel = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        author_id = new javax.swing.JComboBox();
        author_book_count = new javax.swing.JTextField();
        author_addButton = new javax.swing.JButton();
        author_updateButton = new javax.swing.JButton();
        author_deleteButton = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        author_name = new javax.swing.JTextField();
        author_city = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        author_table = new rojeru_san.complementos.RSTableMetro();
        publisher_panel = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        publisher_id = new javax.swing.JComboBox();
        publisher_book_count = new javax.swing.JTextField();
        publisher_addButton = new javax.swing.JButton();
        publisher_updateButton = new javax.swing.JButton();
        publisher_deleteButton = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        publisher_name = new javax.swing.JTextField();
        publisher_address = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        publisher_table = new rojeru_san.complementos.RSTableMetro();
        issue_panel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        issue_table = new rojeru_san.complementos.RSTableMetro();
        issue_sorted = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        issue_searchby_book = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        search_button = new javax.swing.JButton();
        issue_searchby_user = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        issue_searchby_issuedate = new com.toedter.calendar.JDateChooser();
        jLabel65 = new javax.swing.JLabel();
        issue_searchby_status = new javax.swing.JComboBox<>();
        book_panel = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        book_table = new rojeru_san.complementos.RSTableMetro();
        jPanel4 = new javax.swing.JPanel();
        book_id_search = new javax.swing.JComboBox();
        book_name = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        book_author = new javax.swing.JComboBox<>();
        book_publisher = new javax.swing.JComboBox<>();
        book_category = new javax.swing.JComboBox<>();
        book_quantity = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        book_addButton = new javax.swing.JButton();
        book_updateButton = new javax.swing.JButton();
        book_deleteButton = new javax.swing.JButton();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        book_id = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        book_category_search = new javax.swing.JComboBox();
        jLabel29 = new javax.swing.JLabel();
        book_author_search = new javax.swing.JComboBox();
        book_publisher_search = new javax.swing.JComboBox();
        jLabel30 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        user_panel = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        editprofile_panel = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        editprofile_saveButton = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        contact_number_label = new app.bolivia.swing.JCTextField();
        email_label = new app.bolivia.swing.JCTextField();
        jPanel15 = new javax.swing.JPanel();
        account_name_label = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        editprofile_resetButton = new javax.swing.JButton();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        changepassword_panel = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        txtpassword = new app.bolivia.swing.JCTextField();
        txtpassword1 = new app.bolivia.swing.JCTextField();
        txtpassword2 = new app.bolivia.swing.JCTextField();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        Submit_button = new javax.swing.JButton();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel2.setBackground(new java.awt.Color(102, 102, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_menu_48px_1.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, 3, 50));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Library Management System");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 260, 30));

        admin_name.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        admin_name.setForeground(new java.awt.Color(255, 255, 255));
        admin_name.setText("Welcome, Admin ");
        jPanel2.add(admin_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 10, 230, 30));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/male_user_50px.png"))); // NOI18N
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 0, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tw Cen MT", 1, 36)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("X");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 10, 30, 30));

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));
        jPanel3.setForeground(new java.awt.Color(51, 51, 51));

        jPanel5.setBackground(new java.awt.Color(51, 51, 51));

        jLabel7.setText("Feature");
        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 7, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Logout_panel.setBackground(new java.awt.Color(51, 51, 51));

        logout_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        logout_button.setText("Logout");
        logout_button.setBackground(new java.awt.Color(51, 51, 51));
        logout_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        logout_button.setForeground(new java.awt.Color(255, 255, 255));
        logout_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        logout_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logout_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Logout_panelLayout = new javax.swing.GroupLayout(Logout_panel);
        Logout_panel.setLayout(Logout_panelLayout);
        Logout_panelLayout.setHorizontalGroup(
            Logout_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logout_button, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
        );
        Logout_panelLayout.setVerticalGroup(
            Logout_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Logout_panelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logout_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        manage_categories_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Sell_26px.png"))); // NOI18N
        manage_categories_button.setText("Manage Categories");
        manage_categories_button.setBackground(new java.awt.Color(51, 51, 51));
        manage_categories_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        manage_categories_button.setForeground(new java.awt.Color(255, 255, 255));
        manage_categories_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        manage_categories_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_categories_buttonActionPerformed(evt);
            }
        });

        home_page_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/home_24px.png"))); // NOI18N
        home_page_button.setText("Home Page");
        home_page_button.setBackground(new java.awt.Color(255, 0, 51));
        home_page_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        home_page_button.setForeground(new java.awt.Color(255, 255, 255));
        home_page_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        home_page_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                home_page_buttonActionPerformed(evt);
            }
        });

        manage_authors_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        manage_authors_button.setText("Manage Authors");
        manage_authors_button.setBackground(new java.awt.Color(51, 51, 51));
        manage_authors_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        manage_authors_button.setForeground(new java.awt.Color(255, 255, 255));
        manage_authors_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        manage_authors_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_authors_buttonActionPerformed(evt);
            }
        });

        manage_publishers_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        manage_publishers_button.setText("Manage Publishers");
        manage_publishers_button.setBackground(new java.awt.Color(51, 51, 51));
        manage_publishers_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        manage_publishers_button.setForeground(new java.awt.Color(255, 255, 255));
        manage_publishers_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        manage_publishers_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_publishers_buttonActionPerformed(evt);
            }
        });

        manage_issues_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        manage_issues_button.setText("Manage Issues");
        manage_issues_button.setBackground(new java.awt.Color(51, 51, 51));
        manage_issues_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        manage_issues_button.setForeground(new java.awt.Color(255, 255, 255));
        manage_issues_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        manage_issues_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_issues_buttonActionPerformed(evt);
            }
        });

        manage_books_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        manage_books_button.setText("Manage Books");
        manage_books_button.setBackground(new java.awt.Color(51, 51, 51));
        manage_books_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        manage_books_button.setForeground(new java.awt.Color(255, 255, 255));
        manage_books_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        manage_books_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_books_buttonActionPerformed(evt);
            }
        });

        manage_users_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        manage_users_button.setText("Manage Users");
        manage_users_button.setBackground(new java.awt.Color(51, 51, 51));
        manage_users_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        manage_users_button.setForeground(new java.awt.Color(255, 255, 255));
        manage_users_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        manage_users_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_users_buttonActionPerformed(evt);
            }
        });

        edit_profile_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        edit_profile_button.setText("Edit Profile");
        edit_profile_button.setBackground(new java.awt.Color(51, 51, 51));
        edit_profile_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        edit_profile_button.setForeground(new java.awt.Color(255, 255, 255));
        edit_profile_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        edit_profile_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_profile_buttonActionPerformed(evt);
            }
        });

        change_password_button.setBackground(new java.awt.Color(51, 51, 51));
        change_password_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        change_password_button.setForeground(new java.awt.Color(255, 255, 255));
        change_password_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        change_password_button.setText("Change Password");
        change_password_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        change_password_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                change_password_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Logout_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(manage_categories_button, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(home_page_button, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(manage_authors_button, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(manage_publishers_button, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(manage_issues_button, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(manage_books_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(manage_users_button, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(edit_profile_button, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(change_password_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(home_page_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(manage_categories_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(manage_authors_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(manage_publishers_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(manage_issues_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(manage_books_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(manage_users_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(edit_profile_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(change_password_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Logout_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(116, 116, 116))
        );

        Parent_panel.setLayout(new java.awt.CardLayout());

        home_page_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        UserNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_People_50px.png"))); // NOI18N
        UserNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        UserNumber.setForeground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(UserNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(UserNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        JLabel.setText("No Of Books");
        JLabel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        JLabel.setForeground(new java.awt.Color(102, 102, 102));

        jPanel9.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        BookNumber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BookNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Book_Shelf_50px.png"))); // NOI18N
        BookNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        BookNumber.setForeground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BookNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(BookNumber)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        IssueNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Sell_50px.png"))); // NOI18N
        IssueNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        IssueNumber.setForeground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(IssueNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(IssueNumber)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        j.setText("Issue Books");
        j.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        j.setForeground(new java.awt.Color(102, 102, 102));

        Jlabel.setText("No Of Users");
        Jlabel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        Jlabel.setForeground(new java.awt.Color(102, 102, 102));

        recent_issue.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "UserName", "Book", "IssueDate", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        recent_issue.setColorFilasBackgound2(new java.awt.Color(204, 255, 153));
        recent_issue.setRowHeight(25);
        jScrollPane1.setViewportView(recent_issue);

        jLabel20.setText("Most-issued Books");
        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        most_issue_books_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "BookID", "BookName", "Category", "No of issues"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        most_issue_books_table.setColorFilasBackgound2(new java.awt.Color(204, 255, 153));
        most_issue_books_table.setRowHeight(25);
        jScrollPane2.setViewportView(most_issue_books_table);

        jLabel21.setText("Recent Issues");
        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        javax.swing.GroupLayout JPanelLayout = new javax.swing.GroupLayout(JPanel);
        JPanel.setLayout(JPanelLayout);
        JPanelLayout.setHorizontalGroup(
            JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(JLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Jlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(88, 88, 88)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(j, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(JPanelLayout.createSequentialGroup()
                .addGap(121, 121, 121)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addGap(0, 116, Short.MAX_VALUE))
        );
        JPanelLayout.setVerticalGroup(
            JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(j, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Jlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        home_page_panel.add(JPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 920, -1));

        Parent_panel.add(home_page_panel, "card2");

        category_panel.setBackground(new java.awt.Color(255, 255, 255));
        category_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(255, 51, 102));

        category_id.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                category_idItemStateChanged(evt);
            }
        });
        category_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                category_idActionPerformed(evt);
            }
        });
        category_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                category_idKeyPressed(evt);
            }
        });

        category_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Inactive" }));

        category_book_count.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        category_book_count.setEnabled(false);

        category_addButton.setText("ADD");
        category_addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                category_addButtonActionPerformed(evt);
            }
        });

        category_updateButton.setText("UPDATE");
        category_updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                category_updateButtonActionPerformed(evt);
            }
        });

        category_deleteButton.setText("DELETE");
        category_deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                category_deleteButtonActionPerformed(evt);
            }
        });

        jLabel15.setText("Category_ID");

        jLabel16.setText("Name");

        jLabel17.setText("Status");

        jLabel18.setText("Book_count");

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Info");
        jLabel19.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(category_addButton)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(category_updateButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(category_deleteButton)
                                .addGap(9, 9, 9))
                            .addComponent(category_name)
                            .addComponent(category_id, 0, 185, Short.MAX_VALUE)
                            .addComponent(category_status, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(category_book_count)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(category_id))
                .addGap(33, 33, 33)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(category_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(34, 34, 34)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(category_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(26, 26, 26)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(category_book_count, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(58, 58, 58)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(category_addButton)
                    .addComponent(category_updateButton)
                    .addComponent(category_deleteButton))
                .addContainerGap(267, Short.MAX_VALUE))
        );

        category_panel.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 340, 740));

        jLabel14.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Book_Shelf_50px.png"))); // NOI18N
        jLabel14.setText("Category");
        category_panel.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 60, 220, 50));

        category_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Category_ID", "Name", "Status", "Book_count"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        category_table.setColorFilasBackgound2(new java.awt.Color(153, 255, 153));
        category_table.setMultipleSeleccion(false);
        category_table.getTableHeader().setReorderingAllowed(false);
        category_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                category_tableMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(category_table);
        if (category_table.getColumnModel().getColumnCount() > 0) {
            category_table.getColumnModel().getColumn(0).setResizable(false);
            category_table.getColumnModel().getColumn(1).setResizable(false);
            category_table.getColumnModel().getColumn(2).setResizable(false);
            category_table.getColumnModel().getColumn(3).setResizable(false);
        }

        category_panel.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 270, 580, 470));

        Parent_panel.add(category_panel, "card3");

        author_panel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBackground(new java.awt.Color(255, 51, 102));

        author_id.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                author_idItemStateChanged(evt);
            }
        });
        author_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                author_idActionPerformed(evt);
            }
        });

        author_book_count.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        author_book_count.setEnabled(false);

        author_addButton.setText("ADD");
        author_addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                author_addButtonActionPerformed(evt);
            }
        });

        author_updateButton.setText("UPDATE");
        author_updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                author_updateButtonActionPerformed(evt);
            }
        });

        author_deleteButton.setText("DELETE");
        author_deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                author_deleteButtonActionPerformed(evt);
            }
        });

        jLabel22.setText("Author_ID");

        jLabel23.setText("Name");

        jLabel24.setText("City");

        jLabel25.setText("Book_count");

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Info");
        jLabel26.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(author_addButton)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(author_updateButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(author_deleteButton)
                                .addGap(9, 9, 9))
                            .addComponent(author_id, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(author_book_count)
                            .addComponent(author_name)
                            .addComponent(author_city, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(author_id))
                .addGap(33, 33, 33)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(author_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(author_city, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(author_book_count, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addGap(58, 58, 58)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(author_addButton)
                    .addComponent(author_updateButton)
                    .addComponent(author_deleteButton))
                .addContainerGap(270, Short.MAX_VALUE))
        );

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/member_50px.png"))); // NOI18N
        jLabel27.setText("Author");
        jLabel27.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N

        author_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Author_ID", "Name", "City", "Book_count"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        author_table.setColorFilasBackgound2(new java.awt.Color(204, 255, 102));
        author_table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        author_table.getTableHeader().setReorderingAllowed(false);
        author_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                author_tableMouseClicked(evt);
            }
        });
        jScrollPane9.setViewportView(author_table);
        if (author_table.getColumnModel().getColumnCount() > 0) {
            author_table.getColumnModel().getColumn(0).setResizable(false);
            author_table.getColumnModel().getColumn(1).setResizable(false);
            author_table.getColumnModel().getColumn(2).setResizable(false);
            author_table.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout author_panelLayout = new javax.swing.GroupLayout(author_panel);
        author_panel.setLayout(author_panelLayout);
        author_panelLayout.setHorizontalGroup(
            author_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, author_panelLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(author_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 576, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(author_panelLayout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        author_panelLayout.setVerticalGroup(
            author_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(author_panelLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Parent_panel.add(author_panel, "card4");

        publisher_panel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel12.setBackground(new java.awt.Color(255, 51, 102));

        publisher_id.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                publisher_idItemStateChanged(evt);
            }
        });
        publisher_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                publisher_idActionPerformed(evt);
            }
        });

        publisher_book_count.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        publisher_book_count.setEnabled(false);

        publisher_addButton.setText("ADD");
        publisher_addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                publisher_addButtonActionPerformed(evt);
            }
        });

        publisher_updateButton.setText("UPDATE");
        publisher_updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                publisher_updateButtonActionPerformed(evt);
            }
        });

        publisher_deleteButton.setText("DELETE");
        publisher_deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                publisher_deleteButtonActionPerformed(evt);
            }
        });

        jLabel32.setText("Publisher_ID");

        jLabel33.setText("Name");

        jLabel34.setText("Address");

        jLabel35.setText("Book_count");

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("Info");
        jLabel36.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(publisher_addButton)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(publisher_updateButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(publisher_deleteButton)
                                .addGap(9, 9, 9))
                            .addComponent(publisher_id, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(publisher_book_count)
                            .addComponent(publisher_name)
                            .addComponent(publisher_address, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(publisher_id))
                .addGap(33, 33, 33)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(publisher_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(publisher_address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(publisher_book_count, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addGap(58, 58, 58)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(publisher_addButton)
                    .addComponent(publisher_updateButton)
                    .addComponent(publisher_deleteButton))
                .addContainerGap(270, Short.MAX_VALUE))
        );

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_People_50px.png"))); // NOI18N
        jLabel28.setText("Publisher");
        jLabel28.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N

        publisher_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Publisher_ID", "Name", "Address", "Book_count"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        publisher_table.setColorFilasBackgound2(new java.awt.Color(153, 255, 153));
        publisher_table.getTableHeader().setReorderingAllowed(false);
        publisher_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                publisher_tableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(publisher_table);
        if (publisher_table.getColumnModel().getColumnCount() > 0) {
            publisher_table.getColumnModel().getColumn(0).setResizable(false);
            publisher_table.getColumnModel().getColumn(1).setResizable(false);
            publisher_table.getColumnModel().getColumn(2).setResizable(false);
            publisher_table.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout publisher_panelLayout = new javax.swing.GroupLayout(publisher_panel);
        publisher_panel.setLayout(publisher_panelLayout);
        publisher_panelLayout.setHorizontalGroup(
            publisher_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(publisher_panelLayout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(publisher_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(publisher_panelLayout.createSequentialGroup()
                        .addGap(152, 152, 152)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)))
        );
        publisher_panelLayout.setVerticalGroup(
            publisher_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(publisher_panelLayout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Parent_panel.add(publisher_panel, "card5");

        issue_panel.setBackground(new java.awt.Color(255, 102, 102));

        issue_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Issue_ID", "Book", "User", "IssueDate", "Scheduled", "ActualDue", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        issue_table.setColorFilasBackgound2(new java.awt.Color(153, 255, 153));
        issue_table.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(issue_table);
        if (issue_table.getColumnModel().getColumnCount() > 0) {
            issue_table.getColumnModel().getColumn(0).setResizable(false);
            issue_table.getColumnModel().getColumn(0).setPreferredWidth(40);
            issue_table.getColumnModel().getColumn(1).setResizable(false);
            issue_table.getColumnModel().getColumn(1).setPreferredWidth(140);
            issue_table.getColumnModel().getColumn(2).setResizable(false);
            issue_table.getColumnModel().getColumn(3).setResizable(false);
            issue_table.getColumnModel().getColumn(4).setResizable(false);
            issue_table.getColumnModel().getColumn(5).setResizable(false);
            issue_table.getColumnModel().getColumn(6).setResizable(false);
        }

        issue_sorted.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Newest to Oldest", "Oldest to Newest" }));
        issue_sorted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                issue_sortedActionPerformed(evt);
            }
        });

        jLabel3.setText("SORT BY");

        issue_searchby_book.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                issue_searchby_bookActionPerformed(evt);
            }
        });

        jLabel4.setText("Search By");

        search_button.setText("Search");
        search_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_buttonActionPerformed(evt);
            }
        });

        issue_searchby_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                issue_searchby_userActionPerformed(evt);
            }
        });

        jLabel9.setText("User");

        jLabel37.setText("Book");

        jLabel8.setText("IssueDate");

        jButton1.setText("Reset");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel65.setText("Status");

        javax.swing.GroupLayout issue_panelLayout = new javax.swing.GroupLayout(issue_panel);
        issue_panel.setLayout(issue_panelLayout);
        issue_panelLayout.setHorizontalGroup(
            issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 915, Short.MAX_VALUE)
            .addGroup(issue_panelLayout.createSequentialGroup()
                .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(issue_panelLayout.createSequentialGroup()
                        .addGap(106, 106, 106)
                        .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(issue_panelLayout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(149, 149, 149))
                            .addGroup(issue_panelLayout.createSequentialGroup()
                                .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(issue_panelLayout.createSequentialGroup()
                                        .addComponent(issue_sorted, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, issue_panelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(issue_searchby_book, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(issue_panelLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(search_button))
                    .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(issue_searchby_status, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(issue_searchby_issuedate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(issue_searchby_user, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        issue_panelLayout.setVerticalGroup(
            issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, issue_panelLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(issue_sorted, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(issue_searchby_book, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(issue_searchby_user, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(issue_searchby_issuedate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel65)
                    .addComponent(issue_searchby_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(search_button))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 131, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Parent_panel.add(issue_panel, "card6");

        book_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Book_ID", "Name", "Category", "Author", "Publisher", "Quantity"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        book_table.setColorFilasBackgound2(new java.awt.Color(204, 255, 51));
        book_table.setMultipleSeleccion(false);
        book_table.getTableHeader().setReorderingAllowed(false);
        book_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                book_tableMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(book_table);
        if (book_table.getColumnModel().getColumnCount() > 0) {
            book_table.getColumnModel().getColumn(0).setResizable(false);
            book_table.getColumnModel().getColumn(1).setResizable(false);
            book_table.getColumnModel().getColumn(2).setResizable(false);
            book_table.getColumnModel().getColumn(3).setResizable(false);
            book_table.getColumnModel().getColumn(4).setResizable(false);
            book_table.getColumnModel().getColumn(5).setResizable(false);
        }

        jPanel4.setBackground(new java.awt.Color(255, 51, 102));

        book_id_search.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                book_id_searchItemStateChanged(evt);
            }
        });

        jLabel11.setText("Name");

        book_author.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                book_authorItemStateChanged(evt);
            }
        });

        book_publisher.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                book_publisherItemStateChanged(evt);
            }
        });

        book_category.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                book_categoryItemStateChanged(evt);
            }
        });

        jLabel12.setText("Quantity");

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("Info");
        jLabel31.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N

        book_addButton.setText("ADD");
        book_addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                book_addButtonActionPerformed(evt);
            }
        });

        book_updateButton.setText("UPDATE");
        book_updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                book_updateButtonActionPerformed(evt);
            }
        });

        book_deleteButton.setText("DELETE");
        book_deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                book_deleteButtonActionPerformed(evt);
            }
        });

        jLabel66.setText("ID");

        jLabel67.setText("Category");

        jLabel68.setText("Author");

        jLabel69.setText("Publisher");

        jButton2.setText("SEARCH");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        book_id.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        book_id.setEnabled(false);

        jLabel10.setText("ID");

        jLabel13.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Search");

        jLabel29.setText("Category");

        jLabel30.setText("Author");

        jLabel70.setText("Publisher");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                                    .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel67, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel68, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel69, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(book_id, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(book_category, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(book_author, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(book_publisher, javax.swing.GroupLayout.Alignment.LEADING, 0, 147, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                        .addComponent(book_quantity, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel70, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(book_category_search, 0, 147, Short.MAX_VALUE)
                                            .addComponent(book_author_search, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(book_publisher_search, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                        .addComponent(book_name, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 243, Short.MAX_VALUE)
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(book_id_search, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(66, 66, 66)
                                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(290, 290, 290)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(210, 210, 210))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(book_addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(book_updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(book_deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel67)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(book_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11)
                                    .addComponent(book_id_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(book_quantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12)
                                    .addComponent(book_category_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel29))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel66)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(book_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(book_author_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel30)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(book_category, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(book_publisher_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel70)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel68)
                            .addComponent(book_author, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel69)
                            .addComponent(book_publisher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(44, 44, 44))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton2)
                        .addGap(52, 52, 52)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(book_deleteButton)
                    .addComponent(book_updateButton)
                    .addComponent(book_addButton))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout book_panelLayout = new javax.swing.GroupLayout(book_panel);
        book_panel.setLayout(book_panelLayout);
        book_panelLayout.setHorizontalGroup(
            book_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        book_panelLayout.setVerticalGroup(
            book_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, book_panelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        Parent_panel.add(book_panel, "card7");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 903, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 731, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout user_panelLayout = new javax.swing.GroupLayout(user_panel);
        user_panel.setLayout(user_panelLayout);
        user_panelLayout.setHorizontalGroup(
            user_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(user_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        user_panelLayout.setVerticalGroup(
            user_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, user_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        Parent_panel.add(user_panel, "card8");

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        jLabel38.setText("AccountName ( Can't change ) : ");
        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel39.setText("ContactNumber : ");
        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel40.setText("Email : ");
        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

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

        jLabel41.setText("Edit profile");
        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N

        jLabel42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/email_50px.png"))); // NOI18N

        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/member_off_off_50px.png"))); // NOI18N

        jLabel44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/call_50px.png"))); // NOI18N

        jLabel45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/edit_pro5_64px.png"))); // NOI18N

        jLabel46.setText("Update contactnumber and email for better security");
        jLabel46.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(121, 121, 121));

        contact_number_label.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        contact_number_label.setPlaceholder("Contact Number . . .");

        email_label.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        email_label.setPlaceholder("Email . . . ");

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));
        jPanel15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        account_name_label.setText("_AccountName_");
        account_name_label.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(account_name_label)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(account_name_label, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/boyreadingbook_490.png"))); // NOI18N

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

        jLabel48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/girl_120.png"))); // NOI18N

        jLabel49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/idea_girl_50px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel43)
                    .addComponent(jLabel44)
                    .addComponent(jLabel42))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel45)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41)
                            .addComponent(jLabel46)))
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel48)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel49)
                .addGap(155, 155, 155))
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel14Layout.createSequentialGroup()
                            .addGap(19, 19, 19)
                            .addComponent(editprofile_saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(48, 48, 48)
                            .addComponent(editprofile_resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(email_label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel39, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contact_number_label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE))))
                .addGap(16, 16, 16)
                .addComponent(jLabel47)
                .addGap(12, 12, 12))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel46))
                            .addComponent(jLabel45))
                        .addGap(46, 46, 46)
                        .addComponent(jLabel38))
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel49)
                        .addComponent(jLabel48)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contact_number_label, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel44))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel40)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel42)
                                    .addComponent(email_label, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGap(97, 97, 97)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(editprofile_resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(editprofile_saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jLabel47))
                .addContainerGap(81, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout editprofile_panelLayout = new javax.swing.GroupLayout(editprofile_panel);
        editprofile_panel.setLayout(editprofile_panelLayout);
        editprofile_panelLayout.setHorizontalGroup(
            editprofile_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        editprofile_panelLayout.setVerticalGroup(
            editprofile_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Parent_panel.add(editprofile_panel, "card9");

        changepassword_panel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/quenmatkhau.png"))); // NOI18N

        jLabel51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/change.png"))); // NOI18N

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));

        txtpassword.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtpassword.setPlaceholder("Enter Password .....");

        txtpassword1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtpassword1.setPlaceholder("Enter New Password");

        txtpassword2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtpassword2.setPlaceholder("Confirm Password");

        jLabel52.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel52.setText("Current password");

        jLabel53.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(223, 25, 25));
        jLabel53.setText("* ");

        jLabel54.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel54.setText("Confirm password");

        jLabel55.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(223, 25, 25));
        jLabel55.setText("* ");

        jLabel56.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel56.setText("New password");

        jLabel57.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(223, 25, 25));
        jLabel57.setText("* ");

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

        jLabel58.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel58.setText("Change password");

        jLabel59.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(121, 121, 121));
        jLabel59.setText("Update password for enhanced account security");

        jLabel60.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/reset_pass_50px.png"))); // NOI18N

        jLabel61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/password_icon_50px.png"))); // NOI18N

        jLabel62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/key_50px.png"))); // NOI18N

        jLabel63.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/idea_50px.png"))); // NOI18N

        jLabel64.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/confirm_50px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel62, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel63, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(jLabel64, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel52)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel16Layout.createSequentialGroup()
                                        .addComponent(jLabel56)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel16Layout.createSequentialGroup()
                                        .addComponent(jLabel54)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtpassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtpassword1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtpassword2, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addComponent(jLabel60)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel59)
                                    .addGroup(jPanel16Layout.createSequentialGroup()
                                        .addComponent(jLabel58)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel61))))))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(Submit_button, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel60)
                        .addGap(48, 48, 48))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jLabel58))
                            .addComponent(jLabel61))
                        .addGap(8, 8, 8)
                        .addComponent(jLabel59)
                        .addGap(43, 43, 43)))
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(jLabel53))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtpassword, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel62))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(jLabel57))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtpassword1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel63))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel55)
                    .addComponent(jLabel54, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(changepassword_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51))
                .addContainerGap(115, Short.MAX_VALUE))
        );
        changepassword_panelLayout.setVerticalGroup(
            changepassword_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changepassword_panelLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel50)
                .addGap(66, 66, 66))
            .addGroup(changepassword_panelLayout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(162, Short.MAX_VALUE))
        );

        Parent_panel.add(changepassword_panel, "card3");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Parent_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 915, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Parent_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jLabel6MouseClicked

    private void category_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_category_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_category_idActionPerformed

    private void category_addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_category_addButtonActionPerformed
        // TODO add your handling code here:
        String name = category_name.getText();
        if("".equals(name) || category_status.getSelectedItem() == null) {
            return;
        }
        String status = category_status.getSelectedItem().toString();
        String book_count = "0";
        try {
            pst = con.prepareStatement("Insert into Categories(Name,Status,Book_count)values(?,?,?)");
            pst.setString(1, name);
            pst.setString(2, status);
            pst.setString(3, book_count);
            int k = pst.executeUpdate();            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Category created");
                category_name.setText("");
                category_status.setSelectedIndex(-1);
                category_book_count.setText("");
                Category_TableLoad();
                Category_IDLoad();
//                category_id.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_category_addButtonActionPerformed

    private void category_updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_category_updateButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)category_table.getModel();
        int selectIndex = category_table.getSelectedRow();
        String id = model.getValueAt(selectIndex, 0).toString();        
        String name = category_name.getText();
        if("".equals(name)) {
            JOptionPane.showMessageDialog(this, "Missing information");
            return;
        }    
        String status = category_status.getSelectedItem().toString();
        try {
            pst = con.prepareStatement("Update Categories set Name = ?, Status = ? where Category_ID = ?");
            pst.setString(1, name);
            pst.setString(2, status);
            pst.setString(3, id);
            int k = pst.executeUpdate();            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Category updated");
                category_name.setText("");
                category_book_count.setText("");
                Category_TableLoad();
//                category_id.setSelectedIndex(0);
                category_status.setSelectedIndex(-1);                
                category_addButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_category_updateButtonActionPerformed

    private void category_deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_category_deleteButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)category_table.getModel();
        int selectIndex = category_table.getSelectedRow();
        String id = model.getValueAt(selectIndex, 0).toString();
        try {
            PreparedStatement new_pst = con.prepareStatement("SELECT b.Author_ID, b.Publisher_ID, COUNT(*) as \"Count\" "
                    + "FROM books b JOIN authors a ON b.Author_ID = a.Author_ID "
                    + "JOIN categories c ON b.Category_ID = c.Category_ID JOIN publishers p ON b.Publisher_ID = p.Publisher_ID "
                    + "GROUP BY b.Category_ID HAVING b.Category_ID = ?");
            new_pst.setString(1, id);
            ResultSet new_rs = new_pst.executeQuery();
            while(new_rs.next()){
                int author = new_rs.getInt("Author_ID");
                int publisher = new_rs.getInt("Publisher_ID");
                int count = new_rs.getInt("Count");
                DeleteBookCountForAuthors(author, count);
                DeleteBookCountForPublishers(publisher, count);            
            }
            
                    
            new_pst = con.prepareStatement("Delete from Categories Where Category_ID = ?");
            new_pst.setString(1, id);
            int k = new_pst.executeUpdate();
            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Data deleted");
                category_name.setText("");
                category_book_count.setText("");
                category_status.setSelectedIndex(-1);
                Category_TableLoad();
                Category_IDLoad();
//                category_id.setSelectedIndex(0);
                category_addButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
//Delete book_count of book in all field

        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }//GEN-LAST:event_category_deleteButtonActionPerformed

    private void manage_categories_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manage_categories_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(category_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Category_panelLoad();
        TurnOffButtons();
        manage_categories_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_manage_categories_buttonActionPerformed

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

    private void manage_authors_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manage_authors_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(author_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Author_panelLoad();
        TurnOffButtons();
        manage_authors_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_manage_authors_buttonActionPerformed

    private void author_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_author_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_author_idActionPerformed

    private void author_addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_author_addButtonActionPerformed
        // TODO add your handling code here:
        String name = author_name.getText();
        String city = author_city.getText();
        String book_count = "0";
        if("".equals(name) || "".equals(city)) return;
        try {
            pst = con.prepareStatement("Insert into Authors(Name,City,Book_count)values(?,?,?)");
            pst.setString(1, name);
            pst.setString(2, city);
            pst.setString(3, book_count);
            int k = pst.executeUpdate();            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Author created");
                author_name.setText("");
                author_city.setText("");
                author_book_count.setText("");
                Author_TableLoad();
                Author_IDLoad();
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_author_addButtonActionPerformed

    private void author_updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_author_updateButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)author_table.getModel();
        int selectIndex = author_table.getSelectedRow();
        String id = model.getValueAt(selectIndex, 0).toString();        
        String name = author_name.getText();
        if("".equals(name)) {
            JOptionPane.showMessageDialog(this, "Missing information");
            return;
        }    
        String city = author_city.getText();
        try {
            pst = con.prepareStatement("Update Authors set Name = ?, City = ? where Author_ID = ?");
            pst.setString(1, name);
            pst.setString(2, city);
            pst.setString(3, id);
            int k = pst.executeUpdate();            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Data updated");
                author_name.setText("");
                author_city.setText("");
                author_book_count.setText("");
                Author_TableLoad();
                Author_IDLoad();
                author_addButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_author_updateButtonActionPerformed

    private void author_deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_author_deleteButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)author_table.getModel();
        int selectIndex = author_table.getSelectedRow();
        String id = model.getValueAt(selectIndex, 0).toString();
        try {
            PreparedStatement new_pst = con.prepareStatement("SELECT b.Category_ID, b.Publisher_ID, COUNT(*) as \"Count\" "
                    + "FROM books b JOIN authors a ON b.Author_ID = a.Author_ID "
                    + "JOIN categories c ON b.Category_ID = c.Category_ID JOIN publishers p ON b.Publisher_ID = p.Publisher_ID "
                    + "GROUP BY b.Author_ID HAVING b.Author_ID = ?");
            new_pst.setString(1, id);
            ResultSet new_rs = new_pst.executeQuery();
            while(new_rs.next()){
                int category = new_rs.getInt("Category_ID");
                int publisher = new_rs.getInt("Publisher_ID");
                int count = new_rs.getInt("Count");
                DeleteBookCountForCategories(category, count);
                DeleteBookCountForPublishers(publisher, count);            
            }
            
            pst = con.prepareStatement("Delete from Authors Where Author_ID = ?");
            pst.setString(1, id);
            int k = pst.executeUpdate();
            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Data deleted");
                author_name.setText("");
                author_id.setSelectedIndex(-1);
                author_book_count.setText("");
                author_city.setText("");
                Author_TableLoad();
                Author_IDLoad();
                author_addButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }//GEN-LAST:event_author_deleteButtonActionPerformed

    private void manage_publishers_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manage_publishers_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(publisher_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Publisher_panelLoad();
        TurnOffButtons();
        manage_publishers_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_manage_publishers_buttonActionPerformed

    private void publisher_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publisher_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_publisher_idActionPerformed

    private void publisher_addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publisher_addButtonActionPerformed
        // TODO add your handling code here:
        String name = publisher_name.getText();
        String address = publisher_address.getText();
        String book_count = "0";
        if("".equals(name) || "".equals(address)) return;
        try {
            pst = con.prepareStatement("Insert into Publishers(Name,Address,Book_count)values(?,?,?)");
            pst.setString(1, name);
            pst.setString(2, address);
            pst.setString(3, book_count);
            int k = pst.executeUpdate();            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Data created");
//                publisher_id.setSelectedIndex(-1);
                publisher_name.setText("");
                publisher_address.setText("");
                publisher_book_count.setText("");
                Publisher_TableLoad();
//                Publisher_IDLoad();
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_publisher_addButtonActionPerformed

    private void publisher_updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publisher_updateButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)publisher_table.getModel();
        int selectIndex = publisher_table.getSelectedRow();
        String id = model.getValueAt(selectIndex, 0).toString();        
        String name = publisher_name.getText();
        if("".equals(name)) {
            JOptionPane.showMessageDialog(this, "Missing information");
            return;
        }    
        String address = publisher_address.getText();
        try {
            pst = con.prepareStatement("Update Publishers set Name = ?, Address = ? where Publisher_ID = ?");
            pst.setString(1, name);
            pst.setString(2, address);
            pst.setString(3, id);
            int k = pst.executeUpdate();            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Data updated");
                publisher_name.setText("");
                publisher_address.setText("");
                publisher_id.setSelectedIndex(-1);
                publisher_book_count.setText("");
                Publisher_TableLoad();
                publisher_addButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_publisher_updateButtonActionPerformed

    private void publisher_deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publisher_deleteButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)publisher_table.getModel();
        int selectIndex = publisher_table.getSelectedRow();
        String id = model.getValueAt(selectIndex, 0).toString();
        try {
            PreparedStatement new_pst = con.prepareStatement("SELECT b.Author_ID, b.Category_ID, COUNT(*) as \"Count\" "
                    + "FROM books b JOIN authors a ON b.Author_ID = a.Author_ID "
                    + "JOIN categories c ON b.Category_ID = c.Category_ID JOIN publishers p ON b.Publisher_ID = p.Publisher_ID "
                    + "GROUP BY b.Publisher_ID HAVING b.Publisher_ID = ?");
            new_pst.setString(1, id);
            ResultSet new_rs = new_pst.executeQuery();
            while(new_rs.next()){
                int author = new_rs.getInt("Author_ID");
                int category = new_rs.getInt("Category_ID");
                int count = new_rs.getInt("Count");
                DeleteBookCountForAuthors(author, count);
                DeleteBookCountForCategories(category, count);
            }
            
            pst = con.prepareStatement("Delete from Publishers Where Publisher_ID = ?");
            pst.setString(1, id);
            int k = pst.executeUpdate();
            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Data deleted");
                publisher_name.setText("");
//                publisher_id.setSelectedIndex(-1);
                publisher_book_count.setText("");
                publisher_address.setText("");
                Publisher_TableLoad();
                Publisher_IDLoad();
                publisher_addButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }//GEN-LAST:event_publisher_deleteButtonActionPerformed

    private void manage_issues_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manage_issues_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(issue_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Issue_PanelLoad();
        TurnOffButtons();
        manage_issues_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_manage_issues_buttonActionPerformed

    private void issue_sortedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_issue_sortedActionPerformed
        // TODO add your handling code here:
        Issue_SQLQuery();
    }//GEN-LAST:event_issue_sortedActionPerformed

    private void issue_searchby_bookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_issue_searchby_bookActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_issue_searchby_bookActionPerformed

    private void search_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_buttonActionPerformed
        // TODO add your handling code here:
        Issue_SQLQuery();
    }//GEN-LAST:event_search_buttonActionPerformed

    private void issue_searchby_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_issue_searchby_userActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_issue_searchby_userActionPerformed

    private void manage_books_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manage_books_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(book_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Book_panelLoad();
        TurnOffButtons();
        manage_books_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_manage_books_buttonActionPerformed

    private void book_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_book_tableMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)book_table.getModel();
        int clickedRow = book_table.rowAtPoint(evt.getPoint());
        if(clickedRow == NO_SELECTION) return;
        if(clickedRow == table_status.Previous_book_selected) {
            table_status.Previous_book_selected = NO_SELECTION;
            book_table.clearSelection();
            book_addButton.setEnabled(true);
            book_updateButton.setEnabled(false);
            book_deleteButton.setEnabled(false);
            return;
        }
        table_status.Previous_book_selected = clickedRow;
        int selectIndex = book_table.getSelectedRow();
//        book_id.setSelectedItem(model.getValueAt(selectIndex,0).toString());
        book_id.setText(model.getValueAt(selectIndex, 0).toString());
        book_name.setText(model.getValueAt(selectIndex,1).toString());
        book_category.setSelectedItem((CategoryItem)model.getValueAt(selectIndex,2));   
        book_author.setSelectedItem((AuthorItem)model.getValueAt(selectIndex,3)); 
        book_publisher.setSelectedItem((PublisherItem)model.getValueAt(selectIndex,4));
        book_quantity.setText(model.getValueAt(selectIndex,5).toString());
        book_addButton.setEnabled(false);
        book_updateButton.setEnabled(true);
        book_deleteButton.setEnabled(true);
    }//GEN-LAST:event_book_tableMouseClicked

    private void book_addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_book_addButtonActionPerformed
        // TODO add your handling code here:
        String name = book_name.getText();
        String quantity = book_quantity.getText();
        if ("".equals(quantity) || "".equals(name) || book_category.getSelectedIndex() == -1 || book_author.getSelectedIndex() == -1 || book_publisher.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Missing information");
            return;
        }  
        try {
            if(Integer.parseInt(quantity) < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity is invalid");
            return;
        }
        int category = ((CategoryItem)book_category.getSelectedItem()).getId();
        int author = ((AuthorItem)book_author.getSelectedItem()).getId();
        int publisher = ((PublisherItem)book_publisher.getSelectedItem()).getId();       
        try {
            UpdateBookCountForCategories(category);
            UpdateBookCountForAuthors(author);
            UpdateBookCountForPublishers(publisher);

            pst = con.prepareStatement("INSERT INTO Books (Name,Category_ID,Author_ID,Publisher_ID,Quantity) VALUES (?,?,?,?,?)");
            pst.setString(1, name);
            pst.setInt(2, category);
            pst.setInt(3, author);
            pst.setInt(4, publisher);
            pst.setString(5, quantity);
            int k = pst.executeUpdate();            
            if(k == 1){
                Book_panelLoad();
                book_id.setText("");
                JOptionPane.showMessageDialog(this, "Data created");
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
    }//GEN-LAST:event_book_addButtonActionPerformed

    private void book_updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_book_updateButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)book_table.getModel();
        String quantity = book_quantity.getText();
            try {
            if(Integer.parseInt(quantity) < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity is invalid");
            return;
        }
        int selectIndex = book_table.getSelectedRow();
        String id = model.getValueAt(selectIndex, 0).toString();        
        String name = book_name.getText();
        if("".equals(name)) {
            JOptionPane.showMessageDialog(this, "Missing information");
            return;
        }    
        int category = ((CategoryItem)book_category.getSelectedItem()).getId();
        int author = ((AuthorItem)book_author.getSelectedItem()).getId();
        int publisher = ((PublisherItem)book_publisher.getSelectedItem()).getId();
        CategoryItem ci = (CategoryItem)model.getValueAt(selectIndex, 2);
        int old_category_id = ci.getId();
        AuthorItem ai = (AuthorItem)model.getValueAt(selectIndex, 3);
        int old_author_id = ai.getId();
        PublisherItem pi = (PublisherItem)model.getValueAt(selectIndex, 4);
        int old_publisher_id = pi.getId();
        try {
            
            pst = con.prepareStatement("Update Books set Name = ?, Category_ID = ?, Author_ID = ?, Publisher_ID = ?, Quantity = ? where Book_ID = ?");
            pst.setString(1, name);
            pst.setInt(2, category);
            pst.setInt(3, author);
            pst.setInt(4, publisher);
            pst.setString(5, quantity);
            pst.setString(6, id);
            int k = pst.executeUpdate();            
            if(k == 1){
                UpdateBookCountForCategories(category);
                UpdateBookCountForAuthors(author);
                UpdateBookCountForPublishers(publisher);
                DeleteBookCountForCategories(old_category_id, 1);
                DeleteBookCountForAuthors(old_author_id, 1);
                DeleteBookCountForPublishers(old_publisher_id, 1);
                JOptionPane.showMessageDialog(this, "Data updated");
                Book_panelLoad();
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_book_updateButtonActionPerformed

    private void book_deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_book_deleteButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)book_table.getModel();
        int selectIndex = book_table.getSelectedRow();
        String id = model.getValueAt(selectIndex, 0).toString();
        int category = ((CategoryItem)book_category.getSelectedItem()).getId();
        int author = ((AuthorItem)book_author.getSelectedItem()).getId();
        int publisher = ((PublisherItem)book_publisher.getSelectedItem()).getId();
        try {
            pst = con.prepareStatement("Delete from Books Where Book_ID = ?");
            pst.setString(1, id);
            int k = pst.executeUpdate();
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Data deleted");
                book_id.setText("");
                DeleteBookCountForCategories(category,1);
                DeleteBookCountForAuthors(author,1);
                DeleteBookCountForPublishers(publisher,1);
                Book_panelLoad();
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }//GEN-LAST:event_book_deleteButtonActionPerformed

    private void category_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_category_tableMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)category_table.getModel();
        int clickedRow = category_table.rowAtPoint(evt.getPoint());
        if(clickedRow == NO_SELECTION) return;
        if(clickedRow == table_status.Previous_category_selected) {
            table_status.Previous_category_selected = NO_SELECTION;
            category_table.clearSelection();
            category_addButton.setEnabled(true);
            category_updateButton.setEnabled(false);
            category_deleteButton.setEnabled(false);
            return;
        }
        table_status.Previous_category_selected = clickedRow;
        int selectIndex = category_table.getSelectedRow();
//        category_id.setSelectedItem(model.getValueAt(selectIndex,0).toString());
        category_name.setText(model.getValueAt(selectIndex,1).toString());
        category_status.setSelectedItem(model.getValueAt(selectIndex,2).toString());   
        category_book_count.setText(model.getValueAt(selectIndex,3).toString());
        category_addButton.setEnabled(false);
        category_updateButton.setEnabled(true);
        category_deleteButton.setEnabled(true);
    }//GEN-LAST:event_category_tableMouseClicked

    private void author_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_author_tableMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)author_table.getModel();
        int clickedRow = author_table.rowAtPoint(evt.getPoint());
        if(clickedRow == NO_SELECTION) return;
        if(clickedRow == table_status.Previous_author_selected) {
            table_status.Previous_author_selected = NO_SELECTION;
            author_table.clearSelection();
            author_addButton.setEnabled(true);
            author_updateButton.setEnabled(false);
            author_deleteButton.setEnabled(false);
            return;
        }
        table_status.Previous_author_selected = clickedRow;
        int selectIndex = author_table.getSelectedRow();
//        author_id.setSelectedItem(model.getValueAt(selectIndex,0).toString());
        author_name.setText(model.getValueAt(selectIndex,1).toString());
        author_city.setText(model.getValueAt(selectIndex,2).toString());   
        author_book_count.setText(model.getValueAt(selectIndex,3).toString());
        author_addButton.setEnabled(false);
        author_updateButton.setEnabled(true);
        author_deleteButton.setEnabled(true);
    }//GEN-LAST:event_author_tableMouseClicked

    private void publisher_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_publisher_tableMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)publisher_table.getModel();
        int clickedRow = publisher_table.rowAtPoint(evt.getPoint());
        if(clickedRow == NO_SELECTION) return;
        if(clickedRow == table_status.Previous_publisher_selected) {
            table_status.Previous_publisher_selected = NO_SELECTION;
            publisher_table.clearSelection();
            publisher_addButton.setEnabled(true);
            publisher_updateButton.setEnabled(false);
            publisher_deleteButton.setEnabled(false);
            return;
        }
        table_status.Previous_publisher_selected = clickedRow;
        int selectIndex = publisher_table.getSelectedRow();
//        publisher_id.setSelectedItem(model.getValueAt(selectIndex,0).toString());
        publisher_name.setText(model.getValueAt(selectIndex,1).toString());
        publisher_address.setText(model.getValueAt(selectIndex,2).toString());   
        publisher_book_count.setText(model.getValueAt(selectIndex,3).toString());
        publisher_addButton.setEnabled(false);
        publisher_updateButton.setEnabled(true);
        publisher_deleteButton.setEnabled(true);
    }//GEN-LAST:event_publisher_tableMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
//        issue_searchby_book.setText("");
//        issue_searchby_user.setText("");
//        issue_searchby_issuedate.setDate(null);
//        issue_sorted.setSelectedIndex(0);
        Issue_TextandComboboxLoad();
        Issue_SQLQuery();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void category_idItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_category_idItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED){
                String baseQuery = "Select * from Categories Where 1=1";
                    if(category_id.getSelectedIndex() != 0) {
                        baseQuery += " AND Category_ID = ? ";
                    }
            try {
                    PreparedStatement ps = con.prepareStatement(baseQuery);
                    if(category_id.getSelectedIndex() != 0) ps.setString(1, (String)category_id.getSelectedItem());
                    ResultSet r = ps.executeQuery();

                    ResultSetMetaData rsd = r.getMetaData();
                    int columns = rsd.getColumnCount();

                    DefaultTableModel model = (DefaultTableModel)category_table.getModel();
                    model.setRowCount(0);

                    while(r.next()){
                        Object[] obj = new Object[columns];
                        for(int i = 1 ; i <= columns ; i++){
                            obj[i-1] = r.getString(i);
                        }
                        model.addRow(obj);
                    }

                } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_category_idItemStateChanged

    private void category_idKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_category_idKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_category_idKeyPressed

    private void author_idItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_author_idItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED){
                String baseQuery = "Select * from Authors Where 1=1";
                    if(author_id.getSelectedIndex() != 0) {
                        baseQuery += " AND Author_ID = ? ";
                    }
            try {
                    PreparedStatement ps = con.prepareStatement(baseQuery);
                    if(author_id.getSelectedIndex() != 0) ps.setString(1, (String)author_id.getSelectedItem());
                    ResultSet r = ps.executeQuery();

                    ResultSetMetaData rsd = r.getMetaData();
                    int columns = rsd.getColumnCount();

                    DefaultTableModel model = (DefaultTableModel)author_table.getModel();
                    model.setRowCount(0);

                    while(r.next()){
                        Object[] obj = new Object[columns];
                        for(int i = 1 ; i <= columns ; i++){
                            obj[i-1] = r.getString(i);
                        }
                        model.addRow(obj);
                    }

                } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_author_idItemStateChanged

    private void publisher_idItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_publisher_idItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED){
                String baseQuery = "Select * from Publishers Where 1=1";
                    if(publisher_id.getSelectedIndex() != 0) {
                        baseQuery += " AND Publisher_ID = ? ";
                    }
            try {
                    PreparedStatement ps = con.prepareStatement(baseQuery);
                    if(publisher_id.getSelectedIndex() != 0) ps.setString(1, (String)publisher_id.getSelectedItem());
                    ResultSet r = ps.executeQuery();

                    ResultSetMetaData rsd = r.getMetaData();
                    int columns = rsd.getColumnCount();

                    DefaultTableModel model = (DefaultTableModel)publisher_table.getModel();
                    model.setRowCount(0);

                    while(r.next()){
                        Object[] obj = new Object[columns];
                        for(int i = 1 ; i <= columns ; i++){
                            obj[i-1] = r.getString(i);
                        }
                        model.addRow(obj);
                    }

                } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }//GEN-LAST:event_publisher_idItemStateChanged

    private void book_id_searchItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_book_id_searchItemStateChanged
//         TODO add your handling code here:
//        if(evt.getStateChange() == ItemEvent.SELECTED){
//            if(!book_combobox_check.book_id_check || !book_combobox_check.book_category_check 
//                || !book_combobox_check.book_author_check || !book_combobox_check.book_publisher_check){
//                return;
//            }
//                String baseQuery = "SELECT b.Book_ID, b.Name,c.Category_ID ,c.Name as Category,a.Author_ID,a.Name as Author,p.Publisher_ID,p.Name as Publisher, b.Quantity "
//                    + "FROM `books`b JOIN categories c ON b.Category_ID = c.Category_ID "
//                    + "JOIN authors a ON b.Author_ID = a.Author_ID JOIN publishers p ON b.Publisher_ID = p.Publisher_ID WHERE 1=1";
//            ArrayList<String> conditions = new ArrayList<>();
//            ArrayList<Object> parameters = new ArrayList<>();
//            boolean isID_All = true;
//            boolean isCategory_All = true;
//            boolean isAuthor_All = true;
//            boolean isPublisher_All = true;
//            
//            if(book_id.getSelectedIndex() != 0) isID_All = false;
//            if(book_category.getSelectedIndex() != 0) isCategory_All = false;
//            if(book_author.getSelectedIndex() != 0) isAuthor_All = false;
//            if(book_publisher.getSelectedIndex() != 0) isPublisher_All = false;
//            
//            if(!isID_All) {
//                conditions.add("b.Book_ID = ? ");
//                parameters.add(book_id.getSelectedItem().toString());
//            }
//            if(!isCategory_All) {
//                conditions.add("c.Category_ID = ? ");
//                CategoryItem ci = (CategoryItem)book_category.getSelectedItem();
//                parameters.add(ci.getId());
//            }
//            if(!isAuthor_All) {
//                conditions.add("a.Author_ID = ? ");
//                AuthorItem ai = (AuthorItem)book_author.getSelectedItem();
//                parameters.add(ai.getId());
//            }
//            if(!isPublisher_All) {
//                conditions.add("p.Publisher_ID = ? ");
//                PublisherItem pi = (PublisherItem)book_publisher.getSelectedItem();
//                parameters.add(pi.getId());
//            }
//            for (String condition : conditions) {
//                baseQuery += " AND " + condition;
//            }
//            
//            try {
//                    PreparedStatement ps = con.prepareStatement(baseQuery);
//                    for (int i = 0; i < parameters.size(); i++) {
//                    ps.setObject(i+1, parameters.get(i));
//                }
//                    ResultSet r = ps.executeQuery();
//
//                    ResultSetMetaData rsd = r.getMetaData();
//                    int columns = rsd.getColumnCount();
//
//                    DefaultTableModel model = (DefaultTableModel)book_table.getModel();
//                    model.setRowCount(0);
//
//                    while(r.next()){
//                        Object[] obj = new Object[columns];
//                        obj[0] = r.getString("Book_ID");
//                        obj[1] = r.getString("Name");
//                        obj[2] = new CategoryItem(r.getInt("Category_ID"), r.getString("Category"));
//                        obj[3] = new AuthorItem(r.getInt("Author_ID"), r.getString("Author"));
//                        obj[4] = new PublisherItem(r.getInt("Publisher_ID"), r.getString("Publisher"));
//                        obj[5] = r.getString("Quantity");
//                        model.addRow(obj);
//                    }
//
//                } catch (SQLException ex) {
//                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }//GEN-LAST:event_book_id_searchItemStateChanged

    private void book_categoryItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_book_categoryItemStateChanged
        // TODO add your handling code here:
//        if(evt.getStateChange() == ItemEvent.SELECTED){
//            if(!book_combobox_check.book_id_check || !book_combobox_check.book_category_check 
//                || !book_combobox_check.book_author_check || !book_combobox_check.book_publisher_check){
//                return;
//            }
//                String baseQuery = "SELECT b.Book_ID, b.Name,c.Category_ID ,c.Name as Category,a.Author_ID,a.Name as Author,p.Publisher_ID,p.Name as Publisher, b.Quantity "
//                    + "FROM `books`b JOIN categories c ON b.Category_ID = c.Category_ID "
//                    + "JOIN authors a ON b.Author_ID = a.Author_ID JOIN publishers p ON b.Publisher_ID = p.Publisher_ID WHERE 1=1";
//            ArrayList<String> conditions = new ArrayList<>();
//            ArrayList<Object> parameters = new ArrayList<>();
//            boolean isID_All = true;
//            boolean isCategory_All = true;
//            boolean isAuthor_All = true;
//            boolean isPublisher_All = true;
//            
//            if(book_id.getSelectedIndex() != 0) isID_All = false;
//            if(book_category.getSelectedIndex() != 0) isCategory_All = false;
//            if(book_author.getSelectedIndex() != 0) isAuthor_All = false;
//            if(book_publisher.getSelectedIndex() != 0) isPublisher_All = false;
//            
//            if(!isID_All) {
//                conditions.add("b.Book_ID = ? ");
//                parameters.add(book_id.getSelectedItem().toString());
//            }
//            if(!isCategory_All) {
//                conditions.add("c.Category_ID = ? ");
//                CategoryItem ci = (CategoryItem)book_category.getSelectedItem();
//                parameters.add(ci.getId());
//            }
//            if(!isAuthor_All) {
//                conditions.add("a.Author_ID = ? ");
//                AuthorItem ai = (AuthorItem)book_author.getSelectedItem();
//                parameters.add(ai.getId());
//            }
//            if(!isPublisher_All) {
//                conditions.add("p.Publisher_ID = ? ");
//                PublisherItem pi = (PublisherItem)book_publisher.getSelectedItem();
//                parameters.add(pi.getId());
//            }
//            for (String condition : conditions) {
//                baseQuery += " AND " + condition;
//            }
//            
//            try {
//                    PreparedStatement ps = con.prepareStatement(baseQuery);
//                    for (int i = 0; i < parameters.size(); i++) {
//                    ps.setObject(i+1, parameters.get(i));
//                }
//                    ResultSet r = ps.executeQuery();
//
//                    ResultSetMetaData rsd = r.getMetaData();
//                    int columns = rsd.getColumnCount();
//
//                    DefaultTableModel model = (DefaultTableModel)book_table.getModel();
//                    model.setRowCount(0);
//
//                    while(r.next()){
//                        Object[] obj = new Object[columns];
//                        obj[0] = r.getString("Book_ID");
//                        obj[1] = r.getString("Name");
//                        obj[2] = new CategoryItem(r.getInt("Category_ID"), r.getString("Category"));
//                        obj[3] = new AuthorItem(r.getInt("Author_ID"), r.getString("Author"));
//                        obj[4] = new PublisherItem(r.getInt("Publisher_ID"), r.getString("Publisher"));
//                        obj[5] = r.getString("Quantity");
//                        model.addRow(obj);
//                    }
//
//                } catch (SQLException ex) {
//                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }        
    }//GEN-LAST:event_book_categoryItemStateChanged

    private void book_authorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_book_authorItemStateChanged
        // TODO add your handling code here:
//        if(evt.getStateChange() == ItemEvent.SELECTED){
//            if(!book_combobox_check.book_id_check || !book_combobox_check.book_category_check 
//                || !book_combobox_check.book_author_check || !book_combobox_check.book_publisher_check){
//                return;
//            }
//                String baseQuery = "SELECT b.Book_ID, b.Name,c.Category_ID ,c.Name as Category,a.Author_ID,a.Name as Author,p.Publisher_ID,p.Name as Publisher, b.Quantity "
//                    + "FROM `books`b JOIN categories c ON b.Category_ID = c.Category_ID "
//                    + "JOIN authors a ON b.Author_ID = a.Author_ID JOIN publishers p ON b.Publisher_ID = p.Publisher_ID WHERE 1=1";
//            ArrayList<String> conditions = new ArrayList<>();
//            ArrayList<Object> parameters = new ArrayList<>();
//            boolean isID_All = true;
//            boolean isCategory_All = true;
//            boolean isAuthor_All = true;
//            boolean isPublisher_All = true;
//            
//            if(book_id.getSelectedIndex() != 0) isID_All = false;
//            if(book_category.getSelectedIndex() != 0) isCategory_All = false;
//            if(book_author.getSelectedIndex() != 0) isAuthor_All = false;
//            if(book_publisher.getSelectedIndex() != 0) isPublisher_All = false;
//            
//            if(!isID_All) {
//                conditions.add("b.Book_ID = ? ");
//                parameters.add(book_id.getSelectedItem().toString());
//            }
//            if(!isCategory_All) {
//                conditions.add("c.Category_ID = ? ");
//                CategoryItem ci = (CategoryItem)book_category.getSelectedItem();
//                parameters.add(ci.getId());
//            }
//            if(!isAuthor_All) {
//                conditions.add("a.Author_ID = ? ");
//                AuthorItem ai = (AuthorItem)book_author.getSelectedItem();
//                parameters.add(ai.getId());
//            }
//            if(!isPublisher_All) {
//                conditions.add("p.Publisher_ID = ? ");
//                PublisherItem pi = (PublisherItem)book_publisher.getSelectedItem();
//                parameters.add(pi.getId());
//            }
//            for (String condition : conditions) {
//                baseQuery += " AND " + condition;
//            }
//            
//            try {
//                    PreparedStatement ps = con.prepareStatement(baseQuery);
//                    for (int i = 0; i < parameters.size(); i++) {
//                    ps.setObject(i+1, parameters.get(i));
//                }
//                    ResultSet r = ps.executeQuery();
//
//                    ResultSetMetaData rsd = r.getMetaData();
//                    int columns = rsd.getColumnCount();
//
//                    DefaultTableModel model = (DefaultTableModel)book_table.getModel();
//                    model.setRowCount(0);
//
//                    while(r.next()){
//                        Object[] obj = new Object[columns];
//                        obj[0] = r.getString("Book_ID");
//                        obj[1] = r.getString("Name");
//                        obj[2] = new CategoryItem(r.getInt("Category_ID"), r.getString("Category"));
//                        obj[3] = new AuthorItem(r.getInt("Author_ID"), r.getString("Author"));
//                        obj[4] = new PublisherItem(r.getInt("Publisher_ID"), r.getString("Publisher"));
//                        obj[5] = r.getString("Quantity");
//                        model.addRow(obj);
//                    }
//
//                } catch (SQLException ex) {
//                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }        
    }//GEN-LAST:event_book_authorItemStateChanged

    private void book_publisherItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_book_publisherItemStateChanged
        // TODO add your handling code here:
//        if(evt.getStateChange() == ItemEvent.SELECTED){
//            if(!book_combobox_check.book_id_check || !book_combobox_check.book_category_check 
//                || !book_combobox_check.book_author_check || !book_combobox_check.book_publisher_check){
//                return;
//            }
//                String baseQuery = "SELECT b.Book_ID, b.Name,c.Category_ID ,c.Name as Category,a.Author_ID,a.Name as Author,p.Publisher_ID,p.Name as Publisher, b.Quantity "
//                    + "FROM `books`b JOIN categories c ON b.Category_ID = c.Category_ID "
//                    + "JOIN authors a ON b.Author_ID = a.Author_ID JOIN publishers p ON b.Publisher_ID = p.Publisher_ID WHERE 1=1";
//            ArrayList<String> conditions = new ArrayList<>();
//            ArrayList<Object> parameters = new ArrayList<>();
//            boolean isID_All = true;
//            boolean isCategory_All = true;
//            boolean isAuthor_All = true;
//            boolean isPublisher_All = true;
//            
//            if(book_id.getSelectedIndex() != 0) isID_All = false;
//            if(book_category.getSelectedIndex() != 0) isCategory_All = false;
//            if(book_author.getSelectedIndex() != 0) isAuthor_All = false;
//            if(book_publisher.getSelectedIndex() != 0) isPublisher_All = false;
//            
//            if(!isID_All) {
//                conditions.add("b.Book_ID = ? ");
//                parameters.add(book_id.getSelectedItem().toString());
//            }
//            if(!isCategory_All) {
//                conditions.add("c.Category_ID = ? ");
//                CategoryItem ci = (CategoryItem)book_category.getSelectedItem();
//                parameters.add(ci.getId());
//            }
//            if(!isAuthor_All) {
//                conditions.add("a.Author_ID = ? ");
//                AuthorItem ai = (AuthorItem)book_author.getSelectedItem();
//                parameters.add(ai.getId());
//            }
//            if(!isPublisher_All) {
//                conditions.add("p.Publisher_ID = ? ");
//                PublisherItem pi = (PublisherItem)book_publisher.getSelectedItem();
//                parameters.add(pi.getId());
//            }
//            for (String condition : conditions) {
//                baseQuery += " AND " + condition;
//            }
//            
//            try {
//                    PreparedStatement ps = con.prepareStatement(baseQuery);
//                    for (int i = 0; i < parameters.size(); i++) {
//                    ps.setObject(i+1, parameters.get(i));
//                }
//                    ResultSet r = ps.executeQuery();
//
//                    ResultSetMetaData rsd = r.getMetaData();
//                    int columns = rsd.getColumnCount();
//
//                    DefaultTableModel model = (DefaultTableModel)book_table.getModel();
//                    model.setRowCount(0);
//
//                    while(r.next()){
//                        Object[] obj = new Object[columns];
//                        obj[0] = r.getString("Book_ID");
//                        obj[1] = r.getString("Name");
//                        obj[2] = new CategoryItem(r.getInt("Category_ID"), r.getString("Category"));
//                        obj[3] = new AuthorItem(r.getInt("Author_ID"), r.getString("Author"));
//                        obj[4] = new PublisherItem(r.getInt("Publisher_ID"), r.getString("Publisher"));
//                        obj[5] = r.getString("Quantity");
//                        model.addRow(obj);
//                    }
//
//                } catch (SQLException ex) {
//                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }        
    }//GEN-LAST:event_book_publisherItemStateChanged

    private void manage_users_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manage_users_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(user_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Book_panelLoad();
        TurnOffButtons();
        manage_users_button.setBackground(new Color(255,0,51));        
    }//GEN-LAST:event_manage_users_buttonActionPerformed

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

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
            String baseQuery = "SELECT b.Book_ID, b.Name,c.Category_ID ,c.Name as Category,a.Author_ID,a.Name as Author,p.Publisher_ID,p.Name as Publisher, b.Quantity "
                    + "FROM `books`b JOIN categories c ON b.Category_ID = c.Category_ID "
                    + "JOIN authors a ON b.Author_ID = a.Author_ID JOIN publishers p ON b.Publisher_ID = p.Publisher_ID WHERE 1=1";
            ArrayList<String> conditions = new ArrayList<>();
            ArrayList<Object> parameters = new ArrayList<>();
            boolean isID_All = true;
            boolean isCategory_All = true;
            boolean isAuthor_All = true;
            boolean isPublisher_All = true;
            
            if(book_id_search.getSelectedIndex() != 0) isID_All = false;
            if(book_category_search.getSelectedIndex() != 0) isCategory_All = false;
            if(book_author_search.getSelectedIndex() != 0) isAuthor_All = false;
            if(book_publisher_search.getSelectedIndex() != 0) isPublisher_All = false;
            
            if(!isID_All) {
                conditions.add("b.Book_ID = ? ");
                parameters.add(book_id_search.getSelectedItem().toString());
            }
            if(!isCategory_All) {
                conditions.add("c.Category_ID = ? ");
                CategoryItem ci = (CategoryItem)book_category_search.getSelectedItem();
                parameters.add(ci.getId());
            }
            if(!isAuthor_All) {
                conditions.add("a.Author_ID = ? ");
                AuthorItem ai = (AuthorItem)book_author_search.getSelectedItem();
                parameters.add(ai.getId());
            }
            if(!isPublisher_All) {
                conditions.add("p.Publisher_ID = ? ");
                PublisherItem pi = (PublisherItem)book_publisher_search.getSelectedItem();
                parameters.add(pi.getId());
            }
            for (String condition : conditions) {
                baseQuery += " AND " + condition;
            }
            
            try {
                    PreparedStatement ps = con.prepareStatement(baseQuery);
                    for (int i = 0; i < parameters.size(); i++) {
                    ps.setObject(i+1, parameters.get(i));
                }
                    ResultSet r = ps.executeQuery();

                    ResultSetMetaData rsd = r.getMetaData();
                    int columns = rsd.getColumnCount();

                    DefaultTableModel model = (DefaultTableModel)book_table.getModel();
                    model.setRowCount(0);

                    while(r.next()){
                        Object[] obj = new Object[columns];
                        obj[0] = r.getString("Book_ID");
                        obj[1] = r.getString("Name");
                        obj[2] = new CategoryItem(r.getInt("Category_ID"), r.getString("Category"));
                        obj[3] = new AuthorItem(r.getInt("Author_ID"), r.getString("Author"));
                        obj[4] = new PublisherItem(r.getInt("Publisher_ID"), r.getString("Publisher"));
                        obj[5] = r.getString("Quantity");
                        model.addRow(obj);
                    }

                } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminDashboard("admin").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BookNumber;
    private javax.swing.JLabel IssueNumber;
    private javax.swing.JLabel JLabel;
    private javax.swing.JPanel JPanel;
    private javax.swing.JLabel Jlabel;
    private javax.swing.JPanel Logout_panel;
    private javax.swing.JPanel Parent_panel;
    private javax.swing.JButton Submit_button;
    private javax.swing.JLabel UserNumber;
    private javax.swing.JLabel account_name_label;
    private javax.swing.JLabel admin_name;
    private javax.swing.JButton author_addButton;
    private javax.swing.JTextField author_book_count;
    private javax.swing.JTextField author_city;
    private javax.swing.JButton author_deleteButton;
    private javax.swing.JComboBox author_id;
    private javax.swing.JTextField author_name;
    private javax.swing.JPanel author_panel;
    private rojeru_san.complementos.RSTableMetro author_table;
    private javax.swing.JButton author_updateButton;
    private javax.swing.JButton book_addButton;
    private javax.swing.JComboBox<AuthorItem> book_author;
    private javax.swing.JComboBox book_author_search;
    private javax.swing.JComboBox<CategoryItem> book_category;
    private javax.swing.JComboBox book_category_search;
    private javax.swing.JButton book_deleteButton;
    private javax.swing.JTextField book_id;
    private javax.swing.JComboBox book_id_search;
    private javax.swing.JTextField book_name;
    private javax.swing.JPanel book_panel;
    private javax.swing.JComboBox<PublisherItem> book_publisher;
    private javax.swing.JComboBox book_publisher_search;
    private javax.swing.JTextField book_quantity;
    private rojeru_san.complementos.RSTableMetro book_table;
    private javax.swing.JButton book_updateButton;
    private javax.swing.JButton category_addButton;
    private javax.swing.JTextField category_book_count;
    private javax.swing.JButton category_deleteButton;
    private javax.swing.JComboBox<String> category_id;
    private javax.swing.JTextField category_name;
    private javax.swing.JPanel category_panel;
    private javax.swing.JComboBox<String> category_status;
    private rojeru_san.complementos.RSTableMetro category_table;
    private javax.swing.JButton category_updateButton;
    private javax.swing.JButton change_password_button;
    private javax.swing.JPanel changepassword_panel;
    private app.bolivia.swing.JCTextField contact_number_label;
    private javax.swing.JButton edit_profile_button;
    private javax.swing.JPanel editprofile_panel;
    private javax.swing.JButton editprofile_resetButton;
    private javax.swing.JButton editprofile_saveButton;
    private app.bolivia.swing.JCTextField email_label;
    private javax.swing.JButton home_page_button;
    private javax.swing.JPanel home_page_panel;
    private javax.swing.JPanel issue_panel;
    private javax.swing.JTextField issue_searchby_book;
    private com.toedter.calendar.JDateChooser issue_searchby_issuedate;
    private javax.swing.JComboBox<String> issue_searchby_status;
    private javax.swing.JTextField issue_searchby_user;
    private javax.swing.JComboBox<String> issue_sorted;
    private rojeru_san.complementos.RSTableMetro issue_table;
    private javax.swing.JLabel j;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
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
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JButton logout_button;
    private javax.swing.JButton manage_authors_button;
    private javax.swing.JButton manage_books_button;
    private javax.swing.JButton manage_categories_button;
    private javax.swing.JButton manage_issues_button;
    private javax.swing.JButton manage_publishers_button;
    private javax.swing.JButton manage_users_button;
    private rojeru_san.complementos.RSTableMetro most_issue_books_table;
    private javax.swing.JButton publisher_addButton;
    private javax.swing.JTextField publisher_address;
    private javax.swing.JTextField publisher_book_count;
    private javax.swing.JButton publisher_deleteButton;
    private javax.swing.JComboBox publisher_id;
    private javax.swing.JTextField publisher_name;
    private javax.swing.JPanel publisher_panel;
    private rojeru_san.complementos.RSTableMetro publisher_table;
    private javax.swing.JButton publisher_updateButton;
    private rojeru_san.complementos.RSTableMetro recent_issue;
    private javax.swing.JButton search_button;
    private app.bolivia.swing.JCTextField txtpassword;
    private app.bolivia.swing.JCTextField txtpassword1;
    private app.bolivia.swing.JCTextField txtpassword2;
    private javax.swing.JPanel user_panel;
    // End of variables declaration//GEN-END:variables
}
