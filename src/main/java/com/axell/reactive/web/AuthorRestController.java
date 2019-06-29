package com.axell.reactive.web;

import com.axell.reactive.service.author.AuthorService;
import com.axell.reactive.servicedto.request.AddAuthorRequest;
import com.axell.reactive.webdto.request.AddAuthorWebRequest;
import com.axell.reactive.webdto.response.BaseWebResponse;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/authors")
public class AuthorRestController {

    @Autowired
    private AuthorService authorService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Single<ResponseEntity<BaseWebResponse>> addAuthor(@RequestBody AddAuthorWebRequest addAuthorWebRequest) {
        return authorService.addAuthor(toAddAuthorRequest(addAuthorWebRequest))
                .subscribeOn(Schedulers.io())
                .map(s -> ResponseEntity
                        .created(URI.create("/api/authors/" + s))
                        .body(BaseWebResponse.successNoData()));
    }

    private AddAuthorRequest toAddAuthorRequest(AddAuthorWebRequest addAuthorWebRequest) {
        AddAuthorRequest addAuthorRequest = new AddAuthorRequest();
        BeanUtils.copyProperties(addAuthorWebRequest, addAuthorRequest);
        return addAuthorRequest;
    }
}
