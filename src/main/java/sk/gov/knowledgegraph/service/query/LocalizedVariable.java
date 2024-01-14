package sk.gov.knowledgegraph.service.query;

/**
 * Class is responsible for handling localized variables
 * 
 * @author <a href="mailto:marek.surek@xit.camp">marek.surek@xit.camp</a>
 * 
 */
public final class LocalizedVariable {

    private String variableName;
    private boolean filterUnboundValues = false;
    private boolean filterUntagedValues = true;


    public LocalizedVariable(String variableName) {
        this.variableName = variableName;
    }


    public String getVariableName() {
        return variableName;
    }


    public LocalizedVariable setVariableName(String variableName) {
        this.variableName = variableName;
        return this;
    }


    /**
     * If value of variable in OPTIONAL block is unbound and this variable is localized, automatically would be filtered out. Default behaviour is to check
     * whether the value is bound and if it is not, do not filter out the row from result
     * 
     * @return Filter out unbound localized variables?
     */
    public boolean isFilterUnboundValues() {
        return filterUnboundValues;
    }


    /**
     * If value of variable in OPTIONAL block is unbound and this variable is localized, automatically would be filtered out. Default behaviour is to check
     * whether the value is bound and if it is not, do not filter out the row from result
     * 
     * @param filterUnboundValues
     *            Filter out unbound localized variables?
     */
    public LocalizedVariable setFilterUnboundValues(boolean filterUnboundValues) {
        this.filterUnboundValues = filterUnboundValues;
        return this;
    }


    /**
     * Some texts doesn't have language tag. Default behaviour is filter out texts, which are not explicitly in specified
     * 
     * @return Should be untagged(values with no specific language tag) filtered out from the result set?
     */
    public boolean isFilterUntagedValues() {
        return filterUntagedValues;
    }


    /**
     * Some texts doesn't have language tag. Default behaviour is filter out texts, which are not explicitly in specified
     * 
     * @param filterUntagedValues
     *            Should be untagged(values with no specific language tag) filtered out from the result set?
     */
    public LocalizedVariable setFilterUntagedValues(boolean filterUntagedValues) {
        this.filterUntagedValues = filterUntagedValues;
        return this;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((variableName == null) ? 0 : variableName.hashCode());
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
        LocalizedVariable other = (LocalizedVariable) obj;
        if (variableName == null) {
            if (other.variableName != null)
                return false;
        } else if (!variableName.equals(other.variableName))
            return false;
        return true;
    }

}