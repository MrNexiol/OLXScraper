package testing;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class Main extends Application {

    List<HtmlElement> articles;
    List<CustomVBox> list = new LinkedList<>();
    ObservableList<CustomVBox> observableList;
    ListView<CustomVBox> listView;
    Button searchButton = new Button("Search");

    private class CustomVBox extends VBox {
        Text titleLabel = new Text();
        Label priceLabel = new Label();

        public CustomVBox(String title, String price){
            super();

            this.titleLabel.setText(title);
            this.priceLabel.setText(price);

            this.titleLabel.setFont(Font.font("Verdana", FontWeight.BOLD,12));

            this.getChildren().addAll(titleLabel,priceLabel);
        }
    }

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        BorderPane mainPane = new BorderPane();

        VBox leftPane = new VBox();
        Label searchLabel = new Label("Search");
        TextField searchInput = new TextField();
        leftPane.getChildren().addAll(searchLabel,searchInput,searchButton);

        searchButton.setOnAction(actionEvent -> {
            list = new LinkedList<>();
            String searchQuery = searchInput.getText();

            WebClient client = new WebClient();
            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(false);
            try {
                String searchUrl = "https://www.olx.pl/gorzow/q-" +
                        URLEncoder.encode(searchQuery, StandardCharsets.UTF_8) +
                        "/?search%5Border%5D=filter_float_price%3Aasc&search%5Bdist%5D=50";
                HtmlPage website = client.getPage(searchUrl);
                String pageCount = website.getFirstByXPath("string(//div[@class='pager rel clr']/span[@class='item fleft'][last()]/a/span)");

                int integerPage;
                try {
                    integerPage = Integer.parseInt(pageCount);
                }catch (Exception e){
                    integerPage = 1;
                }

                for (int i = 0; i < integerPage; i++) {
                    searchUrl += "&page=" + ++i;
                    HtmlPage page = client.getPage(searchUrl);
                    articles = page.getByXPath("//table[@id='offers_table']/tbody/tr[@class='wrap']");
                    for (HtmlElement element : articles){
                        String title = element.getFirstByXPath("string(descendant::img/@alt)");
                        String price = element.getFirstByXPath("string(descendant::p[@class='price']/strong)");
                        CustomVBox item = new CustomVBox(title,price);
                        list.add(item);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            observableList = FXCollections.observableList(list);
            listView.setItems(observableList);
        });

        listView = new ListView<>();
        observableList = FXCollections.observableList(list);

        listView.setItems(observableList);

        mainPane.setLeft(leftPane);
        mainPane.setCenter(listView);

        Scene scene = new Scene(mainPane,700,600);

        stage.setScene(scene);

        stage.show();
    }
}
