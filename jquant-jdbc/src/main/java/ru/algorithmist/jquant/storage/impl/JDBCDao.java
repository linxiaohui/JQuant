package ru.algorithmist.jquant.storage.impl;
 
import org.joda.time.Instant; 
import ru.algorithmist.jquant.storage.Key; 
 
import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Statement; 
import java.sql.Timestamp; 
import java.util.*; 
import java.util.logging.Level; 
import java.util.logging.Logger; 
 
/**
 * @author "Sergey Edunov" 
 * @version 1/20/11 
 */ 
public class JDBCDao { 
 
    private Connection con; 
    private static final String INITIALIZE_TABLE = "CREATE TABLE Parameter " + 
            "(" + 
            "KID bigint NOT NULL, " + 
            "VTime timestamp, " + 
            "Status varchar(10), " + 
            "Value double," + 
            "CONSTRAINT KID_FK FOREIGN KEY (KID) REFERENCES VKey (ID) )"; 
 
    private static final String INITIALIZE_KEY_TABLE = "CREATE TABLE VKey ( " + 
            "ID bigint PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY, " + 
            "VKey char(100) NOT NULL)"; 
 
    private static final String INITIALIZE_INDEX= "CREATE UNIQUE INDEX ParameterIdx_Key on Parameter (KID, VTime)"; 
    private static final String INITIALIZE_KEY_INDEX= "CREATE UNIQUE INDEX VkeyIdx_Key on VKey (VKey)"; 
 
    private static final String CHECK_TABLE = "SELECT count(*) from Parameter"; 
 
    private static final String INSERT_QUERY = "INSERT INTO Parameter (KID, VTime, Status, Value) VALUES (?,?,?,?)"; 
 
    private static final String INSERT_KEY_QUERY = "INSERT INTO VKey (VKey) VALUES (?)"; 
    private static final String LOAD_KEYS  = "SELECT ID, VKey from VKey"; 
    private static final String FIND_KEY  = "SELECT ID from VKey where VKey = ?"; 
 
    private static final String UPDATE_QUERY = "UPDATE Parameter SET Status = ?, Value = ? WHERE KID = ? and VTime = ?"; 
    private static final String FIND_QUERY = "SELECT KID, VTime, Status, Value from Parameter WHERE KID = ? and VTime = ?"; 
    private static final String FIND_RANGE_QUERY = "SELECT KID, VTime, Status, Value from Parameter WHERE KID = ? and VTime >= ? and VTime <= ? order by VTime"; 
    private static final String ITERATE_QUERY = "SELECT KID, VTime, Status, Value from Parameter"; 
 
    private PreparedStatement FIND_STATEMENT; 
    private PreparedStatement FIND_RANGE_STATEMENT; 
    private PreparedStatement INSERT_STATEMENT; 
    private PreparedStatement UPDATE_STATEMENT; 
 
    private static Logger LOG = Logger.getLogger(JDBCDao.class.getName()); 
 
 
    public void init(String url) throws SQLException { 
        con = DriverManager.getConnection(url); 
        TimeZone.setDefault(TimeZone.getTimeZone("GMT")); 
        initialize(); 
        prepareStatements(); 
        loadAllKeys(); 
    } 
 
    private void prepareStatements() { 
        try{ 
            FIND_STATEMENT = con.prepareStatement(FIND_QUERY); 
            FIND_RANGE_STATEMENT = con.prepareStatement(FIND_RANGE_QUERY); 
            INSERT_STATEMENT = con.prepareStatement(INSERT_QUERY); 
            UPDATE_STATEMENT = con.prepareStatement(UPDATE_QUERY); 
        } catch (SQLException e) { 
            LOG.log(Level.SEVERE, e.getMessage(), e); 
        } 
    } 
 
    public void close() { 
        if (con != null) { 
            try { 
                con.close(); 
            } catch (SQLException e) { 
            } 
        } 
    } 
 
    public void update(ValueDO value){ 
        PreparedStatement st = null; 
        try { 
            st = UPDATE_STATEMENT; 
            st.setString(1, value.getStatus()); 
            st.setDouble(2, value.getValue()); 
            st.setLong(3, key(value.getKey())); 
            st.setTimestamp(4, new Timestamp(value.getDate().getMillis())); 
            st.execute(); 
        } catch (Exception e) { 
            LOG.log(Level.SEVERE, e.getMessage(), e); 
        } 
    } 
 
    public void insert(ValueDO value){ 
        PreparedStatement st = null; 
        try { 
            st = INSERT_STATEMENT; 
            st.setLong(1, key(value.getKey())); 
            st.setTimestamp(2, new Timestamp(value.getDate().getMillis())); 
            st.setString(3, value.getStatus()); 
            st.setDouble(4, value.getValue()); 
            st.execute(); 
        } catch (Exception e) { 
            LOG.log(Level.SEVERE, e.getMessage(), e); 
        } 
    } 
 
    public List<ValueDO> find(Key key, Instant from, Instant to) { 
        PreparedStatement st; 
        List<ValueDO> result = new ArrayList<ValueDO>(); 
        try { 
            st = FIND_RANGE_STATEMENT; 
            st.setLong(1, key(key)); 
            st.setTimestamp(2, new Timestamp(from.getMillis())); 
            st.setTimestamp(3, new Timestamp(to.getMillis())); 
            ResultSet rs = st.executeQuery(); 
            while (rs.next()){ 
                result.add(new ValueDO(key, new Instant(rs.getDate(2)), rs.getString(3), rs.getDouble(4))); 
            } 
            return result; 
        } catch (Exception e) { 
            LOG.log(Level.SEVERE, e.getMessage(), e); 
        } 
        return null; 
    } 
 
    public ValueDO find(Key key, Instant date) { 
        PreparedStatement st; 
        try { 
            st = FIND_STATEMENT; 
            st.setLong(1, key(key)); 
            st.setTimestamp(2, new Timestamp(date.getMillis())); 
            ResultSet rs = st.executeQuery(); 
            if (rs.next()){ 
                return new ValueDO(key, new Instant(rs.getDate(2)), rs.getString(3), rs.getDouble(4)); 
            } 
            return null; 
        } catch (Exception e) { 
            LOG.log(Level.SEVERE, e.getMessage(), e); 
        } 
        return null; 
    } 
 
    private void initialize()  { 
        Statement st = null; 
        try { 
            st = con.createStatement(); 
            try { 
                ResultSet rs = st.executeQuery(CHECK_TABLE); 
                rs.next(); 
                int count = rs.getInt(1); 
                LOG.info("Found " + count + " records in database"); 
            } catch (Exception e) { 
                LOG.info("Database table does not exists... initializing"); 
                st.execute(INITIALIZE_KEY_TABLE); 
                st.execute(INITIALIZE_KEY_INDEX); 
                st.execute(INITIALIZE_TABLE); 
                st.execute(INITIALIZE_INDEX); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            if (st != null) 
                try { 
                    st.close(); 
                } catch (SQLException e) {} 
        } 
    } 
 
    public void iterate(JDBCWalker walker)  { 
        PreparedStatement st = null; 
        try { 
            st = con.prepareStatement(ITERATE_QUERY); 
            ResultSet rs = st.executeQuery(); 
            if (rs.next()){ 
                walker.accept(key(rs.getInt(1)), new Instant(rs.getDate(2)), rs.getString(3), rs.getDouble(4)); 
            } 
        } catch (Exception e) { 
            LOG.log(Level.SEVERE, e.getMessage(), e); 
        } finally { 
            if (st != null) 
                try { 
                    st.close(); 
                } catch (SQLException e) {} 
        } 
    } 
 
    //Key management 
    //********************************************************** 
 
    private Map<String, Integer> keyMap = new HashMap<String, Integer>(); 
    private Map<Integer, String> idMap = new HashMap<Integer, String>(); 
 
    public void loadAllKeys() { 
        PreparedStatement st = null; 
        try { 
            st = con.prepareStatement(LOAD_KEYS); 
            ResultSet rs = st.executeQuery(); 
            while (rs.next()){ 
                keyMap.put(rs.getString(2), rs.getInt(1)); 
                idMap.put(rs.getInt(1), rs.getString(2)); 
            } 
        } catch (Exception e) { 
            LOG.log(Level.SEVERE, e.getMessage(), e); 
        } finally { 
            if (st != null) 
                try { 
                    st.close(); 
                } catch (SQLException e) {} 
        } 
    } 
 
    public String key(int id){ 
         return idMap.get(id); 
    } 
 
    public long key(Key key) { 
        long id = key.getId(); 
        if(id > 0){ 
            return id; 
        } 
        String skey = key.toString(); 
        Integer res = keyMap.get(skey); 
        if (res == null){ 
            res = createKey(skey); 
            keyMap.put(skey, res); 
            idMap.put(res, skey); 
        } 
        key.setId(res); 
        return res; 
    } 
    //TODO: non-transactional 
    private Integer createKey(String key)  { 
        Integer k = loadKey(key); 
        if (k!=null){ 
            return k; 
        } 
        PreparedStatement st = null; 
        try { 
            st = con.prepareStatement(INSERT_KEY_QUERY); 
            st.setString(1, key); 
            st.execute(); 
        } catch (Exception e) { 
            LOG.log(Level.SEVERE, e.getMessage(), e); 
        } finally { 
            if (st != null) 
                try { 
                    st.close(); 
                } catch (SQLException e) {} 
        } 
        return loadKey(key); 
    } 
 
    private Integer loadKey(String key) { 
        PreparedStatement st = null; 
        try { 
            st = con.prepareStatement(FIND_KEY); 
            st.setString(1, key); 
            ResultSet rs = st.executeQuery(); 
            if (rs.next()){ 
                return rs.getInt(1); 
            } 
            return null; 
        } catch (Exception e) { 
            LOG.log(Level.SEVERE, e.getMessage(), e); 
        } finally { 
            if (st != null) 
                try { 
                    st.close(); 
                } catch (SQLException e) {} 
        } 
        return null; 
    } 
 
} 
 
interface JDBCWalker{ 
     public void accept(String key, Instant date, String status, double value); 
}