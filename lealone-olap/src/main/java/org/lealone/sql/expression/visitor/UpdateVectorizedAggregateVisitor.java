/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package org.lealone.sql.expression.visitor;

import java.util.ArrayList;

import org.lealone.db.result.Row;
import org.lealone.db.session.ServerSession;
import org.lealone.sql.expression.ExpressionColumn;
import org.lealone.sql.expression.aggregate.AGroupConcat;
import org.lealone.sql.expression.aggregate.Aggregate;
import org.lealone.sql.expression.aggregate.BuiltInAggregate;
import org.lealone.sql.expression.aggregate.JavaAggregate;
import org.lealone.sql.vector.ValueVector;

public class UpdateVectorizedAggregateVisitor extends VoidExpressionVisitor {

    private ServerSession session;
    private ValueVector bvv;
    private GetValueVectorVisitor getValueVectorVisitor;

    public UpdateVectorizedAggregateVisitor(ServerSession session, ValueVector bvv, ArrayList<Row> batch) {
        this.session = session;
        this.bvv = bvv;
        this.getValueVectorVisitor = new GetValueVectorVisitor(session, bvv, batch);
    }

    @Override
    public Void visitExpressionColumn(ExpressionColumn e) {
        e.updateAggregate(session); // 直接更新单行即可
        return null;
    }

    @Override
    public Void visitAggregate(Aggregate e) {
        ((BuiltInAggregate) e).updateVectorizedAggregate(session, bvv, getValueVectorVisitor);
        return null;
    }

    @Override
    public Void visitAGroupConcat(AGroupConcat e) {
        e.updateVectorizedAggregate(session, bvv, getValueVectorVisitor);
        return null;
    }

    @Override
    public Void visitJavaAggregate(JavaAggregate e) {
        // e.updateVectorizedAggregate(session, bvv, getValueVectorVisitor);
        return null;
    }
}