/*
 * Copyright (C) 2023 peter.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.fimalib.instrument;

import java.util.Date;

/**
 * The CashFlow class implements a single cash flow information.
 * 
 * @author Peter Werno
 */
public class CashFlow extends Instrument {
    Date cashFlowDate;
    double cashFlow;
    Currency currency;
    
    public CashFlow(Date cashFlowDate, double cashFlow, Currency currency) {
        this.cashFlowDate = cashFlowDate;
        this.cashFlow = cashFlow;
        this.currency = currency;
    }

    public Date getCashFlowDate() {
        return cashFlowDate;
    }

    public double getCashFlow() {
        return cashFlow;
    }

    public Currency getCurrency() {
        return currency;
    }
}
