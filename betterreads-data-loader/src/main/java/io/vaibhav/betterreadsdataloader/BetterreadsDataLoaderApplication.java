package io.vaibhav.betterreadsdataloader;

import connection.DataStaxAstraProperties;
import io.vaibhav.betterreadsdataloader.author.Author;
import io.vaibhav.betterreadsdataloader.author.AuthorRepository;
import io.vaibhav.betterreadsdataloader.book.Book;
import io.vaibhav.betterreadsdataloader.book.BookRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BetterreadsDataLoaderApplication {

	@Autowired
	AuthorRepository authorRepository;

	@Autowired
	BookRepository bookRepository;

	@Value("${datadump.location.author}")
	private String authorDumpLocation;

	@Value("${datadump.location.works}")
	private String worksDumpLocation;

	public static void main(String[] args) {
		SpringApplication.run(BetterreadsDataLoaderApplication.class, args);
	}

	private void initAuthors() {
		Path path = Paths.get(authorDumpLocation);
		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(line -> {
				// read and parse the line
				String jsonString = line.substring(line.indexOf("{"));
				try {
					JSONObject jsonObject = new JSONObject(jsonString);

					// construct the author object
					Author author = new Author();
					author.setName(jsonObject.optString("name"));
					author.setPersonalName(jsonObject.optString("personal_name"));
					author.setId(jsonObject.optString("key").replace("/authors/", ""));

					// persist using repository
					System.out.println("saving author : " + author.getName() + ".....");
					authorRepository.save(author);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			});

		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}

	private void initWorks() {
		Path path = Paths.get(worksDumpLocation);
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(line -> {
				// read and parse the line
				String jsonString = line.substring(line.indexOf("{"));
				try {
					JSONObject jsonObject = new JSONObject(jsonString);

					// construct the book object
					Book book = new Book();
					book.setId(jsonObject.getString("key").replace("/works/", ""));
					book.setName(jsonObject.optString("title"));
					JSONObject descriptionObject = jsonObject.optJSONObject("description");
					if (descriptionObject != null) {
						book.setDescription(descriptionObject.optString("value"));
					}
					JSONObject publishedObject = jsonObject.optJSONObject("created");
					if (publishedObject != null) {
						String dateStr = publishedObject.getString("value");
						book.setPublishedDate(LocalDate.parse(dateStr,dateFormat));
					}

					JSONArray coversJsonArr = jsonObject.optJSONArray("covers");
					if (coversJsonArr != null) {
						List<String> coverIds = new ArrayList<>();
						for (int i = 0; i < coversJsonArr.length(); i++) {
							coverIds.add(coversJsonArr.getString(i));
						}
						book.setCoverIds(coverIds);
					}
					JSONArray authorsJsonArr = jsonObject.optJSONArray("authors");
					if (authorsJsonArr != null) {
						List<String> authorIds = new ArrayList<>();
						for (int i = 0; i < authorsJsonArr.length(); i++) {
							String authorId = authorsJsonArr.getJSONObject(i).getJSONObject("author").getString("key")
									.replace("/authors/", "");
							authorIds.add(authorId);
						}
						book.setAuthorIds(authorIds);

						List<String> authorNames = authorIds.stream().map(id -> authorRepository.findById(id))
								.map(optionalAuthor -> {
									if (!optionalAuthor.isPresent())
										return "Unknown author";
									return optionalAuthor.get().getName();
								}).collect(Collectors.toList());
						book.setAuthorNames(authorNames);
					}
					// persist using repository
					System.out.println("saving book : " + book.getName() + ".....");
					bookRepository.save(book);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException exc) {
			exc.printStackTrace();
		}

	}

	@PostConstruct
	public void start() {
//		initAuthors();
//		initWorks();
	}

	// giving spring data cassandra ability to connect securely with instance of
	// datastax
	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

}
