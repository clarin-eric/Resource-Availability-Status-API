package linkResources;

import helpers.CheckedLinkFilter;
import links.CheckedLink;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface checkedLinkResource {

    enum Order {
        ASC,
        DESC
    }

    /* retrieve for single URI (will be normalised before retrieval) */
    public CheckedLink get(URI uri);

    /* batch retrieval with URI as key (to be normalised before retrieval)  with optional filtering
       get(“http://clarin.eu”, new FilterImpl(404, 2019-01-01, 2019-02-01)
    */
    public Map<URI, CheckedLink> get(Collection<URI> uri, Optional<CheckedLinkFilter> filter);

    /* retrieval of history for one URI  as key with optional filtering */
    public Stream<CheckedLink> getHistory(URI uri, Order order, Optional<CheckedLinkFilter> filter);

}
