package com.balashenka.cuddly.entity;

import lombok.AllArgsConstructor;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SerializableCookieJar implements CookieJar, Serializable {
    private static final long serialVersionUID = -837498359387593793L;

    private final Map<String, List<SerializableCookie>> map = new HashMap<>();

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
        return map.getOrDefault(url.host(), new ArrayList<>()).stream()
                .map(c -> c.cookie)
                .collect(Collectors.toList());
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
        var serializableCookies = cookies
                .stream()
                .map(SerializableCookie::new)
                .collect(Collectors.toList());

        if (map.putIfAbsent(url.host(), serializableCookies) != null) {
            map.get(url.host()).addAll(serializableCookies);
        }
    }

    @AllArgsConstructor
    public static class SerializableCookie implements Serializable {
        private static final long serialVersionUID = -8594045714036645534L;
        private static final long NON_VALID_EXPIRES_AT = -1L;
        private transient Cookie cookie;

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(cookie.name());
            objectOutputStream.writeObject(cookie.value());
            objectOutputStream.writeLong(cookie.persistent() ? cookie.expiresAt() : NON_VALID_EXPIRES_AT);
            objectOutputStream.writeObject(cookie.domain());
            objectOutputStream.writeObject(cookie.path());
            objectOutputStream.writeBoolean(cookie.secure());
            objectOutputStream.writeBoolean(cookie.httpOnly());
            objectOutputStream.writeBoolean(cookie.hostOnly());
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            var builder = new Cookie.Builder()
                    .name((String) objectInputStream.readObject())
                    .value((String) objectInputStream.readObject());

            var expiresAt = objectInputStream.readLong();

            if (expiresAt != NON_VALID_EXPIRES_AT) {
                builder.expiresAt(expiresAt);
            }

            var domain = (String) objectInputStream.readObject();

            builder.domain(domain);
            builder.path((String) objectInputStream.readObject());

            if (objectInputStream.readBoolean())
                builder.secure();

            if (objectInputStream.readBoolean())
                builder.httpOnly();

            if (objectInputStream.readBoolean())
                builder.hostOnlyDomain(domain);

            cookie = builder.build();
        }
    }
}
