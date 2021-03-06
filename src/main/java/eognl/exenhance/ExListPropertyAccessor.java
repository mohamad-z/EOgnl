/*
 * Decompiled with CFR 0.139.
 */
package eognl.exenhance;

import eognl.*;

import java.util.*;

public class ExListPropertyAccessor
        extends ExObjectPropertyAccessor
        implements PropertyAccessor {
    @Override
    public Object getProperty(OgnlContext context, Object target, Object name) throws OgnlException {
        int level = this.incIndex(context);
        if (level == 1 && this.isFirstAlwaysIgnored(context) && target.getClass().isAssignableFrom(context.getRoot().getClass())) {
            this.shiftGenericParameters(context, level);
            return target;
        }
        List list = null;
        if(target instanceof List) {
            list = (List) target;
        }
        else {
            list = new ArrayList((Set) target);
        }

        if (name instanceof String) {
            Object result = null;
            if ("size".equals(name)) {
                result = list.size();
            } else if ("iterator".equals(name)) {
                result = list.iterator();
            } else if ("isEmpty".equals(name) || "empty".equals(name)) {
                result = list.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
            } else {
                if (level == 1 && this.isFirstUnknownIgnored(context) && target.getClass().isAssignableFrom(context.getRoot().getClass())) {
                    this.shiftGenericParameters(context, level);
                    return target;
                }
                this.decIndex(context);
                result = super.getProperty(context, target, name);
            }
            return result;
        }
        int index = -1;
        boolean isChained = this.isSetChain(context);
        boolean isExpanded = this.isExpanded(context);
        boolean isnullInited = this.isNullInited(context);
        if (name instanceof DynamicSubscript) {
            int len = list.size();
            switch (((DynamicSubscript) name).getFlag()) {
                case 0: {
                    if (!isChained) {
                        return len > 0 ? list.get(0) : null;
                    }
                    index = 0;
                }
                case 1: {
                    if (!isChained) {
                        return len > 0 ? list.get(len / 2) : null;
                    }
                    index = len / 2;
                }
                case 2: {
                    if (!isChained) {
                        return len > 0 ? list.get(len - 1) : null;
                    }
                    index = len - 1;
                    if (index < 0 && isExpanded) {
                        index = 0;
                    }
                }
                case 3: {
                    return new ArrayList(list);
                }
            }
        }
        if (name instanceof Number) {
            index = ((Number) name).intValue();
        }
        if (index > -1) {
            if (!this.isSetChain(context)) {
                return list.get(index);
            }
            Object value = null;
            if (list.size() > index) {
                value = processObject(context, target, list.get(index), getCurrentAnnotations(context));
                Object clsObj = null;
                if (isnullInited) {
                    clsObj = this.getParameterizedType(context, level, 0);
                }
                if (value != null && value.getClass().isArray()) {
                    this.keepArraySource(context, target, index, level);
                }
                if (value != null || !isnullInited) {
                    list.set(index, value);
                    updateTargetIfSet(target, list);
                    return value;
                }
                return createResultForNullValue(context, clsObj, target, list, index, level);
            }
            if (!isExpanded) {
                return null;
            }
            for (int i = list.size(); i <= index; ++i) {
                list.add(null);
            }
            if (!isnullInited) {
                return null;
            }
            return createResultForNullValue(context, this.getParameterizedType(context, level, 0), target, list, index, level);
        }
        return null;
    }

    @Override
    public void setProperty(OgnlContext context, Object target, Object name, Object value) throws OgnlException {
        if (name instanceof String && !((String) name).contains("$")) {
            super.setProperty(context, target, name, value);
            return;
        }
        this.incIndex(context);
        List list = (List) target;
        boolean isExpanded = this.isExpanded(context);
        if (name instanceof Number) {
            int index = ((Number) name).intValue();
            if (list.size() > index) {
                list.set(index, value);
                return;
            }
            if (!isExpanded) {
                return;
            }
            for (int i = list.size(); i <= index; ++i) {
                list.add(null);
            }
            list.set(index, value);
            return;
        }
        if (name instanceof DynamicSubscript) {
            int len = list.size();
            switch (((DynamicSubscript) name).getFlag()) {
                case 0: {
                    if (len > 0) {
                        list.set(0, value);
                        return;
                    }
                    if (!isExpanded) {
                        return;
                    }
                    list.add(value);
                    return;
                }
                case 1: {
                    if (len > 0) {
                        list.set(len / 2, value);
                        return;
                    }
                    if (!isExpanded) {
                        return;
                    }
                    list.add(value);
                    return;
                }
                case 2: {
                    if (len > 0) {
                        list.set(len - 1, value);
                        return;
                    }
                    if (!isExpanded) {
                        return;
                    }
                    list.add(value);
                    return;
                }
                case 3: {
                    if (!(value instanceof Collection)) {
                        throw new OgnlException("Value must be a collection");
                    }
                    list.clear();
                    list.addAll((Collection) value);
                    return;
                }
            }
            return;
        }
        throw new NoSuchPropertyException(target, name);
    }

    @Override
    public int getGenericArgumentsCount() {
        return 1;
    }

    private Object createResult(OgnlContext context, Class cls, Object target, List list, int index, int level) {
        try {
            Object value = this.createProperObject(context, cls, cls.getComponentType());
            if (cls.isArray()) {
                this.keepArraySource(context, list, index, level);
            }
            list.set(index, value);
            updateTargetIfSet(target, list);
            return value;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object createResultForNullValue(OgnlContext context, Object clsObj, Object target, List list, int index,
                                            int level) throws OgnlException {
        if (clsObj == null) {
            if (this.isUnknownInited(context)) {
                Object value = new Object();
                list.set(index, value);
                updateTargetIfSet(target, list);
                return value;
            }
            throw new OgnlException("Could not determine type of the List");
        }
        return createResult(context, (Class) clsObj, target, list, index, level);
    }

    private void updateTargetIfSet(Object target, List list) {
        if(target instanceof Set) {
            Set set = (Set) target;
            set.clear();
            set.addAll(list);
        }
    }
}

