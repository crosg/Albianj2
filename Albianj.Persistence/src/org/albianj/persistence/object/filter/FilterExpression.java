package org.albianj.persistence.object.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.albianj.persistence.object.LogicalOperation;
import org.albianj.persistence.object.RelationalOperator;

/**
 * 链式表达式过滤项
 * @author seapeak
 * @since v2.1
 */
public class FilterExpression implements IFilterExpression {

	private RelationalOperator _ro = RelationalOperator.Normal;
	private int _style = IChainExpression.STYLE_FILTER;
	
	private String _fieldName = null;
	private String _aliasName = null;
	private LogicalOperation _lo = LogicalOperation.Equal;
	private Object _value = null;
	private  Class<?> _cls = null;
	private boolean _isAddition = false;
	
	private List<IChainExpression> _chains = new ArrayList<>();
	
	public FilterExpression() {
		// TODO Auto-generated constructor stub
	}
	
	public FilterExpression(String fieldName, String aliasName, LogicalOperation lo, Object value){
		this._fieldName = fieldName;
		this._aliasName = aliasName;
		this._lo = lo;
		this._value = value;
		this._chains.add(this);
	}
	
	public FilterExpression(String fieldName, LogicalOperation lo, Object value){
		this._fieldName = fieldName;
		this._lo = lo;
		this._value = value;
		this._chains.add(this);
	}
	
	
	
	@Override
	public RelationalOperator getRelationalOperator() {
		// TODO Auto-generated method stub
		return this._ro;
	}

	@Override
	public void setRelationalOperator(RelationalOperator relationalOperator) {
		// TODO Auto-generated method stub
		this._ro = relationalOperator;
	}

	@Override
	public int getStyle() {
		// TODO Auto-generated method stub
		return this._style;
	}

	@Override
	public void setStyle(int style) {
		// TODO Auto-generated method stub
		this._style = style;
	}

	@Override
	public IFilterExpression and(IFilterExpression fe) {
		fe.setRelationalOperator(RelationalOperator.And);
		_chains.add(fe);
		return this;
		// TODO Auto-generated method stub
//		fe.setRelationalOperator(RelationalOperator.And);
//		this._curr.setLeft(fe);
//		this._curr = fe;
//		return this;
	}

	@Override
	public IFilterExpression or(IFilterExpression fe) {
		// TODO Auto-generated method stub
		fe.setRelationalOperator(RelationalOperator.OR);
		_chains.add(fe);
		return this;
	}

	public IFilterExpression addAddition(IFilterExpression fe) {
		// TODO Auto-generated method stub
		fe.setRelationalOperator(RelationalOperator.Normal);
		fe.setAddition(true);
		_chains.add(fe);
		return this;
	}

	
	@Override
	public IFilterExpression and(String fieldName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression ce = new FilterExpression(fieldName, lo, value);
		this.and(ce);
		return this;
	}

	@Override
	public IFilterExpression and(String fieldName, String aliasName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression ce = new FilterExpression(fieldName,aliasName, lo, value);
		this.and(ce);
		return this;
	}

	@Override
	public IFilterExpression or(String fieldName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression ce = new FilterExpression(fieldName, lo, value);
		this.or(ce);
		return this;
	}

	@Override
	public IFilterExpression or(String fieldName, String aliasName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression ce = new FilterExpression(fieldName,aliasName, lo, value);
		this.and(ce);
		return this;
	}

	@Override
	public IFilterExpression addAddition(String fieldName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression ce = new FilterExpression(fieldName, lo, value);
		this.addAddition(ce);
		return this;
	}

	@Override
	public IFilterExpression addAddition(String fieldName, String aliasName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression ce = new FilterExpression(fieldName,aliasName, lo, value);
		this.addAddition(ce);
		return this;
	}

	@Override
	public IFilterExpression and(IFilterGroupExpression fge) {
		// TODO Auto-generated method stub
		fge.setStyle(STYLE_FILTER_GROUP);
		fge.setRelationalOperator(RelationalOperator.And);
		_chains.add(fge);
		return this;
	}

	@Override
	public IFilterExpression or(IFilterGroupExpression fge) {
		// TODO Auto-generated method stub
		fge.setStyle(STYLE_FILTER_GROUP);
		fge.setRelationalOperator(RelationalOperator.OR);
		_chains.add(fge);
		return this;
	}
	
	@Override
	public IFilterExpression add(String fieldName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression fe = new FilterExpression(fieldName, lo, value);
		this.add(fe);
		return this;
	}

	@Override
	public IFilterExpression add(String fieldName, String aliasName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression fe = new FilterExpression(fieldName,aliasName, lo, value);
		this.add(fe);
		return this;
	}

	public IFilterExpression add(IFilterExpression fe) {
		fe.setRelationalOperator(RelationalOperator.Normal);
		// TODO Auto-generated method stub
		this._chains.add(fe);
		return  this;
	}
	
	

	@Override
	public String getFieldName() {
		// TODO Auto-generated method stub
		return this._fieldName ;
	}

	@Override
	public void setFieldName(String fieldName) {
		// TODO Auto-generated method stub
		this._fieldName = fieldName;
	}

	@Override
	public Class<?> getFieldClass() {
		// TODO Auto-generated method stub
		return this._cls;
	}

	@Override
	public void setFieldClass(Class<?> cls) {
		// TODO Auto-generated method stub
		this._cls = cls;
	}

	@Override
	public LogicalOperation getLogicalOperation() {
		// TODO Auto-generated method stub
		return this._lo;
	}

	@Override
	public void setLogicalOperation(LogicalOperation logicalOperation) {
		// TODO Auto-generated method stub
		this._lo = logicalOperation;
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return this._value;
	}

	@Override
	public void setValue(Object value) {
		// TODO Auto-generated method stub
		this._value = value;
	}

	@Override
	public boolean isAddition() {
		// TODO Auto-generated method stub
		return this._isAddition;
	}

	@Override
	public void setAddition(boolean isAddition) {
		// TODO Auto-generated method stub
		this._isAddition = isAddition;
	}

	@Override
	public String getAliasName() {
		// TODO Auto-generated method stub
		return this._aliasName;
	}

	@Override
	public void setAliasName(String an) {
		// TODO Auto-generated method stub
		this._aliasName = an;
	}
	
	public List<IChainExpression> getChainExpression(){
		return this._chains;
	}
	
}
