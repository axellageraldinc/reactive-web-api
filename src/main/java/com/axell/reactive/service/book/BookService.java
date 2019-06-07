package com.axell.reactive.service.book;

import com.axell.reactive.servicedto.request.AddBookRequest;
import com.axell.reactive.servicedto.request.UpdateBookRequest;
import com.axell.reactive.servicedto.response.BookResponse;
import rx.Completable;
import rx.Single;

import java.util.List;

public interface BookService {
    Single<String> addBook(AddBookRequest addBookRequest);

    Completable updateBook(UpdateBookRequest updateBookRequest);

    Single<List<BookResponse>> getAllBooks(int limit, int page);

    Single<BookResponse> getBookDetail(String id);

    Completable deleteBook(String id);
}
