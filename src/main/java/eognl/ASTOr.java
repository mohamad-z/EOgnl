/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.BooleanExpression;
import eognl.EOgnlRuntime;
import eognl.Node;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.enhance.ExpressionCompiler;
import eognl.enhance.UnsupportedCompilationException;

public class ASTOr
extends BooleanExpression {
    public ASTOr(int id) {
        super(id);
    }

    public ASTOr(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    public void jjtClose() {
        this.flattenTree();
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object result = null;
        int last = this.children.length - 1;
        for (int i = 0; i <= last; ++i) {
            result = this.children[i].getValue(context, source);
            if (i != last && OgnlOps.booleanValue(result)) break;
        }
        return result;
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        int last = this.children.length - 1;
        for (int i = 0; i < last; ++i) {
            Object v = this.children[i].getValue(context, target);
            if (!OgnlOps.booleanValue(v)) continue;
            return;
        }
        this.children[last].setValue(context, target, value);
    }

    @Override
    public String getExpressionOperator(int index) {
        return "||";
    }

    @Override
    public Class<?> getGetterClass() {
        return null;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        if (this.children.length != 2) {
            throw new UnsupportedCompilationException("Can only compile boolean expressions with two children.");
        }
        String result = "(";
        try {
            String first = EOgnlRuntime.getChildSource(context, target, this.children[0]);
            if (!EOgnlRuntime.isBoolean(first)) {
                first = EOgnlRuntime.getCompiler(context).createLocalReference(context, first, context.getCurrentType());
            }
            Class<?> firstType = context.getCurrentType();
            String second = EOgnlRuntime.getChildSource(context, target, this.children[1]);
            if (!EOgnlRuntime.isBoolean(second)) {
                second = EOgnlRuntime.getCompiler(context).createLocalReference(context, second, context.getCurrentType());
            }
            Class<?> secondType = context.getCurrentType();
            boolean mismatched = firstType.isPrimitive() && !secondType.isPrimitive() || !firstType.isPrimitive() && secondType.isPrimitive();
            result = String.valueOf(result) + "org.apache.commons.ognl.OgnlOps.booleanValue(" + first + ")";
            result = String.valueOf(result) + " ? ";
            result = String.valueOf(result) + (mismatched ? " ($w) " : "") + first;
            result = String.valueOf(result) + " : ";
            result = String.valueOf(result) + (mismatched ? " ($w) " : "") + second;
            result = String.valueOf(result) + ")";
            context.setCurrentObject(target);
            context.setCurrentType(Boolean.TYPE);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return result;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        if (this.children.length != 2) {
            throw new UnsupportedCompilationException("Can only compile boolean expressions with two children.");
        }
        String pre = (String)context.get("_currentChain");
        if (pre == null) {
            pre = "";
        }
        String result = "";
        try {
            this.children[0].getValue(context, target);
            String first = String.valueOf(ExpressionCompiler.getRootExpression(this.children[0], context.getRoot(), context)) + pre + this.children[0].toGetSourceString(context, target);
            if (!EOgnlRuntime.isBoolean(first)) {
                first = EOgnlRuntime.getCompiler(context).createLocalReference(context, first, Object.class);
            }
            this.children[1].getValue(context, target);
            String second = String.valueOf(ExpressionCompiler.getRootExpression(this.children[1], context.getRoot(), context)) + pre + this.children[1].toSetSourceString(context, target);
            if (!EOgnlRuntime.isBoolean(second)) {
                second = EOgnlRuntime.getCompiler(context).createLocalReference(context, second, context.getCurrentType());
            }
            result = String.valueOf(result) + "org.apache.commons.ognl.OgnlOps.booleanValue(" + first + ")";
            result = String.valueOf(result) + " ? ";
            result = String.valueOf(result) + first;
            result = String.valueOf(result) + " : ";
            result = String.valueOf(result) + second;
            context.setCurrentObject(target);
            context.setCurrentType(Boolean.TYPE);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return result;
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

