# NugetScraper

NugetScraper is an HTML parser that reads an HTML page retrieved from
[NuGet.org] and provides a list containing NuGet packages released by a
specified organization.

## Example

[A typical usage example](src/test/java/com/example/Demo.java) would be as
follows:

```java
...
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
```

In this example, the result of "`java com.example.Demo Microsoft`" (that
represents the one of parsing [`https://nuget.org/profiles/Microsoft/`][nuget-microsoft]) will be as
follows:

```plaintext
Microsoft.Extensions.Primitives:3179750929
Microsoft.NETCore.Platforms:3174900796
Microsoft.Extensions.DependencyInjection.Abstractions:3114882542
System.Runtime.CompilerServices.Unsafe:2688797891
Microsoft.Extensions.Options:2661649028
Microsoft.Extensions.Logging.Abstractions:2638017167
Microsoft.Extensions.Configuration.Abstractions:2578117049
System.Diagnostics.DiagnosticSource:2574167053
System.Threading.Tasks.Extensions:2290236144
Microsoft.CSharp:2003285729
System.Buffers:1987825889
Microsoft.Extensions.DependencyInjection:1958339674
Microsoft.Extensions.Configuration:1916727034
Microsoft.Extensions.FileProviders.Abstractions:1844208793
Microsoft.Extensions.Logging:1837702027
System.Memory:1792564237
Microsoft.Extensions.Configuration.Binder:1668198620
System.Security.Principal.Windows:1637388455
Microsoft.NETCore.Targets:1590023964
System.Security.Cryptography.Cng:1539977932
The next page URL: https://www.nuget.org/profiles/Microsoft?page=2
```

Note that each number represents the total downloads at that time.

> 🚧 The structure of the HTML that `nuget.org` generates is subject to change.
> This parser will follow such changes in future releases.

## API Reference

- [com.maroontress.nugetscraper][apiref-maroontress.nugetscraper] module

[NuGet.org]: https://nuget.org/
[nuget-microsoft]: https://nuget.org/profiles/Microsoft/
[apiref-maroontress.nugetscraper]:
  https://maroontress.github.io/NugetScraper/api/latest/html/index.html
