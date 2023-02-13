/**
    This package provides an HTML parser that reads an HTML page retrieved from
    NuGet.org and provides a list containing NuGet packages released by a
    specified organization.

    <p>The following code shows how to use
    {@link NugetScraper#toProfile(String)}:</p>
    <pre>
    ...
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
    ...</pre>

    @see com.maroontress.nugetscraper.NugetScraper
    @see com.maroontress.nugetscraper.Profile
*/
package com.maroontress.nugetscraper;
