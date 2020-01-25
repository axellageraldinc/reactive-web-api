package com.axell.reactive.web;

import com.axell.reactive.service.book.BookService;
import com.axell.reactive.servicedto.request.AddBookRequest;
import com.axell.reactive.servicedto.request.UpdateBookRequest;
import com.axell.reactive.servicedto.response.BookResponse;
import com.axell.reactive.webdto.request.AddBookWebRequest;
import com.axell.reactive.webdto.request.UpdateBookWebRequest;
import com.axell.reactive.webdto.response.BaseWebResponse;
import com.axell.reactive.webdto.response.BookWebResponse;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/books")
public class BookRestController {

    @Autowired
    private BookService bookService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    ) public Single<ResponseEntity<BaseWebResponse>> addBook(
        @RequestBody AddBookRequest addBookRequest) {
        return bookService.addBook(addBookRequest).subscribeOn(Schedulers.io()).map(
            s -> ResponseEntity.created(URI.create("/api/books/" + s))
                .body(BaseWebResponse.successNoData()));
    }

    /*private AddBookRequest toAddBookRequest(AddBookWebRequest addBookWebRequest) {
        AddBookRequest addBookRequest = new AddBookRequest();
        BeanUtils.copyProperties(addBookWebRequest, addBookRequest);
        return addBookRequest;
    }*/

    @PutMapping(
            value = "/{bookId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Single<ResponseEntity<BaseWebResponse>> updateBook(@PathVariable(value = "bookId") String bookId,
                                                              @RequestBody UpdateBookWebRequest updateBookWebRequest) {
        return bookService.updateBook(toUpdateBookRequest(bookId, updateBookWebRequest))
                .subscribeOn(Schedulers.io())
                .toSingle(() -> ResponseEntity.ok(BaseWebResponse.successNoData()));
    }

    private UpdateBookRequest toUpdateBookRequest(String bookId, UpdateBookWebRequest updateBookWebRequest) {
        UpdateBookRequest updateBookRequest = new UpdateBookRequest();
        BeanUtils.copyProperties(updateBookWebRequest, updateBookRequest);
        updateBookRequest.setId(bookId);
        return updateBookRequest;
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Single<ResponseEntity<BaseWebResponse<List<BookWebResponse>>>> getAllBooks(@RequestParam(value = "limit", defaultValue = "5") int limit,
                                                                                      @RequestParam(value = "page", defaultValue = "0") int page) {
        return bookService.getAllBooks(limit, page)
                .subscribeOn(Schedulers.io())
                .map(bookResponses -> ResponseEntity.ok(BaseWebResponse.successWithData(toBookWebResponseList(bookResponses))));
    }

    private List<BookWebResponse> toBookWebResponseList(List<BookResponse> bookResponseList) {
        return bookResponseList
                .stream()
                .map(this::toBookWebResponse)
                .collect(Collectors.toList());
    }

    private BookWebResponse toBookWebResponse(BookResponse bookResponse) {
        BookWebResponse bookWebResponse = new BookWebResponse();
        BeanUtils.copyProperties(bookResponse, bookWebResponse);
        return bookWebResponse;
    }

    @GetMapping(
            value = "/{bookId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Single<ResponseEntity<BaseWebResponse<BookWebResponse>>> getBookDetail(@PathVariable(value = "bookId") String bookId) {
        return bookService.getBookDetail(bookId)
                .subscribeOn(Schedulers.io())
                .map(bookResponse -> ResponseEntity.ok(BaseWebResponse.successWithData(toBookWebResponse(bookResponse))));
    }

    @DeleteMapping(
            value = "/{bookId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Single<ResponseEntity<BaseWebResponse>> deleteBook(@PathVariable(value = "bookId") String bookId) {
        return bookService.deleteBook(bookId)
                .subscribeOn(Schedulers.io())
                .toSingle(() -> ResponseEntity.ok(BaseWebResponse.successNoData()));
    }

}
