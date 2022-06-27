package com.balashenka.cuddly.service.impl;

import com.balashenka.cuddly.service.InstagramService;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.media.timeline.*;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.github.instagram4j.instagram4j.utils.IGUtils;
import com.balashenka.cuddly.config.ApplicationConfig;
import com.balashenka.cuddly.entity.PostInformation;
import com.balashenka.cuddly.entity.SerializableCookieJar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DefaultInstagramService implements InstagramService {
    private static final String WORKING_DIRECTORY_PATH = System.getProperty("user.dir") + "/src/main/resources/";
    private static final String INSTAGRAM_CLIENT_SER_NAME = "ig_client.ser";
    private static final String COOKIE_SER_NAME = "cookie.ser";

    private final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(log::debug).setLevel(HttpLoggingInterceptor.Level.NONE);

    private final ApplicationConfig applicationConfig;
    private final IGClient client;

    @Autowired
    public DefaultInstagramService(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;

        client = login();
    }

    @Override
    public CompletableFuture<List<PostInformation>> getUserLastPosts(int amount) {
        return getUserPosts(amount);
    }

    private CompletableFuture<List<PostInformation>> getUserPosts(int amount) {
        return client
                .actions()
                .users()
                .findByUsername(applicationConfig.getInstagramAccount())
                .thenApply(UserAction::getUser)
                .thenApply(user -> client.sendRequest(new FeedUserRequest(user.getPk())).join())
                .thenApply(response -> getPostsInformation(response.getItems(), amount));
    }

    private IGClient login() {
        IGClient instagramClient = null;

        try {
            var clientFile = new File(WORKING_DIRECTORY_PATH + INSTAGRAM_CLIENT_SER_NAME);
            var cookieFile = new File(WORKING_DIRECTORY_PATH + COOKIE_SER_NAME);

            if (!clientFile.exists() || !cookieFile.exists()) {
                instagramClient = serializeLogin();
            } else {
                instagramClient = getClientFromSerialize();
            }
        } catch (IOException | ClassNotFoundException e) {
            log.warn(e.getMessage());
        }

        return instagramClient;
    }

    private IGClient getClientFromSerialize() throws ClassNotFoundException, IOException {
        var fileInputStream = new FileInputStream(WORKING_DIRECTORY_PATH + INSTAGRAM_CLIENT_SER_NAME);

        var client = IGClient.from(fileInputStream,
                formTestHttpClient(deserialize(new File(WORKING_DIRECTORY_PATH + COOKIE_SER_NAME))));

        fileInputStream.close();

        return client;
    }

    private IGClient serializeLogin() throws IOException {
        var serializableCookieJar = new SerializableCookieJar();

        var client = new IGClient.Builder()
                .username(applicationConfig.getInstagramUsername())
                .password(applicationConfig.getInstagramPassword())
                .client(formTestHttpClient(serializableCookieJar))
                .onLogin((instagramClient, loginResponse) -> log.info("Login response status: {}", loginResponse.getStatus()))
                .login();

        serialize(client, new File(WORKING_DIRECTORY_PATH + INSTAGRAM_CLIENT_SER_NAME));
        serialize(serializableCookieJar, new File(WORKING_DIRECTORY_PATH + COOKIE_SER_NAME));

        return client;
    }

    private SerializableCookieJar deserialize(File file) throws IOException, ClassNotFoundException {
        var fileInputStream = new FileInputStream(file);
        var objectInputStream = new ObjectInputStream(fileInputStream);

        var serializableCookieJar = (SerializableCookieJar) objectInputStream.readObject();

        fileInputStream.close();
        objectInputStream.close();

        return serializableCookieJar;
    }

    private void serialize(Object object, File file) throws IOException {
        var fileOutputStream = new FileOutputStream(file);
        var outputStream = new ObjectOutputStream(fileOutputStream);

        outputStream.writeObject(object);
        outputStream.close();
        fileOutputStream.close();
    }

    private OkHttpClient formTestHttpClient(SerializableCookieJar jar) {
        return IGUtils.defaultHttpClientBuilder().cookieJar(jar)
                .addInterceptor(loggingInterceptor).build();
    }

    private List<PostInformation> getPostsInformation(List<TimelineMedia> timelineMedia, long count) {
        return timelineMedia.stream()
                .limit(count)
                .map(post -> PostInformation
                        .builder()
                        .urls(getPictureUrls(post))
                        .caption(post.getCaption().getText())
                        .build())
                .collect(Collectors.toList());
    }

    private Map<String, Boolean> getPictureUrls(TimelineMedia post) {
        Map<String, Boolean> urls = new HashMap<>();

        if (post instanceof TimelineImageMedia) {
            urls = Map.of(((TimelineImageMedia) post).getImage_versions2().getCandidates().get(0).getUrl(), false);
        } else if (post instanceof TimelineVideoMedia) {
            urls = Map.of(((TimelineVideoMedia) post).getVideo_versions().get(0).getUrl(), true);
        } else if (post instanceof TimelineCarouselMedia) {
            urls = ((TimelineCarouselMedia) post).getCarousel_media()
                    .stream()
                    .map(media -> {
                        Tuple tuple = null;

                        if (media instanceof ImageCaraouselItem) {
                            tuple = new Tuple(((ImageCaraouselItem) media).getImage_versions2().getCandidates().get(0).getUrl(), false);
                        } else if (media instanceof VideoCaraouselItem) {
                            tuple = new Tuple(((VideoCaraouselItem) media).getVideo_versions().get(0).getUrl(), true);
                        }

                        return tuple;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(Tuple::getUrl, Tuple::getIsVideo));
        }

        return urls;
    }

    @Getter
    @AllArgsConstructor
    private static class Tuple {
        private final String url;
        private final Boolean isVideo;
    }
}
