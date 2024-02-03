package com.maroontress.nugetscraper;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
    A parser to retrieve a list of NuGet packages that the specified
    organization has released and how many of each have been downloaded.
*/
public final class NugetScraper {

    /** Prevents the class from being instantiated. */
    private NugetScraper() {
    }

    /**
        Parses the HTML page in {@code https://www.nuget.org/profiles/...} and
        returns the new {@link Profile} object.

        @param content A string representing the HTML page.
        @return The new profile object.
    */
    public static Profile toProfile(String content) {
        var doc = Jsoup.parse(content);
        var packageList = doc.getElementsByTag("li")
                .stream()
                .filter(x -> x.hasClass("package"))
                .map(NugetScraper::toPackage)
                .collect(Collectors.toList());
        var maybeUrl = doc.getElementsByTag("li")
                .stream()
                .filter(x -> x.hasClass("next"))
                .map(NugetScraper::toNext)
                .findFirst();
        return Profile.of(packageList, maybeUrl);
    }

    private static String toNext(Element nextListItem) {
        return nextListItem.getElementsByTag("a")
                .stream()
                .map(a -> a.attr("href"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "anchor not found"));
    }

    private static Package toPackage(Element packageListItem) {
        /*
            <a class="package-title" href="/packages/.../">...</a>
        */
        var title = packageListItem.getElementsByTag("a")
                .stream()
                .filter(x -> x.hasClass("package-title"))
                .map(Element::text)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "title not found"));

        /*
            <ul class="package-list">
                <li class="package-warning-indicators">...</li>
                <li class="package-tfm-badges">...</li>
                <li>
                    <span class="icon-text">
                        <i class="ms-Icon ms-Icon--Download" aria-hidden="true"></i>
                        X,XXX total downloads
                    </span>
                </li>
        */
        var format = NumberFormat.getInstance(Locale.US);
        var totalDownloads = packageListItem.getElementsByTag("ul")
                .stream()
                .filter(x -> x.hasClass("package-list"))
                .flatMap(x -> x.getElementsByTag("li").stream())
                .filter(x -> !x.hasClass("package-warning-indicators")
                        && !x.hasClass("package-tfm-badges"))
                .flatMap(x -> Arrays.stream(x.text().split(" +")))
                .map(x -> {
                    try {
                        return format.parse(x).longValue();
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(
                                "invalid totalDownloads: " + x);
                    }
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "totalDownloads not found"));
        if (totalDownloads < 0) {
            throw new IllegalArgumentException(
                    "invalid totalDownloads: " + totalDownloads);
        }
        return Package.of(title, totalDownloads);
    }
}
