import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        String url = "https://www.lamoda.ru/";
        String urlArea = "&submit=y&gender_section=women";
        String urlSearch = "catalogsearch/result/?q=";
        String urlPage = "&page=";

        new CreateGUI();
        CreateGUI.button.addActionListener(actionEvent -> {

            String currentUrl = url + urlSearch + CreateGUI.search.getText() + urlArea;
            Document doc = getDocument(currentUrl);
            String countGoods = doc.getElementsByClass("d-catalog-header__product-counter").get(0).text();
            int lastPage = pageNumber(countGoods);
            StringBuilder content = new StringBuilder();
            content.append(pageContent(doc));
            for (int i = 2; i <= lastPage; i++) {
                String pageUrl = url + urlSearch + CreateGUI.search.getText() + urlArea + urlPage + i;
                Document pageDoc = getDocument(pageUrl);
                content.append(pageContent(pageDoc));
            }
            content.append("\n\n").append(countGoods).append("\n").append("Всего страниц: ").append(lastPage);
            CreateGUI.addContent(content.toString());
        });
    }

    public static Document getDocument(String url) {
        Document doc = new Document("");

        try {
            Connection connection = Jsoup.connect(url);
            connection.userAgent("Mozilla");
            connection.timeout(5000);
            connection.cookie("cookiename", "val234");
            connection.cookie("cookiename", "val234");
            connection.referrer("http://google.com");
            connection.header("headersecurity", "xyz123");
            doc = connection.get();
            //Jsoup.connect(currentUrl).get();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static Integer pageNumber(String info) {
        String[] pages = info.split(" ");
        return Integer.parseInt(pages[0]) / 60 + 1;
    }

    public static String pageContent(Document doc) {
        Elements items = doc.getElementsByClass("x-product-card__card");
        StringBuilder content = new StringBuilder();
        for (Element item : items) {
            //String photo = item.select("img").attr("abs:src");
            String price;
            Elements priceElement = item.getElementsByClass("x-product-card-description__price-new");
            Elements priceElements = item.getElementsByClass("x-product-card-description__price-single");
            if (priceElement.size() == 0) {
                price = priceElements.get(0).text();
            } else {
                price = priceElement.get(0).text();
            }
            String name = item.getElementsByClass("x-product-card-description__product-name").get(0).text();
            String manufacturer = item.getElementsByClass("x-product-card-description__brand-name").get(0).text();
            content.append(name).append("     ").append(manufacturer).append("     ").append(price).append("     ")/*.append(photo)*/.append("\n");
        }
        return content.toString();
    }
}

class CreateGUI {
    static JButton button;
    static JTextArea result;
    static JTextField search;

    public CreateGUI() {
        JFrame frame = new JFrame("Парсинг Lamoda");
        JPanel panel = new JPanel();
        JPanel controlPanel = new JPanel();
        JPanel resultPanel = new JPanel();
        JLabel searchLabel = new JLabel("Введите строку для поиска: ");
        search = new JTextField(20);
        button = new JButton("Искать");
        result = new JTextArea();
        int width = 800, height = 600;
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        search.setMaximumSize(new Dimension(290, 25));
        frame.setBounds(dim.width / 2 - width / 2, dim.height / 2 - height / 2, width, height);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.add(searchLabel);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(search);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(button);
        final JScrollPane scrollPane = new JScrollPane(result);
        scrollPane.setPreferredSize(new Dimension(width - 30, height - 90));
        resultPanel.add(scrollPane);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(controlPanel);
        panel.add(resultPanel);
        frame.add(panel);
        frame.setVisible(true);
    }

    public static void addContent(String s) {
        result.setText(s);
    }


}