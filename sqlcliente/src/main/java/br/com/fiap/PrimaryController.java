package br.com.fiap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PrimaryController {

    @FXML TextField textFieldUsuario;
    @FXML PasswordField passwordFieldSenha;
    @FXML TextField textFieldUrl;
    @FXML TextArea textAreaSQL;
    @FXML Label status;
    @FXML ListView<String> historico;
    @FXML TableView<ArrayList<String>> tabela;

    public void executar(){
        //pegar user, pass, url
        String usuario = textFieldUsuario.getText();
        String senha = passwordFieldSenha.getText();
        String url = textFieldUrl.getText();
        
        //conectar no BD
        try {
            Connection con = DriverManager.getConnection(url, usuario, senha);
            //executar o sql
            String sql = sanitizar( textAreaSQL.getText() );
            var comando = con.prepareStatement(sql);
            var resultado = comando.executeQuery();

            // se for select tem que carregar a tabela
            if (sql.toUpperCase().startsWith("SELECT")) carregarDadosNaTabela(resultado);

            mostrarMensagem("Comando executado: " + sql);
            historico.getItems().add(sql);

            //fechar a conexao
            con.close();
        } catch (SQLException e) {
            mostrarMensagem("Erro de SQL. " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void carregarDadosNaTabela(ResultSet resultado) throws SQLException {
        int columnCount = resultado.getMetaData().getColumnCount();
        tabela.getColumns().removeAll(tabela.getColumns());

        for (int i = 1; i <= columnCount; i++){
            var columnName = resultado.getMetaData().getColumnLabel(i);
            TableColumn<ArrayList<String>, String> tableColumn = new TableColumn<>(columnName);
            tableColumn.setCellValueFactory(new CallbackImp(i-1));
            tabela.getColumns().add(tableColumn);
        }

        tabela.getItems().clear();
        while(resultado.next()){
            var lista = new ArrayList<String>();
            for (int i = 1 ; i <= columnCount; i++){
                lista.add(resultado.getString(i));
            }
            tabela.getItems().add(lista);
        }
    }

    private String sanitizar(String sql){
        return sql.replaceAll(";", "").replaceAll("\"", "'");
    }

    private void mostrarMensagem(String string) {
        status.setText(string);
    }

    public void carregarHistorico(){
        String comando = historico.getSelectionModel().getSelectedItem();
        textAreaSQL.setText(comando);
    }


}
