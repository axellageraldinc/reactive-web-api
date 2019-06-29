package com.axell.reactive.service.book;

import com.axell.reactive.entity.Author;
import com.axell.reactive.entity.Book;
import com.axell.reactive.repository.AuthorRepository;
import com.axell.reactive.repository.BookRepository;
import com.axell.reactive.servicedto.request.AddBookRequest;
import com.axell.reactive.servicedto.request.UpdateBookRequest;
import com.axell.reactive.servicedto.response.BookResponse;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import rx.observers.AssertableSubscriber;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @InjectMocks
    private BookServiceImpl bookService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void AddBook_Success_ReturnSingleOfAddedBookId() {
        when(authorRepository.findById(anyString()))
                .thenReturn(Optional.of(new Author("1", "1")));
        when(bookRepository.save(any(Book.class)))
                .thenReturn(new Book("1", "1", new Author()));

        bookService.addBook(new AddBookRequest("1", "1"))
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue("1")
                .awaitTerminalEvent();

        InOrder inOrder = inOrder(authorRepository, bookRepository);
        inOrder.verify(authorRepository, times(1)).findById(anyString());
        inOrder.verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    public void AddBook_Failed_AuthorIdNotFound_ThrowEntityNotFoundException() {
        when(authorRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        bookService.addBook(new AddBookRequest("1", "1"))
                .test()
                .assertNotComplete()
                .assertError(EntityNotFoundException.class)
                .awaitTerminalEvent();

        InOrder inOrder = inOrder(authorRepository, bookRepository);
        inOrder.verify(authorRepository, times(1)).findById(anyString());
        inOrder.verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void UpdateBook_Success_ReturnCompletable() {
        when(bookRepository.findById(anyString()))
                .thenReturn(Optional.of(new Book("1", "1", new Author())));
        when(bookRepository.save(any(Book.class)))
                .thenReturn(new Book("1", "1", new Author()));

        bookService.updateBook(new UpdateBookRequest("1", "1"))
                .test()
                .assertComplete()
                .assertNoErrors()
                .awaitTerminalEvent();

        InOrder inOrder = inOrder(bookRepository);
        inOrder.verify(bookRepository, times(1)).findById(anyString());
        inOrder.verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    public void UpdateBook_Failed_IdNotFound_ThrowEntityNotFoundException() {
        when(bookRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        bookService.updateBook(new UpdateBookRequest("1", "1"))
                .test()
                .assertNotComplete()
                .assertError(EntityNotFoundException.class)
                .awaitTerminalEvent();

        InOrder inOrder = inOrder(bookRepository);
        inOrder.verify(bookRepository, times(1)).findById(anyString());
        inOrder.verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void GetAllBooks_Success_ReturnSingleOfBookResponseList() {
        Book book1 = new Book("1", "1", new Author());
        Book book2 = new Book("2", "2", new Author());

        when(bookRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        Arrays.asList(book1, book2)));

        TestObserver<List<BookResponse>> testObserver = bookService.getAllBooks(1, 1).test();

        testObserver.awaitTerminalEvent();

        testObserver.assertValue(bookResponses -> bookResponses.get(0).getId().equals("1") && bookResponses.get(1).getId().equals("2"));

        verify(bookRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    public void GetBookDetail_Success_ReturnSingleOfBookResponse() {
        Book book1 = new Book("1", "1", new Author("1", "1"));

        when(bookRepository.findById(anyString()))
                .thenReturn(Optional.of(book1));

        TestObserver<BookResponse> testObserver = bookService.getBookDetail("1").test();

        testObserver.awaitTerminalEvent();

        testObserver.assertValue(bookResponse -> bookResponse.getId().equals("1"));

        verify(bookRepository, times(1)).findById(anyString());
    }

    @Test
    public void GetBookDetail_Failed_IdNotFound_ThrowEntityNotFoundException() {
        when(bookRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        bookService.getBookDetail("1")
                .test()
                .assertNotComplete()
                .assertError(EntityNotFoundException.class)
                .awaitTerminalEvent();

        verify(bookRepository, times(1)).findById(anyString());
    }

    @Test
    public void DeleteBook_Success_ReturnCompletable() {
        when(bookRepository.findById(anyString()))
                .thenReturn(Optional.of(new Book("1", "1", new Author())));
        doNothing().when(bookRepository).delete(any(Book.class));

        bookService.deleteBook("1")
                .test()
                .assertComplete()
                .assertNoErrors()
                .awaitTerminalEvent();

        InOrder inOrder = inOrder(bookRepository);
        inOrder.verify(bookRepository, times(1)).findById(anyString());
        inOrder.verify(bookRepository, times(1)).delete(any(Book.class));
    }

    @Test
    public void DeleteBook_Failed_IdNotFound_ThrowEntityNotFoundException() {
        when(bookRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        bookService.deleteBook("1")
                .test()
                .assertNotComplete()
                .assertError(EntityNotFoundException.class)
                .awaitTerminalEvent();

        InOrder inOrder = inOrder(bookRepository);
        inOrder.verify(bookRepository, times(1)).findById(anyString());
        inOrder.verify(bookRepository, never()).delete(any(Book.class));
    }
}