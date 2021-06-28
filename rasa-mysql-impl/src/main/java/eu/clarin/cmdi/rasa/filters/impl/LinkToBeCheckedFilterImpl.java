package eu.clarin.cmdi.rasa.filters.impl;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.stream.Collectors;

import eu.clarin.cmdi.rasa.filters.LinkToBeCheckedFilter;

public class LinkToBeCheckedFilterImpl extends AbstractFilter implements LinkToBeCheckedFilter {
	
	public LinkToBeCheckedFilterImpl() {
		super.from.add("url u");
	}

	@Override
	public LinkToBeCheckedFilter setUrlIs(String url) {
		super.condition.put("u.url_hash", "u.url_hash = MD5('" + url +"')");
		return this;
	}

	@Override
	public LinkToBeCheckedFilter setUrlIn(String... urls) {
		super.condition.put("u.url_hash", "u.url_hash IN(" + Arrays.stream(urls).collect(Collectors.joining("'), MD5('", "MD5('", "')")) + ")");
		return this;
	}

	@Override
	public LinkToBeCheckedFilter setProviderGroupIs(String providerGroup) {
		super.from.add("providerGroup p");
		super.from.add("context c");
		super.from.add("url_context uc");
		super.condition.put("p.name_hash", "p.name_hash = MD5('" + providerGroup + "')");
		super.condition.put("c-p","c.providerGroup_id=p.id");
		super.condition.put("uc-c","uc.context_id=c.id");
		super.condition.put("u-uc","u.id=uc.url_id");
		return this;
	}

	@Override
	public LinkToBeCheckedFilter setRecordIs(String record) {
		super.from.add("context c");
		super.from.add("url_context uc");
		super.condition.put("c.record", "c.record = '" + record + "'");
		super.condition.put("uc-c","uc.context_id=c.id");
		super.condition.put("u-uc","u.id=uc.url_id");
		
		return this;
	}
	
	@Override
	public LinkToBeCheckedFilter setIngestionDateIs(Timestamp ingestionDate) {
		super.from.add("url_context uc");
		super.condition.put("uc.ingestionDate", "uc.ingestionDate = '" + ingestionDate + "'");
		super.condition.put("u-uc","l.id=uc.url_id");
		
		return this; 
	}

	@Override
	public LinkToBeCheckedFilter setLimit(int offset, int limit) {
		this.limit = " LIMIT " + offset + ", " + limit;
		return this;
	}
	
	@Override
	public LinkToBeCheckedFilter setIsActive(boolean active) {
		super.from.add("url_context uc");
		super.condition.put("uc.active", "uc.active = " + active);
		super.condition.put("u-uc", "u.id=uc.url_id");
		
		return this;
	}


}
