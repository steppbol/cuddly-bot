package com.balashenka.cuddly.service;

import com.balashenka.cuddly.entity.PostInformation;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface InstagramService {
    CompletableFuture<List<PostInformation>> getUserLastPosts(int amount);
}
