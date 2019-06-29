package com.axell.reactive.service.author;

import com.axell.reactive.servicedto.request.AddAuthorRequest;
import io.reactivex.Single;

public interface AuthorService {
    Single<String> addAuthor(AddAuthorRequest addAuthorRequest);
}
