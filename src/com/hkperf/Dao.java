package com.hkperf;

import org.hibernate.*;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.hkperf.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Dao {
	HibernateTemplate hibernateTemplate;
	public void setSessionFactory(SessionFactory factory) {
		hibernateTemplate = new HibernateTemplate(factory);
	}

	public void saveResponse(Response r) {
		hibernateTemplate.saveOrUpdate(r);
	}

	public void saveResponseDetails(ResponseDetails rd ) {
		hibernateTemplate.save(rd);
	}
	public void saveEnumUrl(EnumUrl eu) {
		hibernateTemplate.save(eu);
	}
	public void saveEmployee(ResponseViewType rvt) {
		hibernateTemplate.save(rvt);
	}
	public int  getRequestId(String testId) {
		DetachedCriteria crit = DetachedCriteria.forClass(Response.class);
		crit.add(Restrictions.eq("testId", testId));
		List list = hibernateTemplate.findByCriteria(crit);
		Response response = (Response)list.get(0);
		return  response.getId();
	}
	public int getWebSiteMappingId(String url){
		DetachedCriteria crit = DetachedCriteria.forClass(EnumUrl.class);
		crit.add(Restrictions.like("url",( "%"+url+"%")));
		List list = hibernateTemplate.findByCriteria(crit);
		EnumUrl enumUrl = (EnumUrl)list.get(0);
		return (Integer)enumUrl.getId();
	}
}
