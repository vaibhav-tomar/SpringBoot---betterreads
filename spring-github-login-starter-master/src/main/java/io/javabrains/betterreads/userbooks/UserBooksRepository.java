package io.javabrains.betterreads.userbooks;

import java.awt.print.Pageable;

import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Slice;

public interface UserBooksRepository extends CassandraRepository<UserBooks, UserBooksPrimaryKey>{

//	Slice<UserBooks> findAllById(String id, CassandraPageRequest cassandraPageRequest);

}
