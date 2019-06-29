package com.axell.reactive.service.author;

import com.axell.reactive.entity.Author;
import com.axell.reactive.repository.AuthorRepository;
import com.axell.reactive.servicedto.request.AddAuthorRequest;
import io.reactivex.Single;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public Single<String> addAuthor(AddAuthorRequest addAuthorRequest) {
        return addAuthorToRepository(addAuthorRequest);
    }

    private Single<String> addAuthorToRepository(AddAuthorRequest addAuthorRequest) {
        return Single.create(singleSubscriber -> {
            String addedAuthorId = authorRepository.save(toAuthor(addAuthorRequest)).getId();
            singleSubscriber.onSuccess(addedAuthorId);
        });
    }

    private Author toAuthor(AddAuthorRequest addAuthorRequest) {
        Author author = new Author();
        BeanUtils.copyProperties(addAuthorRequest, author);
        author.setId(UUID.randomUUID().toString());
        return author;
    }
}
