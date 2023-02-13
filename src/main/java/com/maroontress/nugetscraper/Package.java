package com.maroontress.nugetscraper;

/**
    This interface provides the name and the number of the total downloads
    of the NuGet package.
*/
public interface Package {

    /**
        Returns the name of the NuGet package.

        @return The name of the NuGet package.
    */
    String title();

    /**
        Returns the number of the total downloads.

        @return The number of the total downloads.
    */
    long totalDownloads();

    /**
        Returns the new package object with the specified title and the number
        of total downloads.

        <p>Note that the object that this method returns is immutable.</p>

        @param title The name of the NuGet package.
        @param totalDownloads The number of the total downloads.
        @return The new package object.
    */
    static Package of(String title, long totalDownloads) {
        return new Package() {
            @Override
            public long totalDownloads() {
                return totalDownloads;
            }

            @Override
            public String title() {
                return title;
            }
        };
    }
}
