/**
 * 
 */
package org.mifos.application.accounts.persistence;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.TestAccount;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.CustomerAccount;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 * @author krishankg
 *
 */
public class TestAccountPersistence extends TestAccount {			
	
	private AccountPersistence accountPersistence=new AccountPersistence();
	
	public void testSuccessGetNextInstallmentList(){
		List<AccountActionDateEntity> installmentIdList=null;
		installmentIdList = accountBO.getApplicableIdsForFutureInstallments();
		assertTrue(true);
	}
	
	
	public void testSuccessLoadBusinessObject(){
		AccountBO accountObject=null;
		try {
			accountObject = accountPersistence.loadBusinessObject(accountBO.getAccountId());
			assertTrue(true);
		} catch (PersistenceException e) {
			assertTrue(false);
		}
		
	}
	
	
	public void testFailureLoadBusinessObject(){
		try {
			accountPersistence.loadBusinessObject(null);
			assertTrue(false);
		} catch (PersistenceException e) {
			assertTrue(true);
		}
	}
	
	public void testGetAccountAction()throws Exception{
		AccountActionEntity accountaction = accountPersistence.getAccountAction(AccountConstants.ACTION_SAVINGS_INTEREST_POSTING);
		assertNotNull(accountaction);
	}
	
	public void testRetrieveCustomerAccountActionDetails() {
		assertNotNull(center.getCustomerAccount());
		List<AccountActionDateEntity> actionDates = accountPersistence.retrieveCustomerAccountActionDetails(center.getCustomerAccount().getAccountId(),new java.sql.Date(System.currentTimeMillis()));
		assertEquals("The size of the due insallments is ", actionDates.size(),1);
	}
	
	public void testOptionalAccountStates() throws Exception{
		assertEquals(Integer.valueOf(1).intValue(),accountPersistence.getAccountStates(Short.valueOf("0")).size());
	}
	
	public void testAccountStatesInUse() throws Exception{
		assertEquals(Integer.valueOf(17).intValue(),accountPersistence.getAccountStates(Short.valueOf("1")).size());
	}
	
	public void testGetAccountsWithTodaysInstallment() throws PersistenceException {				
		assertEquals(2,accountPersistence.getAccountsWithTodaysInstallment().size());		
	}

	public void testGetCustomerAccountsForFee(){
		
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"ClientPeridoicFee", FeeCategory.CENTER, "5", MeetingFrequency.WEEKLY, Short.valueOf("1"));
		AccountFeesEntity accountFee = new AccountFeesEntity(center.getCustomerAccount(),periodicFee,((AmountFeeBO)periodicFee).getFeeAmount().getAmountDoubleValue());
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		customerAccount.addAccountFees(accountFee);
		TestObjectFactory.updateObject( customerAccount);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		//check for the account fee 
		List accountList =accountPersistence.getCustomerAccountsForFee(periodicFee.getFeeId());
		assertNotNull(accountList);
		assertEquals(1,accountList.size());
		//get all objects again
		accountBO =(AccountBO) TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
		

	}
	
	public void testGetActiveCustomerAndSavingsAccounts() throws Exception{
		
		SavingsBO savingsBO =  TestObjectFactory.createSavingsAccount("12345678910", group,
				new Short("16"), new Date(), createSavingsOffering("qqqqq"), TestObjectFactory.getUserContext());
		List<AccountBO> customerAccounts = accountPersistence.getActiveCustomerAndSavingsAccounts();
		assertNotNull(customerAccounts);
		assertEquals(3,customerAccounts.size());
		TestObjectFactory.cleanUp(savingsBO);
	}
	
	private SavingsOfferingBO createSavingsOffering(String offeringName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(offeringName, Short
				.valueOf("2"), new Date(System.currentTimeMillis()), Short
				.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0, 200.0,
				Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
	}
}
