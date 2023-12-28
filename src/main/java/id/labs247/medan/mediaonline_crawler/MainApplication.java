package id.labs247.medan.mediaonline_crawler;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import id.labs247.medan.mediaonline_crawler.services.NewsScrappingService;

@SpringBootApplication
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
		// Create an instance of the NewsScrapingService
		NewsScrappingService newsService = new NewsScrappingService();

		// Start the scheduled task in the service
		newsService.startScheduledTask();

		// Keep the main thread alive or do other tasks as needed
		// For this example, let's keep the main thread sleeping
		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Shutdown the service gracefully when the application is stopped
		newsService.shutdown();

		// String url = "https://indeks.kompas.com/";
		// Document document;
		// try {
		// 	document = Jsoup.connect(url).get();
		// 	System.out.println("Processing Kompas URL: " + url);
		// 	Elements newsList = document.getElementsByClass("a.article_link");
		// 	for(Element news : newsList) {                        
		// 		System.out.println(news);
		// 	}
		// } catch (IOException e) {
		// 	// TODO Auto-generated catch block
		// 	e.printStackTrace();
		// }


	}

}
