package sk.gov.knowledgegraph.model.refid.application.search;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class SearchResult<T> {

    public final List<T> results;
    public final long numFound;
}