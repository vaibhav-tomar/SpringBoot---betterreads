package io.vaibhav.betterreads.userbooks;

import java.time.LocalDate;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;

@Table(value="book_by_user_and_bookid")
public class UserBooks {

	@PrimaryKey
	private UserBooksPrimaryKey key;

	@Column("reading_status")
	@CassandraType(type=Name.TEXT)
	private String readingStatus;
	
	@Column("started_date")
	@CassandraType(type = Name.DATE)
	private LocalDate startedDate;
	
	@Column("completed_date")
	@CassandraType(type = Name.DATE)
	private LocalDate completedDate;
	
	@Column("rating")
	@CassandraType(type=Name.INT)
	private int rating;

	public UserBooksPrimaryKey getKey() {
		return key;
	}

	public void setKey(UserBooksPrimaryKey key) {
		this.key = key;
	}

	public String getReadingStatus() {
		return readingStatus;
	}

	public void setReadingStatus(String readingStatus) {
		this.readingStatus = readingStatus;
	}

	public LocalDate getStartedDate() {
		return startedDate;
	}

	public void setStartedDate(LocalDate startedDate) {
		this.startedDate = startedDate;
	}

	public LocalDate getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(LocalDate completedDate) {
		this.completedDate = completedDate;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
	
}
