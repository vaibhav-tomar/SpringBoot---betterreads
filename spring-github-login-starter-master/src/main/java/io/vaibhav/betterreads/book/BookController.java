package io.vaibhav.betterreads.book;

import java.util.Optional;

import io.vaibhav.betterreads.userbooks.UserBooks;
import io.vaibhav.betterreads.userbooks.UserBooksPrimaryKey;
import io.vaibhav.betterreads.userbooks.UserBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BookController {

	private final String COVER_IMAGE_ROOT = "http://covers.openlibrary.org/b/id/";
	
	@Autowired
	BookRepository bookRepository;
	
	@Autowired
    UserBooksRepository userBooksRepository;
	
	@GetMapping(value = "/books/{bookId}")
	public String getBook(@PathVariable String bookId, Model model, @AuthenticationPrincipal OAuth2User principal) {
		Optional<Book> optionalBook = bookRepository.findById(bookId);
		if(optionalBook.isPresent()) {
			Book book = optionalBook.get();
			String coverImageUrl = "/images/no-image.png";
			if(book.getCoverIds()!=null && book.getCoverIds().size()>0) {
				coverImageUrl = COVER_IMAGE_ROOT + book.getCoverIds().get(0) + "-L.jpg";
			}
			model.addAttribute("coverImage",coverImageUrl);
			model.addAttribute("book",book);
			
			if(principal!=null && principal.getAttribute("id")!=null) {
				model.addAttribute("loginId",principal.getAttribute("login"));
				UserBooksPrimaryKey key = new UserBooksPrimaryKey();
				key.setBookId(bookId);
				key.setUserId(principal.getAttribute("login"));
				
				Optional<UserBooks> userBooks  = userBooksRepository.findById(key);
				if(userBooks.isPresent()) {
					model.addAttribute("userBooks", userBooks.get());
				}else {
					model.addAttribute("userBooks", new UserBooks());
				}
			}
			
			return "book";
		}
		return "book-not-found";
		
	}
}
