/**

 * WeekDaysEntity.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.application.meeting.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.util.helpers.Constants;

/**
 * This class encapsulate the weekDay
 */
public class WeekDaysEntity extends MasterDataEntity {

	private Short workDay;
	
	private Short startOfWeek;
	
	public WeekDaysEntity() {
	}

	public WeekDaysEntity(WeekDay weekDay) {
		super(weekDay.getValue());
		this.workDay = null;
	}
	
	public boolean isWorkingDay(){
		return (workDay!=null&& workDay.equals(YesNoFlag.NO.getValue())) ? false : true;
	}
	
	public boolean isStartOfFiscalWeek(){
		return (startOfWeek!=null&& startOfWeek.equals(Constants.YES));
	}
}
