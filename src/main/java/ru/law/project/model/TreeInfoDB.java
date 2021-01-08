package ru.law.project.model;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreeInfoDB {
    private NodeInfo root;

    public class NodeInfo {
        String header;
        NodeInfo parent;
        List<NodeInfo> desc;

        NodeInfo(Connection connection, String header, int ord, boolean isLeaf, NodeInfo parent) {
            this.header = header;
            this.parent = parent;
            this.desc = new ArrayList<>();
            if (!isLeaf) {
                try (PreparedStatement preparedStatement = connection
                        .prepareStatement("SELECT name,ord, isleaf FROM allchildsofparentbyonedepth(?)")) {

                    preparedStatement.setInt(1, ord);

                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        desc.add(new NodeInfo(connection,
                                resultSet.getString("name"),
                                resultSet.getInt("ord"),
                                resultSet.getBoolean("isleaf"),
                                this));
                    }
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }

    }

    public TreeInfoDB(Connection connection, String header, int ord) {
        root = new NodeInfo(connection, header, ord, false, null);

    }

    public void sendInTable(Connection connection, int ord) {
        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("CALL sp_del (?, ?)");
            callableStatement.setInt(1, ord);
            callableStatement.setInt(2, 1);
            callableStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        send(root, ord, connection);
    }

    private void send(NodeInfo node, int ord, Connection connection) {
        /// запрос в sql на вставку
        if (root != node) {
            PreparedStatement callableStatement = null;
            try {
                callableStatement = connection.prepareCall("CALL sp_add(?,?,?)"); //sp_add(1,2,'text')
                callableStatement.setInt(1, ord-1);
                callableStatement.setInt(2, 2);
                callableStatement.setString(3, node.header);
                callableStatement.executeUpdate();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }

        for (NodeInfo el : node.desc) {
            send(el, ord + 1, connection);
        }

    }
}
