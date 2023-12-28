package id.labs247.medan.mediaonline_crawler.services;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NewsScrappingService {

    private static final String KAFKA_TOPIC = "news-topic";
    private static final String KAFKA_SERVERS = "localhost:9092";

    String[] newsUrls = {
                "https://www.bisnis.com/",
                "https://indeks.kompas.com/",
                "https://www.liputan6.com/news",
                "https://mediaindonesia.com/",
                "https://www.tribunnews.com/news"
    };

    private Producer<String, String> kafkaProducer;
    
    public void NewsScrapingService() {
        // Configure Kafka producer properties
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KAFKA_SERVERS);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // Create Kafka producer
        kafkaProducer = new KafkaProducer<>(properties);
    }

    public void startScheduledTask() {
        // Create a scheduled executor service
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        // Schedule the scraping task to run every 1 hour
        executorService.scheduleAtFixedRate(this::scrapeAndSendToKafka, 0, 1, TimeUnit.HOURS);
    }

    private void scrapeAndSendToKafka() {
        try {

            for(String newsUrl : newsUrls) {
                Document document = Jsoup.connect(newsUrl).get();   
                Elements newsList;
                switch (newsUrl) {
                    case "https://indeks.kompas.com/":
                        System.out.println("Processing Kompas URL: " + newsUrl);
                        newsList = document.getElementsByClass("a.article_link");
                        for(Element news : newsList) {                        
                            System.out.println(news);
                            kafkaProducer.send(new ProducerRecord<>(KAFKA_TOPIC, news.text()));
                        }
                        break;
                    case "https://www.tribunnews.com/news":
                        // Action for Tribun URL
                        System.out.println("Processing Tribun URL: " + newsUrl);
                        newsList = document.select("h3 a");
                        for(Element news : newsList) {                        
                            System.out.println(news);
                            kafkaProducer.send(new ProducerRecord<>(KAFKA_TOPIC, news.text()));
                        }
                        break;
                    case "https://www.liputan6.com/news":
                        System.out.println("Processing Liputan6 URL: " + newsUrl);
                        newsList = document.select("article.articles--iridescent-list--text-item");
                        for(Element news : newsList) {                        
                            System.out.println(news);
                            kafkaProducer.send(new ProducerRecord<>(KAFKA_TOPIC, news.text()));
                        }
                        break;
                    default:
                        // Default action for other URLs
                        System.out.println("Processing default URL: " + newsUrl);
                        break;
                }

            }

            System.out.println("News scraped and sent to Kafka successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        // Close the Kafka producer when the service is stopped
        kafkaProducer.close();
    }
    
}
