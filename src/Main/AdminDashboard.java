/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Main;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
//        Connect();
        AdminGreeting_Load();
        Home_page_Load();
    }
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
//    public void Connect(){
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//                    con = DriverManager.getConnection("jdbc:mysql://localhost/" + Database.DB_Name,Database.DB_UserName,Database.DB_Password);
//        } catch (ClassNotFoundException | SQLException ex) {
//            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }   
//Load AdminGreeting in 
    private void AdminGreeting_Load() {
//        Recent_Issue_Load();
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
        logout_button.setBackground(color);
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
//Initialize category_panel
    private void Category_panelLoad() {
        Category_TableLoad();
        Category_IDLoad();
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
        category_id.setSelectedIndex(-1);
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
             
            while(rs.next()){
                category_id.addItem(new CategoryItem(rs.getInt("Category_ID"),rs.getString("Name")));
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
        author_id.setSelectedIndex(-1);
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
             
            while(rs.next()){
                author_id.addItem(new AuthorItem(rs.getInt("Author_ID"),rs.getString("Name")));
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
        publisher_id.setSelectedIndex(-1);
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
             
            while(rs.next()){
                publisher_id.addItem(new PublisherItem(rs.getInt("Publisher_ID"),rs.getString("Name")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }           
    } 
//---------------------------------------------------------------------------------------------------------------------------------------------    
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
        
        if(Sorted.getSelectedItem().toString().equals("Oldest to Newest")) sortByNewestToOldest = false;
        if(searchby_user.getText().equals("")) filterByUserName = false;
        if(searchby_book.getText().equals("")) filterByBookName = false;
        if(searchby_issuedate.getDate() == null) filterByIssueDate = false;
        
        if(filterByUserName) {
            conditions.add("username = ? ");
            parameters.add(searchby_user.getText());
        }
        if(filterByBookName) {
            conditions.add("name = ? ");
            parameters.add(searchby_book.getText());
        }
        if(filterByIssueDate) {
            conditions.add("IssueDate = ? ");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(searchby_issuedate.getDate());
            parameters.add(formattedDate);
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
        Book_TableLoad();
        Book_IDLoad();
        Book_buttonLoad();
        Book_TextandComboboxLoad();
    }       
//Load button_status when initialize AdminDashboard
    private void Book_buttonLoad() {
        book_addButton.setEnabled(true);
        book_updateButton.setEnabled(false);
        book_deleteButton.setEnabled(false);
    }
//Load text and combobox status when initialize AdminDashboard
    private void Book_TextandComboboxLoad() {
        book_id.setSelectedIndex(-1);
        book_name.setText("");
        book_quantity.setText("");
        book_category.setSelectedIndex(-1);
        book_author.setSelectedIndex(-1);
        book_publisher.setSelectedIndex(-1);
    }
// Load publisher_table in AdminDashboard
    private void Book_TableLoad(){
            try {
                pst = con.prepareStatement("SELECT b.Book_ID, b.Name,c.Category_ID ,c.Name as Category,a.Author_ID,a.Name as Author,p.Publisher_ID,p.Name as Publisher, b.Quantity FROM `books`b "
                        + "JOIN categories c ON b.Category_ID = c.Category_ID "
                        + "JOIN authors a ON b.Author_ID = a.Author_ID "
                        + "JOIN publishers p ON b.Publisher_ID = p.Publisher_ID;");
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
    private void Book_IDLoad(){
        try {
            book_id.removeAllItems();        
            book_category.removeAllItems();
            book_author.removeAllItems();
            book_publisher.removeAllItems();
            //Load book_id
            pst = con.prepareStatement("Select Book_ID from Books b");
            rs = pst.executeQuery();
            while(rs.next()){
                book_id.addItem(rs.getString("Book_ID"));
            }
            //Load book_category
            pst = con.prepareStatement("SELECT Category_ID, Name FROM categories");
            rs = pst.executeQuery();
            while(rs.next()){
                book_category.addItem(new CategoryItem(rs.getInt("Category_ID"), rs.getString("Name")));
            }
            //Load book_author
            pst = con.prepareStatement("SELECT Author_ID, Name FROM authors");
            rs = pst.executeQuery();
            while(rs.next()){
                book_author.addItem(new AuthorItem(rs.getInt("Author_ID"), rs.getString("Name")));
            }
            //Load book_publisher
            pst = con.prepareStatement("SELECT Publisher_ID, Name FROM publishers");
            rs = pst.executeQuery();
            while(rs.next()){
                book_publisher.addItem(new PublisherItem(rs.getInt("Publisher_ID"),rs.getString("Name")));
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
    private void DeleteBookCountForCategories(int Category_ID) {
        try {
            pst = con.prepareStatement("Update Categories SET Book_count = Book_count - 1 Where Category_ID = " + Category_ID);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void DeleteBookCountForAuthors(int Author_ID) {
        try {
            pst = con.prepareStatement("Update Authors SET Book_count = Book_count - 1 Where Author_ID = " + Author_ID);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void DeleteBookCountForPublishers(int Publisher_ID) {
        try {
            pst = con.prepareStatement("Update Publishers SET Book_count = Book_count - 1 Where Publisher_ID = " + Publisher_ID);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
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
        manage_categories_button = new javax.swing.JButton();
        home_page_button = new javax.swing.JButton();
        logout_button = new javax.swing.JButton();
        manage_authors_button = new javax.swing.JButton();
        manage_publishers_button = new javax.swing.JButton();
        manage_issues_button = new javax.swing.JButton();
        manage_books_button = new javax.swing.JButton();
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
        category_id = new javax.swing.JComboBox();
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
        Sorted = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        searchby_book = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        search_button = new javax.swing.JButton();
        searchby_user = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        searchby_issuedate = new com.toedter.calendar.JDateChooser();
        book_panel = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        book_table = new rojeru_san.complementos.RSTableMetro();
        jPanel4 = new javax.swing.JPanel();
        book_id = new javax.swing.JComboBox();
        book_name = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        book_author = new javax.swing.JComboBox<>();
        book_publisher = new javax.swing.JComboBox<>();
        book_category = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        book_quantity = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        book_addButton = new javax.swing.JButton();
        book_updateButton = new javax.swing.JButton();
        book_deleteButton = new javax.swing.JButton();

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

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Feature");

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

        javax.swing.GroupLayout Logout_panelLayout = new javax.swing.GroupLayout(Logout_panel);
        Logout_panel.setLayout(Logout_panelLayout);
        Logout_panelLayout.setHorizontalGroup(
            Logout_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 237, Short.MAX_VALUE)
        );
        Logout_panelLayout.setVerticalGroup(
            Logout_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 39, Short.MAX_VALUE)
        );

        manage_categories_button.setBackground(new java.awt.Color(51, 51, 51));
        manage_categories_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        manage_categories_button.setForeground(new java.awt.Color(255, 255, 255));
        manage_categories_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Sell_26px.png"))); // NOI18N
        manage_categories_button.setText("Manage Categories");
        manage_categories_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        manage_categories_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_categories_buttonActionPerformed(evt);
            }
        });

        home_page_button.setBackground(new java.awt.Color(255, 0, 51));
        home_page_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        home_page_button.setForeground(new java.awt.Color(255, 255, 255));
        home_page_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/home_24px.png"))); // NOI18N
        home_page_button.setText("Home Page");
        home_page_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        home_page_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                home_page_buttonActionPerformed(evt);
            }
        });

        logout_button.setBackground(new java.awt.Color(51, 51, 51));
        logout_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        logout_button.setForeground(new java.awt.Color(255, 255, 255));
        logout_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        logout_button.setText("Logout");
        logout_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        logout_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logout_buttonActionPerformed(evt);
            }
        });

        manage_authors_button.setBackground(new java.awt.Color(51, 51, 51));
        manage_authors_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        manage_authors_button.setForeground(new java.awt.Color(255, 255, 255));
        manage_authors_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        manage_authors_button.setText("Manage Authors");
        manage_authors_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        manage_authors_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_authors_buttonActionPerformed(evt);
            }
        });

        manage_publishers_button.setBackground(new java.awt.Color(51, 51, 51));
        manage_publishers_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        manage_publishers_button.setForeground(new java.awt.Color(255, 255, 255));
        manage_publishers_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        manage_publishers_button.setText("Manage Publishers");
        manage_publishers_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        manage_publishers_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_publishers_buttonActionPerformed(evt);
            }
        });

        manage_issues_button.setBackground(new java.awt.Color(51, 51, 51));
        manage_issues_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        manage_issues_button.setForeground(new java.awt.Color(255, 255, 255));
        manage_issues_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        manage_issues_button.setText("Manage Issues");
        manage_issues_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        manage_issues_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_issues_buttonActionPerformed(evt);
            }
        });

        manage_books_button.setBackground(new java.awt.Color(51, 51, 51));
        manage_books_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        manage_books_button.setForeground(new java.awt.Color(255, 255, 255));
        manage_books_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        manage_books_button.setText("Manage Books");
        manage_books_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        manage_books_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_books_buttonActionPerformed(evt);
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
            .addComponent(logout_button, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(manage_authors_button, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(manage_publishers_button, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(manage_issues_button, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(manage_books_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGap(18, 18, 18)
                .addComponent(logout_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                .addComponent(Logout_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(116, 116, 116))
        );

        Parent_panel.setLayout(new java.awt.CardLayout());

        home_page_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        UserNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        UserNumber.setForeground(new java.awt.Color(102, 102, 102));
        UserNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_People_50px.png"))); // NOI18N

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

        JLabel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        JLabel.setForeground(new java.awt.Color(102, 102, 102));
        JLabel.setText("No Of Books");

        jPanel9.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        BookNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        BookNumber.setForeground(new java.awt.Color(102, 102, 102));
        BookNumber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BookNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Book_Shelf_50px.png"))); // NOI18N

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

        IssueNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        IssueNumber.setForeground(new java.awt.Color(102, 102, 102));
        IssueNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Sell_50px.png"))); // NOI18N

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

        j.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        j.setForeground(new java.awt.Color(102, 102, 102));
        j.setText("Issue Books");

        Jlabel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        Jlabel.setForeground(new java.awt.Color(102, 102, 102));
        Jlabel.setText("No Of Users");

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

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel20.setText("Most-issued Books");

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

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel21.setText("Recent Issues");

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

        category_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                category_idActionPerformed(evt);
            }
        });

        category_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Inactive" }));

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

        jLabel19.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Info");

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

        author_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                author_idActionPerformed(evt);
            }
        });

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

        jLabel26.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Info");

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
                .addContainerGap(260, Short.MAX_VALUE))
        );

        jLabel27.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/member_50px.png"))); // NOI18N
        jLabel27.setText("Author");

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

        jLabel36.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("Info");

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
                .addContainerGap(260, Short.MAX_VALUE))
        );

        jLabel28.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_People_50px.png"))); // NOI18N
        jLabel28.setText("Publisher");

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

        Sorted.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Newest to Oldest", "Oldest to Newest" }));
        Sorted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SortedActionPerformed(evt);
            }
        });

        jLabel3.setText("SORT BY");

        searchby_book.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchby_bookActionPerformed(evt);
            }
        });

        jLabel4.setText("Search By");

        search_button.setText("search");
        search_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_buttonActionPerformed(evt);
            }
        });

        searchby_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchby_userActionPerformed(evt);
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
                                        .addComponent(Sorted, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, issue_panelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(searchby_book, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(issue_panelLayout.createSequentialGroup()
                        .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(searchby_issuedate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(searchby_user, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(search_button)))
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
                    .addComponent(Sorted, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchby_book, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchby_user, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(search_button))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(issue_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(searchby_issuedate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 154, Short.MAX_VALUE)
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

        jLabel11.setText("Name");

        jLabel10.setText("ID");

        jLabel12.setText("Quantity");

        jLabel13.setText("Category");

        jLabel29.setText("Author");

        jLabel30.setText("Publisher");

        jLabel31.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("Info");

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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(158, 158, 158)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE))
                        .addGap(42, 42, 42)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(book_quantity)
                                    .addComponent(book_id, 0, 149, Short.MAX_VALUE)
                                    .addComponent(book_name))
                                .addGap(42, 42, 42)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel30)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(book_addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                                .addComponent(book_updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(21, 21, 21)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(book_author, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(book_category, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(book_publisher, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(book_deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(233, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(book_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(book_category, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(book_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(book_author, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(book_publisher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(book_quantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(jLabel30)))
                    .addComponent(jLabel29))
                .addGap(31, 31, 31)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(book_addButton)
                    .addComponent(book_updateButton)
                    .addComponent(book_deleteButton))
                .addContainerGap(98, Short.MAX_VALUE))
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                category_id.setSelectedIndex(-1);
                category_name.setText("");
                category_status.setSelectedIndex(-1);
                category_book_count.setText("");
                Category_TableLoad();
                Category_IDLoad();
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
                category_status.setSelectedIndex(-1);
                category_id.setSelectedIndex(-1);
                category_book_count.setText("");
                Category_TableLoad();
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
            pst = con.prepareStatement("Delete from Categories Where Category_ID = ?");
            pst.setString(1, id);
            int k = pst.executeUpdate();
            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Data deleted");
                category_name.setText("");
                category_id.setSelectedIndex(-1);
                category_book_count.setText("");
                category_status.setSelectedIndex(-1);
                Category_TableLoad();
                Category_IDLoad();
                category_addButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
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
        try {
            pst = con.prepareStatement("Insert into Authors(Name,City,Book_count)values(?,?,?)");
            pst.setString(1, name);
            pst.setString(2, city);
            pst.setString(3, book_count);
            int k = pst.executeUpdate();            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Author created");
                author_id.setSelectedIndex(-1);
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
                author_id.setSelectedIndex(-1);
                author_book_count.setText("");
                Author_TableLoad();
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
        try {
            pst = con.prepareStatement("Insert into Publishers(Name,Address,Book_count)values(?,?,?)");
            pst.setString(1, name);
            pst.setString(2, address);
            pst.setString(3, book_count);
            int k = pst.executeUpdate();            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Data created");
                publisher_id.setSelectedIndex(-1);
                publisher_name.setText("");
                publisher_address.setText("");
                publisher_book_count.setText("");
                Publisher_TableLoad();
                Publisher_IDLoad();
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
            pst = con.prepareStatement("Delete from Publishers Where Publisher_ID = ?");
            pst.setString(1, id);
            int k = pst.executeUpdate();
            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Data deleted");
                publisher_name.setText("");
                publisher_id.setSelectedIndex(-1);
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
        Issue_SQLQuery();
        TurnOffButtons();
        manage_issues_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_manage_issues_buttonActionPerformed

    private void SortedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SortedActionPerformed
        // TODO add your handling code here:
        Issue_SQLQuery();
    }//GEN-LAST:event_SortedActionPerformed

    private void searchby_bookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchby_bookActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchby_bookActionPerformed

    private void search_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_buttonActionPerformed
        // TODO add your handling code here:
        Issue_SQLQuery();
    }//GEN-LAST:event_search_buttonActionPerformed

    private void searchby_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchby_userActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchby_userActionPerformed

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
        book_id.setSelectedItem(model.getValueAt(selectIndex,0).toString());
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
//            pst = con.prepareStatement("Select Category_ID from Categories Where Name = \"" + category + "\"");
//            rs = pst.executeQuery();
//            rs.next();
//            category = rs.getString(1);
            UpdateBookCountForCategories(category);

//            pst = con.prepareStatement("Select Author_ID from Authors Where Name = \"" + author + "\"");
//            rs = pst.executeQuery();
//            rs.next();
//            author = rs.getString(1);
            UpdateBookCountForAuthors(author);

//            pst = con.prepareStatement("Select Publisher_ID from Publishers Where Name = \"" + publisher + "\"");
//            rs = pst.executeQuery();
//            rs.next();
//            publisher = rs.getString(1);
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
        int selectIndex = book_table.getSelectedRow();
        String id = model.getValueAt(selectIndex, 0).toString();        
        String name = book_name.getText();
        int category = ((CategoryItem)book_category.getSelectedItem()).getId();
        int author = ((AuthorItem)book_author.getSelectedItem()).getId();
        int publisher = ((PublisherItem)book_publisher.getSelectedItem()).getId();
        String quantity = book_quantity.getText();
        try {
//            pst = con.prepareStatement("Select Category_ID from Categories Where Name = \"" + category + "\"");
//            rs = pst.executeQuery();
//            rs.next();
//            category = rs.getString(1);
//            
//            pst = con.prepareStatement("Select Author_ID from Authors Where Name = \"" + author + "\"");
//            rs = pst.executeQuery();
//            rs.next();
//            author = rs.getString(1);
//
//            pst = con.prepareStatement("Select Publisher_ID from Publishers Where Name = \"" + publisher + "\"");
//            rs = pst.executeQuery();
//            rs.next();
//            publisher = rs.getString(1);
            
            pst = con.prepareStatement("Update Books set Name = ?, Category_ID = ?, Author_ID = ?, Publisher_ID = ?, Quantity = ? where Book_ID = ?");
            pst.setString(1, name);
            pst.setInt(2, category);
            pst.setInt(3, author);
            pst.setInt(4, publisher);
            pst.setString(5, quantity);
            pst.setString(6, id);
            int k = pst.executeUpdate();            
            if(k == 1){
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
                DeleteBookCountForCategories(category);
                DeleteBookCountForAuthors(author);
                DeleteBookCountForPublishers(publisher);
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
        category_id.setSelectedItem(model.getValueAt(selectIndex,0).toString());
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
        author_id.setSelectedItem(model.getValueAt(selectIndex,0).toString());
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
        publisher_id.setSelectedItem(model.getValueAt(selectIndex,0).toString());
        publisher_name.setText(model.getValueAt(selectIndex,1).toString());
        publisher_address.setText(model.getValueAt(selectIndex,2).toString());   
        publisher_book_count.setText(model.getValueAt(selectIndex,3).toString());
        publisher_addButton.setEnabled(false);
        publisher_updateButton.setEnabled(true);
        publisher_deleteButton.setEnabled(true);
    }//GEN-LAST:event_publisher_tableMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        searchby_book.setText("");
        searchby_user.setText("");
        searchby_issuedate.setDate(null);
        Sorted.setSelectedIndex(0);
        Issue_SQLQuery();
    }//GEN-LAST:event_jButton1ActionPerformed

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
                new AdminDashboard("Tester").setVisible(true);
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
    private javax.swing.JComboBox<String> Sorted;
    private javax.swing.JLabel UserNumber;
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
    private javax.swing.JComboBox<CategoryItem> book_category;
    private javax.swing.JButton book_deleteButton;
    private javax.swing.JComboBox book_id;
    private javax.swing.JTextField book_name;
    private javax.swing.JPanel book_panel;
    private javax.swing.JComboBox<PublisherItem> book_publisher;
    private javax.swing.JTextField book_quantity;
    private rojeru_san.complementos.RSTableMetro book_table;
    private javax.swing.JButton book_updateButton;
    private javax.swing.JButton category_addButton;
    private javax.swing.JTextField category_book_count;
    private javax.swing.JButton category_deleteButton;
    private javax.swing.JComboBox category_id;
    private javax.swing.JTextField category_name;
    private javax.swing.JPanel category_panel;
    private javax.swing.JComboBox<String> category_status;
    private rojeru_san.complementos.RSTableMetro category_table;
    private javax.swing.JButton category_updateButton;
    private javax.swing.JButton home_page_button;
    private javax.swing.JPanel home_page_panel;
    private javax.swing.JPanel issue_panel;
    private rojeru_san.complementos.RSTableMetro issue_table;
    private javax.swing.JLabel j;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
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
    private javax.swing.JTextField searchby_book;
    private com.toedter.calendar.JDateChooser searchby_issuedate;
    private javax.swing.JTextField searchby_user;
    // End of variables declaration//GEN-END:variables
}
