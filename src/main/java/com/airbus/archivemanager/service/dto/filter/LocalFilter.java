package com.airbus.archivemanager.service.dto.filter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Base class for the various attribute filters. It can be added to a criteria class as a member, to support the
 * following query parameters:
 * <pre>
 *      fieldName.equals='something'
 *      fieldName.specified=true
 *      fieldName.specified=false
 *      fieldName.in='something','other'
 * </pre>
 */
public class LocalFilter<FIELD_TYPE> implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient FIELD_TYPE equals;
    private Boolean specified;

    /**
     * <p>Constructor for Filter.</p>
     */
    public LocalFilter() {
    }

    /**
     * <p>Constructor for Filter.</p>
     *
     * @param filter a {@link LocalFilter} object.
     */
    public LocalFilter(LocalFilter<FIELD_TYPE> filter) {
        this.equals = filter.equals;
        this.specified = filter.specified;
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link LocalFilter} object.
     */
    public LocalFilter<FIELD_TYPE> copy() {
        return new LocalFilter<>(this);
    }

    /**
     * <p>Getter for the field <code>equals</code>.</p>
     *
     * @return a FIELD_TYPE object.
     */
    public FIELD_TYPE getEquals() {
        return equals;
    }

    /**
     * <p>Setter for the field <code>equals</code>.</p>
     *
     * @param equals a FIELD_TYPE object.
     * @return a {@link io.github.jhipster.service.filter.Filter} object.
     */
    public LocalFilter<FIELD_TYPE> setEquals(FIELD_TYPE equals) {
        this.equals = equals;
        return this;
    }

    /**
     * <p>Getter for the field <code>specified</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getSpecified() {
        return specified;
    }

    /**
     * <p>Setter for the field <code>specified</code>.</p>
     *
     * @param specified a {@link java.lang.Boolean} object.
     * @return a {@link LocalFilter} object.
     */
    public LocalFilter<FIELD_TYPE> setSpecified(Boolean specified) {
        this.specified = specified;
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
        final LocalFilter<?> filter = (LocalFilter<?>) o;
        return Objects.equals(equals, filter.equals) &&
            Objects.equals(specified, filter.specified);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(equals, specified);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getFilterName() + " ["
            + (getEquals() != null ? "equals=" + getEquals() + ", " : "")
            + (getSpecified() != null ? "specified=" + getSpecified() : "")
            + "]";
    }

    /**
     * <p>getFilterName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected String getFilterName() {
        return this.getClass().getSimpleName();
    }
}


