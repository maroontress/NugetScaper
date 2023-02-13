package com.example;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import com.maroontress.nugetscraper.NugetScraper;

public final class Demo {

    public static void run(String organizationName)
            throws IOException, InterruptedException {
        var urlBase = "https://www.nuget.org";
        var id = URLEncoder.encode(organizationName, StandardCharsets.UTF_8);
        var request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + "/profiles/" + id + "/"))
                .build();
        var client = HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var profile = NugetScraper.toProfile(response.body());

        for (var i : profile.packageList()) {
            System.out.println(i.title() + ":" + i.totalDownloads());
        }
        var maybePath = profile.nextPageUrl();
        if (maybePath.isPresent()) {
            var path = maybePath.get();
            if (!path.startsWith("/")) {
                throw new IllegalStateException("unexpected URL: " + path);
            }
            var nextPageUrl = URI.create(urlBase + path);
            System.out.println("The next page URL: " + nextPageUrl);
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("usage: java com.example.Demo ID");
            return;
        }
        var organizationName = args[0];
        try {
            run(organizationName);
        } catch (IOException | InterruptedException e) {
            System.out.println("failed (ignored)");
        }
    }
}
