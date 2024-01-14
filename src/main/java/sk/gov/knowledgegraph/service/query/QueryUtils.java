package sk.gov.knowledgegraph.service.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bezne metody potrebne pre pracu s dopytmi nad databazou
 * 
 * @author <a href="mailto:marek.surek@xit.camp">marek.surek@xit.camp</a>
 * 
 */
public class QueryUtils {

    private final static Logger log = LoggerFactory.getLogger(QueryUtils.class.getName());


    /**
     * Odstranuje nepotrebne biele znaky z query. Vhodna, ak citame sformatovane query zo suboru. V takom pripade obsahuje dopyt velke mnozstvo bielych znakov,
     * ktore ale nie su povolene pri spustani query nad databazov.
     * 
     * @param s string to be processed
     * @return processed string
     */
    public static String stripString(String s) {
        if (s == null) {
            return "";
        }
        return s.trim().replaceAll("\\s+", " ");
    }


    public static String getPrefix(String uri) {
        if (uri == null) {
            return "";
        }
        int index = uri.indexOf(":");
        if (index == -1) {
            return "";
        }

        return uri.substring(index + 1);
    }
}
