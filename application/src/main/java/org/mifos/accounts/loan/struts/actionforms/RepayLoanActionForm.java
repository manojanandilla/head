/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.accounts.loan.struts.actionforms;

import java.sql.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.UserContext;

import static org.mifos.framework.util.helpers.DateUtils.dateFallsBeforeDate;
import static org.mifos.framework.util.helpers.DateUtils.getDateAsSentFromBrowser;

public class RepayLoanActionForm extends BaseActionForm {

    private String accountId;
    private String amount;
    private String receiptNumber;
    private String receiptDate;
    private String dateOfPayment;
    private String paymentTypeId;
    private boolean waiverInterest;
    private java.util.Date lastPaymentDate;

    public RepayLoanActionForm() {
        waiverInterest = true;
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(String paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDateOfPayment() {
        return dateOfPayment;
    }

    public void setDateOfPayment(String dateOfPayment) {
        this.dateOfPayment = dateOfPayment;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        String method = request.getParameter("method");
        ActionErrors errors = new ActionErrors();
        Locale userLocale = getUserLocale(request);
        ResourceBundle resources = getResourceBundle(userLocale);
        if (!method.equals("loadRepayment") && !method.equals("makeRepayment") && !method.equals("validate")
                && !method.equals("previous") && !method.equals("cancel")) {
            errors.add(super.validate(mapping, request));
            validateDateOfPayment(errors, resources);
        }

        if (!errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", method);
        }

        return errors;
    }

    public boolean isWaiverInterest() {
        return waiverInterest;
    }

    public void setWaiverInterest(boolean waiverInterest) {
        this.waiverInterest = waiverInterest;
    }

    public Date getReceiptDateValue(Locale preferredLocale) throws InvalidDateException {
        return new Date(DateUtils.getLocaleDate(preferredLocale, receiptDate).getTime());
    }

    public Date getDateOfPaymentValue(Locale preferredLocale) throws InvalidDateException {
        return new Date(DateUtils.getLocaleDate(preferredLocale, dateOfPayment).getTime());
    }

    protected Locale getUserLocale(HttpServletRequest request) {
        Locale locale = null;
        HttpSession session = request.getSession();
        if (session != null) {
            UserContext userContext = (UserContext) session.getAttribute(LoginConstants.USERCONTEXT);
            if (null != userContext) {
                locale = userContext.getCurrentLocale();

            }
        }
        return locale;
    }

    ResourceBundle getResourceBundle(Locale userLocale) {
        return ResourceBundle.getBundle(FilePaths.ACCOUNTS_UI_RESOURCE_PROPERTYFILE,
                userLocale);
    }

    private void validateDateOfPayment(ActionErrors errors, ResourceBundle resources) {
        String fieldName = "accounts.date_of_trxn";
        ActionErrors validationErrors = validateDate(getDateOfPayment(), resources.getString(fieldName));
        if (null != validationErrors && !validationErrors.isEmpty()) {
            errors.add(validationErrors);
        }
        validationErrors = validatePaymentDate(getDateOfPayment(), resources.getString(fieldName));
        if (null != validationErrors && !validationErrors.isEmpty()) {
            errors.add(validationErrors);
        }
    }

    protected ActionErrors validateDate(String date, String fieldName) {
        ActionErrors errors = null;
        java.sql.Date sqlDate = null;
        if (date != null && !date.equals("")) {
            try {
                sqlDate = getDateAsSentFromBrowser(date);
                if (DateUtils.whichDirection(sqlDate) > 0) {
                    errors = new ActionErrors();
                    errors.add(AccountConstants.ERROR_FUTUREDATE, new ActionMessage(AccountConstants.ERROR_FUTUREDATE,
                            fieldName));
                }
            } catch (InvalidDateException ide) {
                errors = new ActionErrors();
                errors.add(AccountConstants.ERROR_INVALIDDATE, new ActionMessage(AccountConstants.ERROR_INVALIDDATE,
                        fieldName));
            }
        } else {
            errors = new ActionErrors();
            errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(AccountConstants.ERROR_MANDATORY,
                            fieldName));
        }
        return errors;
    }

    public ActionErrors validatePaymentDate(String transactionDate, String fieldName) {
        ActionErrors errors = null;
        try {
            if (lastPaymentDate != null && dateFallsBeforeDate(getDateAsSentFromBrowser(transactionDate), lastPaymentDate)) {
                errors = new ActionErrors();
                errors.add(AccountConstants.ERROR_PAYMENT_DATE_BEFORE_LAST_PAYMENT,
                        new ActionMessage(AccountConstants.ERROR_PAYMENT_DATE_BEFORE_LAST_PAYMENT,
                                fieldName));
            }
        } catch (InvalidDateException ide) {
            errors = new ActionErrors();
            errors.add(AccountConstants.ERROR_INVALIDDATE, new ActionMessage(AccountConstants.ERROR_INVALIDDATE,
                    fieldName));
        }
        return errors;
    }

    public void setLastPaymentDate(java.util.Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }
}
