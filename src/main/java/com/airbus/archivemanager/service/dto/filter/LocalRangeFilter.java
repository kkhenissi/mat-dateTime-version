package com.airbus.archivemanager.service.dto.filter;

import java.util.Objects;

/**
 * Filter class for Comparable types, where less than / greater than / etc relations could be interpreted. It can be
 * added to a criteria class as a member, to support the following query parameters:
 * <pre>
 *      fieldName.equals=42
 *      fieldName.specified=true
 *      fieldName.specified=false
 *      fieldName.greaterThan=41
 *      fieldName.lessThan=44
 *      fieldName.greaterThanOrEqual=42
 *      fieldName.lessThanOrEqual=44
 * </pre>
 * Due to problems with the type conversions, the descendant classes should be used, where the generic type parameter
 * is materialized.
 *
 * @param <FIELD_TYPE> the type of filter.
 * @see LocalDateTimeFilter
 */
public class LocalRangeFilter<FIELD_TYPE extends Comparable<? super FIELD_TYPE>> extends LocalFilter<FIELD_TYPE> {

    private static final long serialVersionUID = 1L;

    private transient FIELD_TYPE greaterThan;
    private transient FIELD_TYPE lessThan;
    private transient FIELD_TYPE greaterThanOrEqual;
    private transient FIELD_TYPE lessThanOrEqual;

    /**
     * <p>Constructor for RangeFilter.</p>
     */
    public LocalRangeFilter() {
    }

    /**
     * <p>Constructor for RangeFilter.</p>
     *
     * @param filter a {@link com.airbus.archivemanager.service.dto.filter.LocalRangeFilter} object.
     */
    public LocalRangeFilter(final LocalRangeFilter<FIELD_TYPE> filter) {
        super(filter);
        this.greaterThan = filter.greaterThan;
        this.lessThan = filter.lessThan;
        this.greaterThanOrEqual = filter.greaterThanOrEqual;
        this.lessThanOrEqual = filter.lessThanOrEqual;
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link com.airbus.archivemanager.service.dto.filter.LocalRangeFilter} object.
     */
    @Override
    public LocalRangeFilter<FIELD_TYPE> copy() {
        return new LocalRangeFilter<>(this);
    }

    /**
     * <p>Getter for the field <code>greaterThan</code>.</p>
     *
     * @return a FIELD_TYPE object.
     */
    public FIELD_TYPE getGreaterThan() {
        return greaterThan;
    }

    /**
     * <p>Setter for the field <code>greaterThan</code>.</p>
     *
     * @param greaterThan a FIELD_TYPE object.
     * @return a {@link com.airbus.archivemanager.service.dto.filter.LocalRangeFilter} object.
     */
    public LocalRangeFilter<FIELD_TYPE> setGreaterThan(FIELD_TYPE greaterThan) {
        this.greaterThan = greaterThan;
        return this;
    }

    /**
     * <p>Getter for the field <code>greaterThanOrEqual</code>.</p>
     *
     * @return a FIELD_TYPE object.
     */
    public FIELD_TYPE getGreaterThanOrEqual() {
        return greaterThanOrEqual;
    }

    /**
     * <p>Setter for the field <code>greaterThanOrEqual</code>.</p>
     *
     * @param greaterThanOrEqual a FIELD_TYPE object.
     * @return a {@link com.airbus.archivemanager.service.dto.filter.LocalRangeFilter} object.
     */
    public LocalRangeFilter<FIELD_TYPE> setGreaterThanOrEqual(FIELD_TYPE greaterThanOrEqual) {
        this.greaterThanOrEqual = greaterThanOrEqual;
        return this;
    }

    /**
     * <p>Getter for the field <code>lessThan</code>.</p>
     *
     * @return a FIELD_TYPE object.
     */
    public FIELD_TYPE getLessThan() {
        return lessThan;
    }

    /**
     * <p>Setter for the field <code>lessThan</code>.</p>
     *
     * @param lessThan a FIELD_TYPE object.
     * @return a {@link com.airbus.archivemanager.service.dto.filter.LocalRangeFilter} object.
     */
    public LocalRangeFilter<FIELD_TYPE> setLessThan(FIELD_TYPE lessThan) {
        this.lessThan = lessThan;
        return this;
    }

    /**
     * <p>Getter for the field <code>lessThanOrEqual</code>.</p>
     *
     * @return a FIELD_TYPE object.
     */
    public FIELD_TYPE getLessThanOrEqual() {
        return lessThanOrEqual;
    }

    /**
     * <p>Setter for the field <code>lessThanOrEqual</code>.</p>
     *
     * @param lessThanOrEqual a FIELD_TYPE object.
     * @return a {@link com.airbus.archivemanager.service.dto.filter.LocalRangeFilter} object.
     */
    public LocalRangeFilter<FIELD_TYPE> setLessThanOrEqual(FIELD_TYPE lessThanOrEqual) {
        this.lessThanOrEqual = lessThanOrEqual;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final LocalRangeFilter<?> that = (LocalRangeFilter<?>) o;
        return Objects.equals(greaterThan, that.greaterThan) &&
            Objects.equals(lessThan, that.lessThan) &&
            Objects.equals(greaterThanOrEqual, that.greaterThanOrEqual) &&
            Objects.equals(lessThanOrEqual, that.lessThanOrEqual);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), greaterThan, lessThan, greaterThanOrEqual, lessThanOrEqual);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getFilterName() + " ["
            + (getGreaterThan() != null ? "greaterThan=" + getGreaterThan() + ", " : "")
            + (getGreaterThanOrEqual() != null ? "greaterThanOrEqual=" + getGreaterThanOrEqual() + ", " : "")
            + (getLessThan() != null ? "lessThan=" + getLessThan() + ", " : "")
            + (getLessThanOrEqual() != null ? "lessThanOrEqual=" + getLessThanOrEqual() + ", " : "")
            + (getEquals() != null ? "equals=" + getEquals() + ", " : "")
            + (getSpecified() != null ? "specified=" + getSpecified() + ", " : "")
            + "]";
    }

}
