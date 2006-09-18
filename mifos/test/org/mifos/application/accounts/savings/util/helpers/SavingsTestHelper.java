package org.mifos.application.accounts.savings.util.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.scheduler.ScheduleDataIntf;
import org.mifos.framework.components.scheduler.SchedulerFactory;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.helpers.SchedulerHelper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsTestHelper {
	
	public AccountPaymentEntity createAccountPayment(AccountBO account,Money amount, Date paymentDate, PersonnelBO createdBy){
		return createAccountPayment(account,null, amount, paymentDate, createdBy);
	}

	public AccountPaymentEntity createAccountPayment(AccountBO account,Integer paymentId, Money amount, Date paymentDate, PersonnelBO createdBy){
		AccountPaymentEntity payment = new AccountPaymentEntity(account,amount,null,null,new PaymentTypeEntity(Short.valueOf("1")));
		payment.setCreatedBy(createdBy.getPersonnelId());
		payment.setCreatedDate(new Date());
		return payment;
	}
	
	public AccountPaymentEntity createAccountPaymentToPersist(AccountBO account,Money amount, Money balance,  Date trxnDate, Short accountAction, SavingsBO savingsObj,PersonnelBO createdBy, CustomerBO customer)throws Exception{
		AccountPaymentEntity payment =createAccountPayment(account,amount, new Date(),createdBy);
		payment.addAcountTrxn(createAccountTrxn(payment,null,amount, balance, trxnDate, trxnDate, null, accountAction,savingsObj, createdBy, customer));
		return payment;
	}
	
	public SavingsTrxnDetailEntity createAccountTrxn(
			AccountPaymentEntity paymentEntity, Short installmentId,
			Money amount, Money balance, Date trxnDate, Date dueDate,
			Integer id, Short accountAction, SavingsBO savingsObj,
			PersonnelBO createdBy, CustomerBO customer) throws Exception {
		SavingsTrxnDetailEntity trxn = new SavingsTrxnDetailEntity(
				paymentEntity, customer,
				(AccountActionEntity) new MasterPersistence()
						.getPersistentObject(AccountActionEntity.class,
								accountAction), amount, balance, createdBy,
				dueDate, trxnDate, installmentId, "");
		return trxn;
	}

	public SavingsTrxnDetailEntity createAccountTrxn(
			AccountPaymentEntity paymentEntity, Money amount, Money balance,
			Date trxnDate, Date dueDate, Short accountAction,
			SavingsBO savingsObj, PersonnelBO createdBy, CustomerBO customer,
			String comments, AccountTrxnEntity relatedTrxn) throws Exception {
		SavingsTrxnDetailEntity trxn = new SavingsTrxnDetailEntity(
				paymentEntity, (AccountActionEntity) new MasterPersistence()
						.getPersistentObject(AccountActionEntity.class,
								accountAction), amount, balance, createdBy,
				dueDate, trxnDate, comments, relatedTrxn);
		return trxn;
	}

	public SavingsTrxnDetailEntity createAccountTrxn(
			AccountPaymentEntity paymentEntity, Short installmentId,
			Money amount, Money balance, Date trxnDate, Date dueDate,
			Integer id, Short accountAction, SavingsBO savingsObj,
			PersonnelBO createdBy, CustomerBO customer, String comments)
			throws Exception {
		SavingsTrxnDetailEntity trxn = new SavingsTrxnDetailEntity(
				paymentEntity, customer,
				(AccountActionEntity) new MasterPersistence()
						.getPersistentObject(AccountActionEntity.class,
								accountAction), amount, balance, createdBy,
				dueDate, trxnDate, installmentId, comments);
		return trxn;
	}
	
	public CenterBO createCenter(){
		MeetingBO meeting = TestObjectFactory.getMeetingHelper(2,2,4);
		meeting.setMeetingStartDate(Calendar.getInstance());
		meeting.getMeetingDetails().getMeetingRecurrence().setDayNumber(new Short("1"));
		TestObjectFactory.createMeeting(meeting);
		return TestObjectFactory.createCenter("Center_Active_test", Short.valueOf("13"), "1.1", meeting,new Date(System.currentTimeMillis()));
	}
	
	public SavingsOfferingBO createSavingsOffering(String offeringName, String shortName){
		return createSavingsOffering(offeringName,shortName, Short.valueOf("1"),Short.valueOf("2"));
	}
	
	public SavingsOfferingBO createSavingsOffering(String offeringName, String shortName,Short depGLCode,Short intGLCode){
		return createSavingsOffering(offeringName,shortName, Short.valueOf("1"),Short.valueOf("2"),depGLCode,intGLCode);
	}
	
	public SavingsOfferingBO createSavingsOffering(String offeringName, String shortName,Short interestCalcType, Short savingsTypeId,Short depGLCode,Short intGLCode){
		MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(offeringName,shortName,Short.valueOf("2"),new Date(System.currentTimeMillis()),
				Short.valueOf("2"),300.0,Short.valueOf("1"),24.0,200.0,200.0,savingsTypeId, interestCalcType,meetingIntCalc,meetingIntPost,depGLCode,intGLCode);
	}
	
	public SavingsOfferingBO createSavingsOffering(String offeringName, Short interestCalcType, Short savingsTypeId){
		MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(offeringName,Short.valueOf("2"),new Date(System.currentTimeMillis()),
				Short.valueOf("2"),300.0,Short.valueOf("1"),24.0,200.0,200.0,savingsTypeId, interestCalcType,meetingIntCalc,meetingIntPost);
	}
	
	public SavingsBO createSavingsAccount(String globalAccountNum,SavingsOfferingBO savingsOffering, CustomerBO customer, short accountStateId, UserContext userContext){
		SavingsBO savings = TestObjectFactory.createSavingsAccount(globalAccountNum,customer, accountStateId ,new Date(),savingsOffering,userContext);
		savings.setUserContext(userContext);
		return savings;
	}
	
	public SavingsBO createSavingsAccount(SavingsOfferingBO savingsOffering, CustomerBO customer, Short accountState, UserContext userContext)throws Exception{
		SavingsBO savings = new SavingsBO(userContext);
		savings.setSavingsOffering(savingsOffering);
		savings.setCustomer(customer);
		savings.setRecommendedAmount(new Money(TestObjectFactory.getMFICurrency(), "500.0"));
		savings.setAccountState(new AccountStateEntity(accountState));
		savings.save();
		return savings;
	}
	
	public Date getDate(String date)throws ParseException{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		return df.parse(date);
	}
	
	public Date getDate(String date, int hr, int min)throws ParseException{
		Calendar cal = Calendar.getInstance(Configuration.getInstance().getSystemConfig().getMifosTimeZone());
		cal.setTime(getDate(date));
		cal.set(Calendar.HOUR,hr);
		cal.set(Calendar.MINUTE,min);
		return cal.getTime();
	}

	public AccountNotesEntity getAccountNotes(SavingsBO savingsBO){
		AccountNotesEntity notes = new AccountNotesEntity();
		notes.setAccount(savingsBO);
		notes.setComment("xxxxxxxxxxxx");
		notes.setCommentDate(new java.sql.Date(System.currentTimeMillis()));
		notes.setPersonnel(savingsBO.getPersonnel());
		return notes;
	}
	public AccountActionDateEntity createAccountActionDate(AccountBO account,short installmentId,Date dueDate,Date paymentDate,CustomerBO customer, Money deposit, Money depositPaid, PaymentStatus paymentStatus){
		SavingsScheduleEntity actionDate = new SavingsScheduleEntity(account,customer,installmentId,new java.sql.Date(dueDate.getTime()),paymentStatus,deposit);
		actionDate.setDepositPaid(depositPaid);
		if(paymentDate!=null)
			actionDate.setPaymentDate(new java.sql.Date(paymentDate.getTime()));
		return actionDate;
	}
	
//	public SchedulerIntf getScheduler(MeetingBO meeting, int dayNumber)throws Exception{		
//		Short recurrenceId = meeting.getMeetingDetails().getRecurrenceType().getRecurrenceId();
//		ScheduleDataIntf scheduleData=SchedulerFactory.getScheduleData(recurrenceId);
//		scheduleData.setDayNumber(dayNumber);
//		return SchedulerHelper.getScheduler(scheduleData , meeting);
//	}

	public MeetingBO getMeeting(String frequency, Short recurAfter, Short meetingTypeId)throws Exception{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		MeetingBO meeting =  TestObjectFactory.getMeeting(frequency,String.valueOf(recurAfter),Short.valueOf(meetingTypeId));
		Calendar cal = Calendar.getInstance(Configuration.getInstance().getSystemConfig().getMifosTimeZone());
		cal.setTime(df.parse("01/01/2006"));
		meeting.setMeetingStartDate(cal);		
		return meeting;
	}
	
	public SchedulerIntf getScheduler(MeetingBO meeting)throws Exception{		
		Short recurrenceId = meeting.getMeetingDetails().getRecurrenceType().getRecurrenceId();
		ScheduleDataIntf scheduleData=SchedulerFactory.getScheduleData(recurrenceId);
		return SchedulerHelper.getScheduler(scheduleData , meeting);
	}

	public MeetingBO getMeeting(RecurrenceType recurrenceType, Short dayNumber, Short weekDay, Short dayRank, Short recurAfter, MeetingType meetingType)throws Exception{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		MeetingBO meeting =  TestObjectFactory.getMeeting(recurrenceType,dayNumber, weekDay, dayRank, recurAfter, meetingType);
		Calendar cal = Calendar.getInstance(Configuration.getInstance().getSystemConfig().getMifosTimeZone());
		cal.setTime(df.parse("01/01/2006"));
		meeting.setMeetingStartDate(cal);	
		return meeting;
	}
}
