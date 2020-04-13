package testing;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class Main extends Application {

    private class CustomVBox extends VBox {
        Label titleLabel = new Label();
        Label priceLabel = new Label();

        public CustomVBox(String title, String price){
            super();

            this.titleLabel.setText(title);
            this.priceLabel.setText(price);

            this.getChildren().addAll(titleLabel,priceLabel);
        }
    }

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        String searchQuery = "Akwarium";
        List<HtmlElement> articles;
        List<CustomVBox> list = new LinkedList<>();

        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        try {
            String searchUrl = "https://www.olx.pl/gorzow/q-" +
                    URLEncoder.encode(searchQuery, StandardCharsets.UTF_8) +
                    "/?search%5Border%5D=filter_float_price%3Aasc";
            HtmlPage page = client.getPage(searchUrl);
            articles = page.getByXPath("//table[@id='offers_table']/tbody/tr[@class='wrap']");
            if (articles.isEmpty()) {
                System.out.println("No articles");
            } else {
                for (HtmlElement element : articles){
                    System.out.println(element.asXml());
                    String title = element.getFirstByXPath("string(descendant::img/@alt)");
                    String price = element.getFirstByXPath("string(descendant::p[@class='price']/strong)");
                    CustomVBox item = new CustomVBox(title,price);
                    list.add(item);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        ListView<CustomVBox> listView = new ListView<>();
        ObservableList<CustomVBox> observableList = FXCollections.observableList(list);

        listView.setItems(observableList);

        Scene scene = new Scene(listView, 500, 500);

        stage.setScene(scene);

        stage.show();
    }
}
