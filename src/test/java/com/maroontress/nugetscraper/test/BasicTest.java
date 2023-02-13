package com.maroontress.nugetscraper.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.maroontress.nugetscraper.NugetScraper;
import com.maroontress.nugetscraper.Package;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicTest {

    @Test
    public void withoutNext() throws IOException {
        var expectedList = List.of(Package.of("StyleChecker", 23088),
                Package.of("StyleChecker.Annotations", 6456),
                Package.of("Maroontress.HtmlBuilder", 4291),
                Package.of("BomSweeper.GlobalTool", 1815),
                Package.of("Maroontress.Oxbind", 1752),
                Package.of("Maroontress.CuiMallet", 1014),
                Package.of("Maroontress.Collection", 421),
                Package.of("Maroontress.Euclid", 248),
                Package.of("PourOver.GlobalTool", 213));

        try (var in = getClass().getResourceAsStream("Maroontress.html")) {
            assert in != null;
            var out = new ByteArrayOutputStream();
            in.transferTo(out);
            var content = out.toString(StandardCharsets.UTF_8);
            var profile = NugetScraper.toProfile(content);
            var all = profile.packageList();
            var n = all.size();
            assertThat(n, is(equalTo(expectedList.size())));
            for (var k = 0; k < n; ++k) {
                var i = all.get(k);
                var j = expectedList.get(k);
                assertThat(i.title(), is(equalTo(j.title())));
                assertThat(i.totalDownloads(), is(equalTo(j.totalDownloads())));
            }
            assertThat(profile.nextPageUrl().isEmpty(), is(true));
        }
    }

    @Test
    public void withNext() throws IOException {
        var expectedList = List.of(
                Package.of("Microsoft.NETCore.Platforms", 3041601407L),
                Package.of("Microsoft.Extensions.Primitives", 3037823349L),
                Package.of("Microsoft.Extensions.DependencyInjection.Abstractions", 2980667925L));

        try (var in = getClass().getResourceAsStream("Microsoft.html")) {
            assert in != null;
            var out = new ByteArrayOutputStream();
            in.transferTo(out);
            var content = out.toString(StandardCharsets.UTF_8);
            var profile = NugetScraper.toProfile(content);
            var all = profile.packageList();
            var n = all.size();
            assertThat(n, is(equalTo(20)));
            var m = expectedList.size();
            for (var k = 0; k < m; ++k) {
                var i = all.get(k);
                var j = expectedList.get(k);
                assertThat(i.title(), is(equalTo(j.title())));
                assertThat(i.totalDownloads(), is(equalTo(j.totalDownloads())));
            }
            assertThat(profile.nextPageUrl().isEmpty(), is(false));
            var url = profile.nextPageUrl().get();
            assertThat(url, is(equalTo("/profiles/Microsoft?page=2")));
        }
    }

    @Test
    public void invalidPages() throws IOException {
        var files = List.of(
                "invalidNext.html",
                "invalidPackage.html",
                "noTitle.html",
                "invalidTotalDownloads.html",
                "negativeTotalDownloads.html");
        for (var i : files) {
            try (var in = getClass().getResourceAsStream(i)) {
                assert in != null;
                var out = new ByteArrayOutputStream();
                in.transferTo(out);
                var content = out.toString(StandardCharsets.UTF_8);
                assertThrows(IllegalArgumentException.class, () -> {
                    NugetScraper.toProfile(content);
                });
            }
        }
    }
}
