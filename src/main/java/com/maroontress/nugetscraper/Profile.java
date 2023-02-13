package com.maroontress.nugetscraper;

import java.util.List;
import java.util.Optional;

/**
    This interface represents an abstraction of a list of the NuGet packages
    that the specified organization has released.

    <p>The maximum list of packages that NuGet.org returns at once is 20. If
    the organization has released more packages than that, the page contains a
    link to the next page.</p>
*/
public interface Profile {

    /**
        Returns the list of the NuGet packages.

        @return The list of the NuGet packages.
    */
    List<Package> packageList();

    /**
        Returns the relative URL of the next page if it exists.

        <p>Note that the relative URL that NuGet.org returns is an <i>absolute
        URL path</i> beginning with a slash ({@code /}).</p>

        @return The URL of the next page. Or {@link Optional#empty()} if it
        does not exist.
    */
    Optional<String> nextPageUrl();

    /**
        Returns the new profile object with the specified package list and
        next page URL.

        <p>Note that the object that this method returns is immutable.</p>

        @param packageList The package list.
        @param nextPageUrl The URL of the next page.
        @return The new profile object.
    */
    static Profile of(List<Package> packageList,
                      Optional<String> nextPageUrl) {
        var list = List.copyOf(packageList);
        return new Profile() {

            @Override
            public List<Package> packageList() {
                return list;
            }

            @Override
            public Optional<String> nextPageUrl() {
                return nextPageUrl;
            }
        };
    }
}
