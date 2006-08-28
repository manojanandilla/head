package org.mifos.framework.components.cronjob.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;

import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.cronjobs.helpers.ApplyCustomerFeeHelper;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerFeeHelper extends MifosTestCase{	
	
	private CustomerBO center;	
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() {		
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();

	}
	public void testExecute() throws Exception{
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("center1_Active_test", Short.valueOf("13"), "1.4", meeting, new Date(System.currentTimeMillis()));
		Set<AccountFeesEntity> accountFeeSet = center.getCustomerAccount().getAccountFees();
		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee("Training_Fee", FeeCategory.LOAN, "100", MeetingFrequency.WEEKLY,Short.valueOf("2"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center.getCustomerAccount(),trainingFee,((AmountFeeBO)trainingFee).getFeeAmount().getAmountDoubleValue());
		accountFeeSet.add(accountPeriodicFee);
		Date currentDate=DateUtils.getCurrentDateWithoutTimeStamp();
		Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        calendar.add(calendar.WEEK_OF_MONTH,-1);
        Date lastAppliedFeeDate = calendar.getTime();
		assertEquals(2,accountFeeSet.size());	
		for (Iterator iter = accountFeeSet.iterator(); iter.hasNext();) {
			AccountFeesEntity accountFeesEntity = (AccountFeesEntity) iter.next();
			accountFeesEntity.setLastAppliedDate(lastAppliedFeeDate);			
		}
		TestObjectFactory.updateObject(center);
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO)TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());		
		ApplyCustomerFeeHelper customerFeeHelper = new ApplyCustomerFeeHelper();
		customerFeeHelper.execute(currentDate.getTime());
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO)TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		Set<AccountFeesEntity> periodicFeeSet = center.getCustomerAccount().getAccountFees();
		for (AccountFeesEntity periodicFees : periodicFeeSet) {
			if(periodicFees.getFees().getFeeName().equalsIgnoreCase("Training_Fee"))
				assertEquals(lastAppliedFeeDate,DateUtils.getDateWithoutTimeStamp(periodicFees.getLastAppliedDate().getTime()));
			else
				assertEquals(currentDate,DateUtils.getDateWithoutTimeStamp(periodicFees.getLastAppliedDate().getTime()));
		}
		
		}
}
