package ru.law.project;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.law.project.model.TreeInfoDB;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/LawProject",
                    "postgres",
                    "dan_rud1");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT ord FROM tree WHERE name = ?");
            preparedStatement.setString(1, "Вино");
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            TreeInfoDB treeInfoDB = new TreeInfoDB(connection, "Вино", resultSet.getInt("ord"));
            treeInfoDB.sendInTable(connection,resultSet.getInt("ord"));
            System.out.println();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
}
