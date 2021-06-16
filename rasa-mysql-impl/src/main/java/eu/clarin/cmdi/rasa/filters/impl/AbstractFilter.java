package eu.clarin.cmdi.rasa.filters.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractFilter {
	protected Set<String> from = new HashSet<String>();
	protected Map<String, String> condition = new HashMap<String, String>();
	protected String limit = "";

	public String toString() {
		return "FROM " 
				+ this.from.stream().collect(Collectors.joining(", ")) 
				+ (condition.size() > 0? " WHERE ":"") 
				+ condition.values().stream().collect(Collectors.joining(" AND ")) 
				+ limit;
	}
}
