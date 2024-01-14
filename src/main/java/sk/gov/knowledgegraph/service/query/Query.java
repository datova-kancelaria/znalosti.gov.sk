package sk.gov.knowledgegraph.service.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.rdf4j.model.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reprezentuje samotny dopyt nad databazov. Obaluje retazcovu reprezentaciu dopytu a dovoluje nam s nim manipulovat, pomocou naimplementovanych metod.
 * 
 * @author <a href="mailto:marek.surek@xit.camp">marek.surek@xit.camp</a>
 * 
 */
public class Query implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(Query.class.getName());

    private final String id;
    private String query;
    private String type;
    private Locale languageTag = null;
    private int limit = 0;
    private int offset = 0;
    private boolean includeInfered = true;
    private String orderByVariable = null;
    private SortDirection direction = SortDirection.ASC;
    private HashMap<String, List<String>> bindedValuesToVariable = new HashMap<String, List<String>>();
    private Set<LocalizedVariable> localizedVariables = new HashSet<LocalizedVariable>();

    public Query(String query) {
        this(null, query, null, true);
    }


    public Query(String id, String query) {
        this(id, query, null, true);
    }


    public Query(String query, Locale langTag) {
        this(null, query, langTag, true);
    }


    public Query(String query, boolean includeInfered) {
        this(null, query, null, includeInfered);
    }


    public Query(String query, Locale languageTag, boolean includeInfered) {
        this(null, query, languageTag, includeInfered);
    }


    public Query(String id, String query, Locale languageTag, boolean includeInfered) {
        this.id = id;
        this.query = query;
        if (this.query == null) {
            this.query = "";
        }
        this.setIncludeInfered(includeInfered);
        setType(query);
    }


    public String getId() {
        return this.id;
    }


    private void setType(String query) {
        TreeMap<String, Integer> map = new TreeMap<String, Integer>();
        map.put("BOOLEAN", query.indexOf("ASK"));
        map.put("GRAPH", query.indexOf("CONSTRUCT"));
        map.put("TUPLE", query.indexOf("SELECT"));

        int upd = query.indexOf("UPDATE");
        if (upd == -1 || (query.indexOf("DELETE") != -1 && upd > query.indexOf("DELETE"))) {
            upd = query.indexOf("DELETE");
        }
        if (upd == -1 || (query.indexOf("INSERT") != -1 && upd > query.indexOf("INSERT"))) {
            upd = query.indexOf("INSERT");
        }
        if (upd == -1 || (query.indexOf("MOVE") != -1 && upd > query.indexOf("MOVE"))) {
            upd = query.indexOf("MOVE");
        }
        if (upd == -1 || (query.indexOf("ADD") != -1 && upd > query.indexOf("ADD"))) {
            upd = query.indexOf("ADD");
        }
        if (upd == -1 || (query.indexOf("COPY") != -1 && upd > query.indexOf("COPY"))) {
            upd = query.indexOf("COPY");
        }
        if (upd == -1 || (query.indexOf("DROP") != -1 && upd > query.indexOf("DROP"))) {
            upd = query.indexOf("DROP");
        }
        map.put("UPDATE", upd);

        upd = Integer.MAX_VALUE;
        for (String key : map.keySet()) {
            if (map.get(key).intValue() > -1 && map.get(key).intValue() < upd) {
                upd = map.get(key).intValue();
                this.type = key;
            }
        }

        // try {
        // ParsedQuery queryNode = QueryParserUtil.parseQuery(
        // getQueryLanguage(), getPreparedQuery(), null );
        //
        // if ( queryNode instanceof ParsedTupleQuery ) {
        // this.type ="TUPLE";
        // } else if ( queryNode instanceof ParsedGraphQuery ) {
        // this.type ="UPDATE";
        // } else if ( queryNode instanceof ParsedBooleanQuery ) {
        // this.type ="BOOLEAN";
        // } else if ( queryNode instanceof ParsedGraphQuery ) {
        // this.type ="GRAPH";
        // } else {
        // throw new RuntimeException( "Unexpected query type: " +
        // queryNode.getClass() );
        // }
        // } catch ( MalformedQueryException e ) {
        // log.error( e );
        // } catch ( UnsupportedQueryLanguageException e ) {
        // log.error( e );
        // }

    }


    public String getRawQuery() {
        return this.query;
    }


    public void setQuery(String query) {
        this.query = query;
    }


    /**
     * Method process inserted query and bind variables, languages, ordering, limit etc.
     * 
     * @return processed query (bind variables, languages, ordering, limit etc. )
     */
    public String getPreparedQuery() {
        return getPreparedQuery(null);
    }


    /**
     * Method process inserted query and bind variables, languages, ordering, limit etc.
     * 
     * @return    processed query (bind variables, languages, ordering, limit etc. )
     * @param  ns
     *            namespaces
     */
    public String getPreparedQuery(List<Namespace> ns) {
        if (ns == null) {
            ns = new ArrayList<Namespace>();
        }
        return parseQuery(this.query, ns);
    }


    private String parseQuery(String query, List<Namespace> ns) {
        StringBuilder buf = new StringBuilder();

        if (!query.toLowerCase().contains("prefix ")) {
            for (Namespace n : ns) {
                buf.append("PREFIX ");
                buf.append(n.getPrefix());
                buf.append(":<");
                buf.append(n.getName());
                buf.append("> ");
            }
        }

        buf.append(QueryUtils.stripString(query));

        if (!this.bindedValuesToVariable.isEmpty()) {

            String[][] bindings = getCartesianProduct(this.bindedValuesToVariable);

            StringBuilder sb = new StringBuilder();
            sb.append(" VALUES (");
            for (int i = 0; i < bindings[0].length; i++) {
                sb.append("?");
                sb.append(bindings[0][i]);
                sb.append(" ");
            }

            sb.append(") { ");

            for (int i = 1; i < bindings.length; i++) {
                sb.append(" (");
                for (int j = 0; j < bindings[0].length; j++) {

                    if (bindings[i][j].startsWith("http://") || bindings[i][j].startsWith("https://")) {
                        sb.append(" <");
                    } else {
                        sb.append(" ");
                    }
                    sb.append(bindings[i][j]);
                    if (bindings[i][j].startsWith("http://") || bindings[i][j].startsWith("https://")) {
                        sb.append(">");
                    }
                }
                sb.append(" ) ");

            }
            sb.append(" } ");

            String tmpQuery = buf.toString();
            if ("TUPLE".equals(this.type)) {
                buf = new StringBuilder(tmpQuery.substring(0, tmpQuery.indexOf("{")) + "{ " + sb.toString() + tmpQuery.substring(tmpQuery.indexOf("{") + 1));
            } else {
                buf = new StringBuilder(tmpQuery.substring(0, tmpQuery.indexOf("{", tmpQuery.indexOf("WHERE"))) + "{ " + sb.toString()
                        + tmpQuery.substring(tmpQuery.indexOf("{", tmpQuery.indexOf("WHERE")) + 1));
            }

        }

        if (this.languageTag != null && this.localizedVariables.size() > 0) {
            StringBuffer langBuf = new StringBuffer();
            langBuf.append(" . FILTER( ");
            Iterator<LocalizedVariable> i = this.localizedVariables.iterator();
            LocalizedVariable languageVariable = null;
            while (i.hasNext()) {
                languageVariable = i.next();
                langBuf.append("( langMatches(lang(?");
                langBuf.append(languageVariable.getVariableName());
                langBuf.append("), \"");
                langBuf.append(this.languageTag.getLanguage());
                langBuf.append("\")");
                if (!languageVariable.isFilterUntagedValues()) {
                    langBuf.append(" || langMatches(lang(?");
                    langBuf.append(languageVariable.getVariableName());
                    langBuf.append("), \"\") ");
                }
                if (!languageVariable.isFilterUnboundValues()) {
                    langBuf.append(" || !BOUND(?");
                    langBuf.append(languageVariable.getVariableName());
                    langBuf.append(") ");
                }

                langBuf.append(" )");

                if (i.hasNext()) {
                    langBuf.append(" && ");
                }
            }
            langBuf.append(") ");
            buf.insert(buf.lastIndexOf("}"), langBuf.toString());
        }

        if (getOrderByVariable() != null && !getOrderByVariable().isEmpty()) {
            buf.append(" ORDER BY ");
            if (SortDirection.ASC.equals(getDirection())) {
                buf.append(" ASC(?" + getOrderByVariable() + ")");
            } else {
                buf.append(" DESC(?" + getOrderByVariable() + ")");
            }
        }

        if (this.limit > 0 && buf.indexOf("LIMIT", buf.lastIndexOf("}")) == -1) {
            buf.append(" LIMIT " + this.limit);
        }
        if (this.offset > 0 && buf.indexOf("OFFSET", buf.lastIndexOf("}")) == -1) {
            buf.append(" OFFSET " + this.offset);
        }

        return buf.toString();
    }


    /**
     * Method responsible for making cartesian product of binded variables
     * 
     * @param  dataset
     * @return
     */
    private String[][] getCartesianProduct(HashMap<String, List<String>> dataset) {
        int n = dataset.keySet().size();
        int solutions = 1;

        for (String key : dataset.keySet()) {
            solutions *= dataset.get(key).size();
        }

        String[][] allCombinations = new String[solutions + 1][];
        allCombinations[0] = dataset.keySet().toArray(new String[n]);

        for (int i = 0; i < solutions; i++) {
            List<String> combination = new ArrayList<String>(n);
            int j = 1;
            for (List<String> vec : dataset.values()) {
                combination.add(vec.get((i / j) % vec.size()));
                j *= vec.size();
            }
            allCombinations[i + 1] = combination.toArray(new String[n]);
        }

        return allCombinations;

    }


    /**
     * Setter method which process inserted key value pair(variable-value)
     * 
     * @param variableName
     *                     - name of variable which should be inicialized with value
     * @param value
     *                     - value to be added to variable in query
     */
    public void bindValueToVariable(String variableName, String value) {
        if (value == null || value.trim().isEmpty()) {
            log.debug("Empty variable value cannot be binded in query : {}", new Object[] { getPreparedQuery() });
            return;
        }
        if (this.bindedValuesToVariable.containsKey(variableName)) {
            this.bindedValuesToVariable.get(variableName).add(value);
        } else {

            this.bindedValuesToVariable.put(variableName, new ArrayList<String>(Arrays.asList(value)));
        }
    }


    public HashMap<String, List<String>> getBindedVariables() {
        return this.bindedValuesToVariable;
    }


    /**
     * Setter method which process inserted key value pair(variable-value)
     * 
     * @param variableName
     *                     - name of variable which should be inicialized with value
     * @param values
     *                     - values to be added to variable in query
     */
    public void bindValuesToVariable(String variableName, List<String> values) {
        if (this.bindedValuesToVariable.containsKey(variableName)) {
            this.bindedValuesToVariable.get(variableName).addAll(values);
        } else {
            this.bindedValuesToVariable.put(variableName, values);
        }
    }


    /**
     * Select which variable should be localized. The query is localized according to localeLanguage. Default behavior is to remove results from query which
     * don't fit to actual language. At the same time, if variable is in OPTIONAL block and is not bound with value, default behavior is not to remove it from
     * result set. To change this behavior, you can customize variable by LocalizedVariable object. See bindLanguageTagToVariable(LocalizedVariable)
     * 
     * @param variableName
     *                     - variableName to be localized
     */
    public void bindLanguageTagToVariable(String variableName) {
        this.localizedVariables.add(new LocalizedVariable(variableName));
    }


    /**
     * Select which variable should be localized. The query is localized according to localeLanguage. Default behavior is to remove results from query which
     * don't fit to actual language. At the same time, if variable is in OPTIONAL block and is not bound with value, default behavior is not to remove it from
     * result set. To change this behavior, you can customize variable by LocalizedVariable object.
     * 
     * @param variableName
     *                     - variableName to be localized
     */
    public void bindLanguageTagToVariable(LocalizedVariable variableName) {
        this.localizedVariables.add(variableName);
    }


    public boolean isUpdate() {
        return ("UPDATE".equals(type));
    }


    public boolean isGraph() {
        return "GRAPH".equals(type);
    }


    public boolean isBoolean() {
        return "BOOLEAN".equals(type);
    }


    public boolean isTuple() {
        return "TUPLE".equals(type);
    }


    /**
     * Limit result set by adding LIMIT in the query
     * 
     * @param numRows
     *                - maximal number of rows in results
     */
    public void setLimit(int numRows) {
        this.limit = numRows;
    }


    /**
     * Limit result set by adding LIMIT in the query
     * 
     * @return limit
     */
    public int getLimit() {
        return this.limit;
    }


    /**
     * Offset result set by adding OFFSET in the query
     * 
     * @param numRows
     *                - number of rows to be skipped from the begining
     */
    public void setOffset(int numRows) {
        this.offset = numRows;
    }


    /**
     * Offset result set by adding OFFSET in the query. Number of rows to be skipped from the begining
     * 
     * @return offset
     */
    public int getOffset() {
        return this.offset;
    }


    public boolean isIncludeInfered() {
        return includeInfered;
    }


    public void setIncludeInfered(boolean includeInfered) {
        this.includeInfered = includeInfered;
    }


    /**
     * @return the localeLanguage - language which should be used in query. To take advantage of localeLanguage, you have to bind locale language to variable
     * 
     */
    public Locale getLanguageTag() {
        return languageTag;
    }


    /**
     * @param locale
     *               the localeLanguage - language which should be used in query. To take advantage of localeLanguage, you have to bind locale language to
     *               variable
     */
    public void setLocale(Locale locale) {
        this.languageTag = locale;
    }


    /**
     * @return the orderByVariable - name of the variable in query, according which results are sorted
     */
    public String getOrderByVariable() {
        return orderByVariable;
    }


    /**
     * @param orderByVariable
     *                        the orderByVariable - name of the variable in query, according which results are sorted
     */
    public void setOrderByVariable(String orderByVariable) {
        if (orderByVariable != null && orderByVariable.isEmpty()) {
            if (orderByVariable.charAt(0) == '?') {
                this.orderByVariable = orderByVariable;
            } else {
                this.orderByVariable = "?" + orderByVariable;
            }
        } else {
            this.orderByVariable = orderByVariable;
        }
    }


    /**
     * @return the direction - ASC/DESC direction of ordering in set variable. If no ordering variable is set, direction is not used.
     * 
     */
    public SortDirection getDirection() {
        return direction;
    }


    /**
     * @param direction
     *                  Set the direction of ordering(ASC/DESC). When no ordering variable is set, this method has no efect.
     */
    public void setDirection(SortDirection direction) {
        if (direction != null) {
            this.direction = direction;
        }
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Query other = (Query) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
