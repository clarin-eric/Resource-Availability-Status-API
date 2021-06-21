package eu.clarin.cmdi.rasa.filters.impl;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.stream.Collectors;

import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;

public class CheckedLinkFilterImpl extends AbstractFilter implements CheckedLinkFilter {
	
	public CheckedLinkFilterImpl() {
		super.from.add("status s");
		super.from.add("link l");
		
		super.condition.put("s-l","s.link_id=l.id");
	}
	

	@Override
	public CheckedLinkFilter setUrlIs(String url) {

		super.condition.put("l.url_hash", "l.url_hash = MD5('" + url +"')");
		return this;
	}

	@Override
	public CheckedLinkFilter setUrlIn(String... urls) {
		super.condition.put("l.url_hash", "l.url_hash IN(" + Arrays.stream(urls).collect(Collectors.joining("'), MD5('", "MD5('", "')")) + ")");
		return this;
	}
	

	@Override
	public CheckedLinkFilter setStatusIs(Integer status) {
		super.condition.put("s.statusCode", "s.statusCode = " + status);
		return this;
	}

	@Override
	public CheckedLinkFilter setStatusBetween(Integer statusFrom, Integer statusTo) {
		super.condition.put("s.statusCode", "s.statusCode >= " + statusFrom + " AND s.statusCode <= " + statusTo);
		return this;
	}

	@Override
	public CheckedLinkFilter setCheckedBetween(Timestamp checkedAfter, Timestamp checkedBefore) {
		super.condition.put("s.checkingDate", "s.checkingDate > '" + checkedAfter + "' AND s.checkingDate < '" + checkedBefore + "'");
		
		return this;
	}

	@Override
	public CheckedLinkFilter setProviderGroupIs(String providerGroup) {
		if(!providerGroup.equals("Overall")) {
			super.from.add("providerGroup p");
			super.from.add("context c");
			super.from.add("link_context lc");
			super.condition.put("p.hash_name", "p.name_hash = MD5('" + providerGroup + "')");
			super.condition.put("c-p", "c.providerGroup_id=p.id");
			super.condition.put("lc-c","lc.context_id=c.id");
			super.condition.put("l-lc", "l.id=lc.link_id");
		}
		return this;
	}

	@Override
	public CheckedLinkFilter setRecordIs(String record) {
		super.from.add("context c");
		super.from.add("link_context lc");
		super.condition.put("c.record", "c.record = '" + record + "'");
		super.condition.put("lc-c","lc.context_id=c.id");
		super.condition.put("l-lc", "l.id=lc.link_id");
		
		return this;
	}
	
	@Override
	public CheckedLinkFilter setCategoryIs(Category category) {
		this.condition.put("s.category", "s.category = '" + category.name() + "'");
		
		return this;
	}

	@Override
	public CheckedLinkFilter setCategoryIn(Category... categories) {
		
		this.condition.put("s.category", "s.category IN ("  + Arrays.stream(categories).map(Category::name).collect(Collectors.joining("', '", "'", "'")) + ")");
		
		return this;
	}
	
	@Override
	public CheckedLinkFilter setIngestionDateIs(Timestamp ingestionDate) {
		super.from.add("link_context lc");
		super.condition.put("lc.ingestionDate", "lc.ingestionDate = '" + ingestionDate + "'");
		super.condition.put("l-lc", "l.id=lc.link_id");
		
		return this; 
	}

	@Override
	public CheckedLinkFilter setLimit(int offset, int limit) {
		this.limit = " LIMIT " + offset + ", " + limit;
		return this;
	}
}
