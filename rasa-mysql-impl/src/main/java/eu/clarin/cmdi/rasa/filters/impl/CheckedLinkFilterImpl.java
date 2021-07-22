package eu.clarin.cmdi.rasa.filters.impl;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.stream.Collectors;

import eu.clarin.cmdi.rasa.filters.CheckedLinkFilter;
import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;

public class CheckedLinkFilterImpl extends AbstractFilter implements CheckedLinkFilter {
	
	public CheckedLinkFilterImpl() {
		super.from = "status s";
		super.join.add("INNER JOIN url u ON s.url_id=u.id");
	}
	

	@Override
	public CheckedLinkFilter setUrlIs(String url) {

		super.where.put("u.url", "u.url = '" + url +"'");
		return this;
	}

	@Override
	public CheckedLinkFilter setUrlIn(String... urls) {
		super.where.put("u.url", "u.url IN " + Arrays.stream(urls).collect(Collectors.joining("', '", "('", "')")));
		return this;
	}
	

	@Override
	public CheckedLinkFilter setStatusIs(Integer status) {
		super.where.put("s.statusCode", "s.statusCode = " + status);
		return this;
	}

	@Override
	public CheckedLinkFilter setStatusBetween(Integer statusFrom, Integer statusTo) {
		super.where.put("s.statusCode", "s.statusCode >= " + statusFrom + " AND s.statusCode <= " + statusTo);
		return this;
	}

	@Override
	public CheckedLinkFilter setCheckedBetween(Timestamp checkedAfter, Timestamp checkedBefore) {
		super.where.put("s.checkingDate", "s.checkingDate BETWEEN '" + checkedAfter + "' AND '" + checkedBefore + "'");
		
		return this;
	}

	@Override
	public CheckedLinkFilter setProviderGroupIs(String providerGroup) {
		if(!providerGroup.equals("Overall")) {
	      super.join.add("INNER JOIN url_context uc ON u.id=uc.url_id");
	      super.join.add("INNER JOIN context c ON uc.context_id=c.id");
	      super.join.add("INNER JOIN providerGroup p ON c.providerGroup_id=p.id");
	      super.where.put("p.name", "p.name = '" + providerGroup + "'");
		}
		return this;
	}

	@Override
	public CheckedLinkFilter setRecordIs(String record) {
      super.join.add("INNER JOIN url_context uc ON u.id=uc.url_id");
      super.join.add("INNER JOIN context c ON uc.context_id=c.id");
		super.where.put("c.record", "c.record = '" + record + "'");

		return this;
	}
	
	@Override
	public CheckedLinkFilter setCategoryIs(Category category) {
		this.where.put("s.category", "s.category = '" + category.name() + "'");
		
		return this;
	}

	@Override
	public CheckedLinkFilter setCategoryIn(Category... categories) {
		
		this.where.put("s.category", "s.category IN ("  + Arrays.stream(categories).map(Category::name).collect(Collectors.joining("', '", "'", "'")) + ")");
		
		return this;
	}
	
	@Override
	public CheckedLinkFilter setIngestionDateIs(Timestamp ingestionDate) {
      super.join.add("INNER JOIN url_context uc ON u.id=uc.url_id");
		super.where.put("uc.ingestionDate", "uc.ingestionDate = '" + ingestionDate + "'");
		
		return this; 
	}

	@Override
	public CheckedLinkFilter setLimit(int offset, int limit) {
		this.limit = offset + ", " + limit;
		
		return this;
	}


	@Override
	public CheckedLinkFilter setIsActive(boolean active) {
      super.join.add("INNER JOIN url_context uc ON u.id=uc.url_id");
		super.where.put("uc.active", "uc.active = " + active);
		
		return this;
	}


  @Override
  public CheckedLinkFilter setDoOrder(boolean doOrder) {
     if (doOrder) {
        this.join.add("LEFT JOIN status s ON u.id=s.url_id");
        this.orderBy = "s.checkingDate";
     } else {
        this.join.remove("LEFT JOIN status s ON u.id=s.url_id");
        this.orderBy = null;
     }
    return this;
  }
}
