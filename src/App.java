import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class App {
    public static void main(String[] args) throws Exception {

        String NameOfLanguageDependencyOnPage;
        System.setProperty("webdriver.chrome.driver","C:\\Users\\Laptop\\javaSelenium\\seleniumJava\\src\\drivers\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("https://boardgamegeek.com/");
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("#hompage-container > gg-home-game-explorer-row-hotness > gg-home-game-explorer-row > section > gg-card-scroll > div > div > gg-home-game-explorer-row-hotness-items > ul > li:nth-child(1) > div > div.media-card__body > h3 > a")).click();
        Thread.sleep(1000);
        String my_href = driver.getCurrentUrl();
        String[] result = my_href.split("/");
        int idIndex = result.length - 2;
        String gameId = result[idIndex];
        NameOfLanguageDependencyOnPage = driver.findElement(By.cssSelector("#mainbody > div.global-body-content-container.container-fluid > div > div.content.ng-isolate-scope > div:nth-child(2) > ng-include > div > div > ui-view > ui-view > div > overview-module > description-module > div > div.panel-body > div > div.game-description > div.row.col-xl-middle-border.fs-responsive-sm.ng-scope > div.col-sm-6.col-xl-5 > div > ul > li > div.feature-description > span > span")).getText();
        Thread.sleep(1000);
        driver.quit();
        String url = "https://boardgamegeek.com/xmlapi/boardgame/" + gameId;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        int responseCode = con.getResponseCode();
        System.out.println("responseCode: " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(response.toString())));
        NodeList polls = doc.getElementsByTagName("poll");

        Element finalPollNodeList;
        NodeList results;
        ArrayList<String> highetsVotesPollsNames = new ArrayList<String>();
        int highetsVotesPollCount = 0;
        String tieBrakers;
        String winnerString;
        if (polls != null && polls.getLength() > 0) {
            for (int j = 0; j < polls.getLength(); j++) {
                Element el = (org.w3c.dom.Element) polls.item(j);
                if (el.hasAttribute("name") && el.getAttribute("name").equals("language_dependence")) {
                    finalPollNodeList = el;
                    results = finalPollNodeList.getElementsByTagName("result");
                    for (int p=0; p < results.getLength(); p++) {
                        Element tempEl = (Element)results.item(p);
                        int voteCountValue = Integer.parseInt(tempEl.getAttribute("numvotes"));
                        String pollNameValue = tempEl.getAttribute("value");
                        if (highetsVotesPollCount == voteCountValue) {
                            highetsVotesPollsNames.add(pollNameValue);
                        }
                        
                        if (voteCountValue > highetsVotesPollCount) {
                            highetsVotesPollCount = voteCountValue;
                            highetsVotesPollsNames = new ArrayList<String>();
                            highetsVotesPollsNames.add(pollNameValue);
                        }

                    }
                    if(highetsVotesPollsNames.size() > 1) {

                        if (highetsVotesPollCount == 0) {
                            System.out.println("\n\nREPORT: \nThere has been no votes on this game, try hardcoding the url's gameId to 342942");
                        }
                        else {
                            tieBrakers = "\n\nREPORT: \nFollowing results are in tie braker for most votes with " + highetsVotesPollCount + " number of votes :\n";
                            for(int l =0; l < highetsVotesPollsNames.size(); l++) {
                                tieBrakers += highetsVotesPollsNames.get(l) + "\n";
                            }
                            System.out.println(tieBrakers);
                        }
                    }

                    if (highetsVotesPollsNames.size() == 1) {
                        winnerString = "REPORT: \n\nWinner result with the most number of votes is: " + highetsVotesPollsNames.get(0) + " with " + highetsVotesPollCount + " votes";
                        System.out.println(winnerString);
                    }
                    if(highetsVotesPollsNames.size() == 1) {
                        if (Objects.equals(NameOfLanguageDependencyOnPage,highetsVotesPollsNames.get(0) )) {
                            System.out.println("\n\nREPORT: \nValue of Language Dependency poll item on XML report and on Website are matching");
                        }
                    }
                    if(highetsVotesPollsNames.size() > 1) {
                        if (highetsVotesPollCount == 0) {
                            System.out.println("\n\nREPORT: \nThere has been no votes on this game, try hardcoding the url's gameId to 342942");
                        } else {
                            String tieBreakerString = "\n\nREPORT: \nThere has been a tie breaker for multiple options with the result of " + highetsVotesPollCount + " number of votes for the following items:\n";
                            for (int n = 0; n < highetsVotesPollsNames.size(); n++) {
                                tieBreakerString += highetsVotesPollsNames.get(n) + "\n";
                            }
                            System.out.println(tieBreakerString);
                            System.out.println("\n\nREPORT: \nThere has been a tie breaker in number of votes so I'm not sure what should be presented on the Website");
                        }
                    }
                }
            }
        }
    }
}

