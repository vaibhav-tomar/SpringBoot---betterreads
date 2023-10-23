package io.vaibhav.betterreads.userbooks;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface UserBooksRepository extends CassandraRepository<UserBooks, UserBooksPrimaryKey>{

//	Slice<UserBooks> findAllById(String id, CassandraPageRequest cassandraPageRequest);

}
