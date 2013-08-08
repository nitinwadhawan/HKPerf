package com.hkperf;

import org.hibernate.*;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.Date;
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
	public List findByQuery(String queryString) {
		return hibernateTemplate.find(queryString)   ;
	}
}
