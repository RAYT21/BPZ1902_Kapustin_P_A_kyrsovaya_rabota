import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

class CoinParser {

    private static final String URL = "https://coinmarketcap.com/";

    public static Document getPager() {
        try {

            final Document document = Jsoup.connect(URL).get();
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Elements getCoinLinkImages(Document page) {
        return page.select("div.h7vnx2-1.bFzXgl").select("img[src$=.png]");
    }

    public static Elements getCoinWeeklyGraphic(Document page) {
        return  page.select("div.h7vnx2-1.bFzXgl").select("img[src$=.svg]");

    }

    public static Elements getCoinName(Document page) {
        return page.select("div.h7vnx2-1.bFzXgl").select("p[class=sc-1eb5slv-0 iworPT]");
    }

    public static Elements getCoinPrice(Document page) {
        return page.select("div.h7vnx2-1.bFzXgl").select("div[class$=sc-131di3y-0 cLgOOr]").select("span");
    }

}
