package eu.clarin.cmdi.rasa.filters.impl;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractFilter {
	protected String from;
	protected Set<String> join = new LinkedHashSet<String>();
	protected Map<String, String> where = new HashMap<String, String>();
	protected String orderBy;
	protected String limit;

	public String toString() {
		return "FROM " + from
		      + (join.size() == 0?"": join.stream().collect(Collectors.joining(" ", " ", "")))
				+ (where.size() == 0? "":" WHERE " + where.values().stream().collect(Collectors.joining(" AND "))) 
				+ (orderBy == null?"":" ORDER BY " + orderBy)
				+ (limit == null?"": " LIMIT " + limit);
	}
}
