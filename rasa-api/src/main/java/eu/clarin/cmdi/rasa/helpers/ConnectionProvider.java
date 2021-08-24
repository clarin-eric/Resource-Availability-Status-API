package eu.clarin.cmdi.rasa.helpers;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Twan Goosen <twan@clarin.eu>
 */
public interface ConnectionProvider {

    Connection getConnection() throws SQLException;
}
