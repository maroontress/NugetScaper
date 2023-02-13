/**
    This module provides an HTML parser that reads an HTML page retrieved from
    NuGet.org and provides a list containing NuGet packages released by a
    specified organization.
*/
module com.maroontress.nugetscraper {
    requires org.jsoup;
    exports com.maroontress.nugetscraper;
}
