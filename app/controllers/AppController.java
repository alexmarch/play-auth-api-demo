package controllers;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import play.mvc.*;
import com.fasterxml.jackson.databind.JsonNode;
import play.db.*;
import play.Logger;
import play.libs.Json;
import play.libs.Json.*;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
import java.util.HashMap;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class AppController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    private Connection con;
    private Logger log;
    private final Database db;

    @Inject
    AppController(Database db) {
        this.db = db;
        this.con = this.db.getConnection();
    }

    public Result index() {
        return ok(views.html.index.render());
    }

    public Result signin() {
        HashMap<String, String> res = new HashMap<String, String>();
        try {
            
            JsonNode json = request().body().asJson();
            PreparedStatement ps = this.con.prepareStatement("SELECT * FROM users WHERE email = ?");
             
            if (!json.isNull() && !json.get("email").isNull()) {
                ps.setString(1, json.get("email").toString());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String pwdHash = rs.getString("password");
                    this.log.debug("GOOD PASS={}", pwdHash);
                    if (BCrypt.checkpw(json.get("password").toString(), pwdHash)) {
                        res.put("status", "OK");
                        res.put("email", rs.getString("email"));
                        res.put("id", rs.getString("id"));
                        res.put("firstname", rs.getString("firstname"));
                        res.put("lastname", rs.getString("lastname"));
                        
                    } else {
                        res.put("status", "ERROR");
                    }
                }
            }
            
        } catch(SQLException e) {
        }
        return ok(Json.toJson(res)).withHeader("Access-Control-Allow-Origin", "*");
    }

    /**
     * SignUp method
     */
    public Result signup() {
        try {
            JsonNode json = request().body().asJson();
            PreparedStatement ps = this.con.prepareStatement("INSERT INTO users (firstname, lastname, email, password, company, country, timezone, currency, business)" + 
                " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            if (!json.isNull()) {
                ps.setString(1, json.get("firstname").toString());
                ps.setString(2, json.get("lastname").toString());
                ps.setString(3, json.get("email").toString());
                ps.setString(4, BCrypt.hashpw(json.get("password").toString(), BCrypt.gensalt()));
                ps.setString(5, json.get("company").toString());
                ps.setString(6, json.get("country").toString());
                ps.setString(7, json.get("timezone").toString());
                ps.setString(8, json.get("currency").toString());
                ps.setInt(9, json.get("business").asInt());
                
                ps.executeUpdate();

                HashMap<String, String> res = new HashMap<String, String>();
                res.put("status", "OK");
                return ok(Json.toJson(res)).withHeader("Access-Control-Allow-Origin", "*");
            }
             
        } catch (SQLException e) { 
            this.log.error(e.toString()); 
        }
        return ok().withHeader("Access-Control-Allow-Origin", "*");
    }

    public Result preflight(String all) {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        response().setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept");
        return ok();
    }

}
