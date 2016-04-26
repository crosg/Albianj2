package org.albianj.persistence.object.filter;

import java.util.ArrayList;
import java.util.List;

import org.albianj.persistence.object.LogicalOperation;
import org.albianj.persistence.object.RelationalOperator;

/**
 * 链式表达式组
 * @author seapeak
 * @since v2.1
 *
 */
public class FilterGroupExpression implements IFilterGroupExpression {

	private RelationalOperator _ro = RelationalOperator.Normal;
	private int _style = IChainExpression.STYLE_FILTER_GROUP;
	
	private List<IChainExpression> _chains = new ArrayList<>();
	
	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#getRelationalOperator()
	 */
	@Override
	public RelationalOperator getRelationalOperator() {
		// TODO Auto-generated method stub
		return this._ro;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#setRelationalOperator(org.albianj.persistence.object.RelationalOperator)
	 */
	@Override
	public void setRelationalOperator(RelationalOperator relationalOperator) {
		// TODO Auto-generated method stub
		this._ro = relationalOperator;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#getStyle()
	 */
	@Override
	public int getStyle() {
		// TODO Auto-generated method stub
		return this._style;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#setStyle(int)
	 */
	@Override
	public void setStyle(int style) {
		// TODO Auto-generated method stub
		this._style = style;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#and(org.albianj.persistence.object.filter.IFilterExpression)
	 */
	@Override
	public IFilterGroupExpression and(IFilterExpression fe) {
		// TODO Auto-generated method stub
		fe.setRelationalOperator(RelationalOperator.And);
		this._chains.add(fe);
		return  this;
		
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#or(org.albianj.persistence.object.filter.IFilterExpression)
	 */
	@Override
	public IFilterGroupExpression or(IFilterExpression fe) {
		// TODO Auto-generated method stub
		fe.setRelationalOperator(RelationalOperator.OR);
		this._chains.add(fe);
		return  this;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#addAddition(org.albianj.persistence.object.filter.IFilterExpression)
	 */
	@Override
	public IFilterGroupExpression addAddition(IFilterExpression fe) {
		// TODO Auto-generated method stub
		fe.setRelationalOperator(RelationalOperator.Normal);
		this._chains.add(fe);
		return  this;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#and(java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
	 */
	@Override
	public IFilterGroupExpression and(String fieldName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression fe = new FilterExpression(fieldName, lo, value);
		this.and(fe);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#and(java.lang.String, java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
	 */
	@Override
	public IFilterGroupExpression and(String fieldName, String aliasName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression fe = new FilterExpression(fieldName,aliasName, lo, value);
		this.and(fe);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#or(java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
	 */
	@Override
	public IFilterGroupExpression or(String fieldName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression fe = new FilterExpression(fieldName, lo, value);
		this.or(fe);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#or(java.lang.String, java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
	 */
	@Override
	public IFilterGroupExpression or(String fieldName, String aliasName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression fe = new FilterExpression(fieldName,aliasName, lo, value);
		this.or(fe);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#addAddition(java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
	 */
	@Override
	public IFilterGroupExpression addAddition(String fieldName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression fe = new FilterExpression(fieldName, lo, value);
		this.addAddition(fe);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#addAddition(java.lang.String, java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
	 */
	@Override
	public IFilterGroupExpression addAddition(String fieldName, String aliasName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression fe = new FilterExpression(fieldName,aliasName, lo, value);
		this.addAddition(fe);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#and(org.albianj.persistence.object.filter.IFilterGroupExpression)
	 */
	@Override
	public IFilterGroupExpression and(IFilterGroupExpression fge) {
		// TODO Auto-generated method stub
		fge.setRelationalOperator(RelationalOperator.And);
		this._chains.add(fge);
		return  this;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#or(org.albianj.persistence.object.filter.IFilterGroupExpression)
	 */
	@Override
	public IFilterGroupExpression or(IFilterGroupExpression fge) {
		// TODO Auto-generated method stub
		fge.setRelationalOperator(RelationalOperator.OR);
		this._chains.add(fge);
		return  this;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#add(org.albianj.persistence.object.filter.IFilterExpression)
	 */
	@Override
	public IFilterGroupExpression add(IFilterExpression fe) {
		fe.setRelationalOperator(RelationalOperator.Normal);
		// TODO Auto-generated method stub
		this._chains.add(fe);
		return  this;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IFilterGroupExpression#addFilterGroup(org.albianj.persistence.object.filter.IFilterGroupExpression)
	 */
	@Override
	public IFilterGroupExpression addFilterGroup(IFilterGroupExpression fge) {
		fge.setRelationalOperator(RelationalOperator.Normal);
		// TODO Auto-generated method stub
		this._chains.add(fge);
		return  this;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#add(java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
	 */
	@Override
	public IFilterGroupExpression add(String fieldName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression fe = new FilterExpression(fieldName, lo, value);
		this.add(fe);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#add(java.lang.String, java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
	 */
	@Override
	public IFilterGroupExpression add(String fieldName, String aliasName, LogicalOperation lo, Object value) {
		// TODO Auto-generated method stub
		IFilterExpression fe = new FilterExpression(fieldName,aliasName, lo, value);
		this.add(fe);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.albianj.persistence.object.filter.IChainExpression#getChainExpression()
	 */
	public List<IChainExpression> getChainExpression(){
		return this._chains;
	}
	

}
