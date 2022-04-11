package com.tengjiao.tool.indep.sql;

import com.tengjiao.tool.indep.StringTool;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL工具类
 * <br>Usage:<br>
 *
 * SqlTool.custom().andEqualTo(<property>, <value>)
 *
 * @author kangtengjiao
 */
public final class SqlTool {
    private Criteria criteria;

    private SqlTool() {
        this.criteria = new Criteria();
    }

    public static SqlTool custom() {
        return new SqlTool();
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public SqlTool andIsNull(String property) {
        this.criteria.criterions.add(new Criterion(property, "is null", "and"));
        return this;
    }

    public SqlTool andIsNotNull(String property) {
        this.criteria.criterions.add(new Criterion(property, "is not null", "and"));
        return this;
    }

    public SqlTool andEqualTo(String property, Object value) {
        if (value == null) {
            this.criteria.criterions.add(new Criterion(property, "is null", "and"));
        } else {
            this.criteria.criterions.add(new Criterion(property, value, "=", "and"));
        }
        return this;
    }

    public SqlTool andEqualTo(String property, Object value, boolean ignoreNull) {
        if (ignoreNull && value == null) {
            return this;
        }
        if (value == null) {
            this.criteria.criterions.add(new Criterion(property, "is null", "and"));
        } else {
            this.criteria.criterions.add(new Criterion(property, value, "=", "and"));
        }
        return this;
    }

    public SqlTool andNotEqualTo(String property, Object value) {
        this.criteria.criterions.add(new Criterion(property, value, "<>", "and"));
        return this;
    }

    public SqlTool andNotEqualTo(String property, Object value, boolean ignoreNull) {
        if (ignoreNull && value == null) {
            return this;
        }
        this.criteria.criterions.add(new Criterion(property, value, "<>", "and"));
        return this;
    }

    public SqlTool andGreaterThan(String property, Object value) {
        this.criteria.criterions.add(new Criterion(property, value, ">", "and"));
        return this;
    }

    public SqlTool andGreaterThanOrEqualTo(String property, Object value) {
        this.criteria.criterions.add(new Criterion(property, value, ">=", "and"));
        return this;
    }


    public SqlTool andLessThan(String property, Object value) {
        this.criteria.criterions.add(new Criterion(property, value, "<", "and"));
        return this;
    }

    public SqlTool andLessThanOrEqualTo(String property, Object value) {
        this.criteria.criterions.add(new Criterion(property, value, "<=", "and"));
        return this;
    }

    public SqlTool andIn(String property, Iterable values) {
        this.criteria.criterions.add(new Criterion(property, values, "in", "and"));
        return this;
    }

    public SqlTool andIn(String property, Iterable values, boolean ignoreEmpty) {
        if (ignoreEmpty && !values.iterator().hasNext()) {
            return this;
        }
        this.criteria.criterions.add(new Criterion(property, values, "in", "and"));
        return this;
    }

    public SqlTool andNotIn(String property, Iterable values) {
        this.criteria.criterions.add(new Criterion(property, values, "not in", "and"));
        return this;
    }

    public SqlTool andBetween(String property, Object value1, Object value2) {
        this.criteria.criterions.add(new Criterion(property, value1, value2, "between", "and"));
        return this;
    }

    public SqlTool andNotBetween(String property, Object value1, Object value2) {
        this.criteria.criterions.add(new Criterion(property, value1, value2, "not between", "and"));
        return this;
    }

    public SqlTool andLike(String property, String value) {
        if (!StringTool.isBlank(value)) {
            value = "%" + value + "%";
        }
        this.criteria.criterions.add(new Criterion(property, value, "like", "and"));
        return this;
    }

    public SqlTool andLikeLeft(String property, String value) {
        if (!StringTool.isBlank(value)) {
            value = "%" + value;
        }
        this.criteria.criterions.add(new Criterion(property, value, "like", "and"));
        return this;
    }

    public SqlTool andLikeRight(String property, String value) {
        if (!StringTool.isBlank(value)) {
            value = value + "%";
        }
        this.criteria.criterions.add(new Criterion(property, value, "like", "and"));
        return this;
    }

    public SqlTool andLike(String property, String value, boolean ignoreNull) {
        if (ignoreNull && value == null) {
            return this;
        }
        if (!StringTool.isBlank(value)) {
            value = "%" + value + "%";
        }
        this.criteria.criterions.add(new Criterion(property, value, "like", "and"));
        return this;
    }

    public SqlTool andLikeLeft(String property, String value, boolean ignoreNull) {
        if (ignoreNull && value == null) {
            return this;
        }
        if (!StringTool.isBlank(value)) {
            value = "%" + value;
        }
        this.criteria.criterions.add(new Criterion(property, value, "like", "and"));
        return this;
    }

    public SqlTool andLikeRight(String property, String value, boolean ignoreNull) {
        if (ignoreNull && value == null) {
            return this;
        }
        if (!StringTool.isBlank(value)) {
            value = value + "%";
        }
        this.criteria.criterions.add(new Criterion(property, value, "like", "and"));
        return this;
    }

    public SqlTool andNotLike(String property, String value) {
        this.criteria.criterions.add(new Criterion(property, value, "not like", "and"));
        return this;
    }


    public SqlTool orIsNull(String property) {
        this.criteria.criterions.add(new Criterion(property, "is null", "or"));
        return this;
    }

    public SqlTool orIsNotNull(String property) {
        this.criteria.criterions.add(new Criterion(property, "is not null", "or"));
        return this;
    }


    public SqlTool orEqualTo(String property, Object value) {
        this.criteria.criterions.add(new Criterion(property, value, "=", "or"));
        return this;
    }

    public SqlTool orNotEqualTo(String property, Object value) {
        this.criteria.criterions.add(new Criterion(property, value, "<>", "or"));
        return this;
    }

    public SqlTool orGreaterThan(String property, Object value) {
        this.criteria.criterions.add(new Criterion(property, value, ">", "or"));
        return this;
    }

    public SqlTool orGreaterThanOrEqualTo(String property, Object value) {
        this.criteria.criterions.add(new Criterion(property, value, ">=", "or"));
        return this;
    }

    public SqlTool orLessThan(String property, Object value) {
        this.criteria.criterions.add(new Criterion(property, value, "<", "or"));
        return this;
    }

    public SqlTool orLessThanOrEqualTo(String property, Object value) {
        this.criteria.criterions.add(new Criterion(property, value, "<=", "or"));
        return this;
    }

    public SqlTool orIn(String property, Iterable values) {
        this.criteria.criterions.add(new Criterion(property, values, "in", "or"));
        return this;
    }

    public SqlTool orNotIn(String property, Iterable values) {
        this.criteria.criterions.add(new Criterion(property, values, "not in", "or"));
        return this;
    }

    public SqlTool orBetween(String property, Object value1, Object value2) {
        this.criteria.criterions.add(new Criterion(property, value1, value2, "between", "or"));
        return this;
    }

    public SqlTool orNotBetween(String property, Object value1, Object value2) {
        this.criteria.criterions.add(new Criterion(property, value1, value2, "not between", "or"));
        return this;
    }

    public SqlTool orLike(String property, String value) {
        this.criteria.criterions.add(new Criterion(property, value, "like", "or"));
        return this;
    }

    public SqlTool orNotLike(String property, String value) {
        this.criteria.criterions.add(new Criterion(property, value, "not like", "or"));
        return this;
    }

    public static class Criteria {
        private String andOr;
        private List<Criterion> criterions;

        public Criteria() {
            this.criterions = new ArrayList<Criterion>(2);
        }

        public List<Criterion> getCriterions() {
            return criterions;
        }

        public String getAndOr() {
            return andOr;
        }

        public void setAndOr(String andOr) {
            this.andOr = andOr;
        }
    }

    public static class Criterion {
        private String property;
        private Object value;
        private Object secondValue;
        private String condition;
        private String andOr;

        public Criterion(String property, String condition, String andOr) {
            this.property = property;
            this.condition = condition;
            this.andOr = andOr;
        }


        public Criterion(String property, Object value, String condition, String andOr) {
            this.property = property;
            this.value = value;
            this.condition = condition;
            this.andOr = andOr;
        }

        public Criterion(String property, Object value1, Object value2, String condition, String andOr) {
            this.property = property;
            this.value = value1;
            this.secondValue = value2;
            this.condition = condition;
            this.andOr = andOr;
        }

        public String getProperty() {
            return property;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public Object[] getValues() {
            if (value != null) {
                if (secondValue != null) {
                    return new Object[]{value, secondValue};
                } else {
                    return new Object[]{value};
                }
            } else {
                return new Object[]{};
            }
        }

        public String getCondition() {
            return condition;
        }

        public String getAndOr() {
            return andOr;
        }
    }
}
