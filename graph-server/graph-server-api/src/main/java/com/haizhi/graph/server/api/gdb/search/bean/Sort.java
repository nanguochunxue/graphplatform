package com.haizhi.graph.server.api.gdb.search.bean;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by thomas on 18/2/6.
 */
public class Sort
{
    public static final Direction DEFAULT_DIRECTION = Direction.ASC;
    private List<Order> orders;

    public enum Direction {
        ASC,
        DESC;

        public boolean isAscending() {
            return equals(ASC);
        }

        public boolean isDescending() {
            return equals(DESC);
        }

        public static Sort.Direction fromString(String value) {
            try {
                return valueOf(value.toUpperCase());
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("Invalid direction '%s'. Options: 'desc' or 'asc' (case insensitive).", value), e);
            }
        }

        public static Sort.Direction fromStringOrNull(String value) {
            try {
                return fromString(value);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public static class Order
    {
        /**
         * the field to sort(eg. age, name, p.name, c._id)
         */
        private String property;
        private Direction direction;

        public Order() {}

        public Order(String property)
        {
            this(property, DEFAULT_DIRECTION);
        }

        public Order(String property, Direction direction)
        {
            this.property = property;
            this.direction = direction;
        }

        public String getProperty()
        {
            return property;
        }

        public void setProperty(String property)
        {
            this.property = property;
        }

        public Direction getDirection()
        {
            return direction;
        }

        public void setDirection(Direction direction)
        {
            this.direction = direction;
        }
    }

    public Sort()
    {
        this.orders = new ArrayList<>();
    }

    public Sort(Order... orders)
    {
        this.orders = Arrays.asList(orders);
    }

    public Sort(String... properties)
    {
        orders = Arrays.stream(properties).map(Order::new).collect(Collectors.toList());
    }

    public void setOrders(List<Order> orders)
    {
        this.orders = orders;
    }

    public List<Order> getOrders()
    {
        return orders;
    }

    public Sort add(Order... orders)
    {
        if(orders != null && orders.length > 0)
            this.orders.addAll(Arrays.asList(orders));
        return this;
    }

    public Sort add(String... properties)
    {
        if(properties != null && properties.length > 0)
            this.orders.addAll(Arrays.stream(properties).map(Order::new).collect(Collectors.toList()));
        return this;
    }

    public static boolean isEmpty(Sort sort)
    {
        return sort == null || CollectionUtils.isEmpty(sort.orders);
    }

    /**
     * parse sorting information
     *
     * @return a string contains all sorting information(eg. p._id ASC, c.name DESC)
     */
    public String parse()
    {
        if(!CollectionUtils.isEmpty(orders))
        {
            List<String> strList = orders.stream().filter(Objects::nonNull).map(order -> order.getProperty() + " " + order.getDirection().name()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(strList)) return StringUtils.join(strList, ", ");
        }
        return "";
    }
}
