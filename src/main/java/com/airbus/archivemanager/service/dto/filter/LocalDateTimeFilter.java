package com.airbus.archivemanager.service.dto.filter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Filter class for {@link java.time.LocalDateTime} type attributes.
 *
 * @see LocalRangeFilter
 */
public class LocalDateTimeFilter extends LocalRangeFilter<LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for LocalDateTimeFilter.</p>
     */
    public LocalDateTimeFilter() {
    }

    /**
     * <p>Constructor for LocalDateTimeFilter.</p>
     *
     * @param filter a {@link LocalDateTimeFilter} object.
     */
    public LocalDateTimeFilter(final LocalDateTimeFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link LocalDateTimeFilter} object.
     */
    @Override
    public LocalDateTimeFilter copy() {
        return new LocalDateTimeFilter(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public LocalDateTimeFilter setEquals(LocalDateTime equals) {
        super.setEquals(equals);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public LocalDateTimeFilter setGreaterThan(LocalDateTime equals) {
        super.setGreaterThan(equals);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public LocalDateTimeFilter setGreaterThanOrEqual(LocalDateTime equals) {
        super.setGreaterThanOrEqual(equals);
        return this;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public LocalDateTimeFilter setLessThan(LocalDateTime equals) {
        super.setLessThan(equals);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public LocalDateTimeFilter setLessThanOrEqual(LocalDateTime equals) {
        super.setLessThanOrEqual(equals);
        return this;
    }
}
