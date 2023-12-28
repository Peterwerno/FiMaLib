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
package org.fimalib.calc.cal;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author peter
 */
public class CalendarTest {
    
    public CalendarTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    static long[] easterTimes = {
        323827200000L,
        356486400000L,
        387331200000L,
        418176000000L,
        451440000000L,
        481680000000L,
        512524800000L,
        545788800000L,
        576028800000L,
        606873600000L,
        640137600000L,
        670377600000L,
        703641600000L,
        734486400000L,
        765331200000L,
        797990400000L,
        828835200000L,
        859680000000L,
        892339200000L,
        923184000000L,
        956448000000L,
        987292800000L,
        1017532800000L,
        1050796800000L,
        1081641600000L,
        1111881600000L,
        1145145600000L,
        1175990400000L,
        1206230400000L,
        1239494400000L,
        1270339200000L,
        1303603200000L,
        1333843200000L,
        1364688000000L,
        1397952000000L,
        1428192000000L,
        1459036800000L,
        1492300800000L,
        1522540800000L,
        1555804800000L,
        1586649600000L,
        1617494400000L,
        1650153600000L,
        1680998400000L,
        1711843200000L,
        1745107200000L,
        1775347200000L,
        1806192000000L,
        1839456000000L,
        1869696000000L,
        1902960000000L,
        1933804800000L,
        1964044800000L,
        1997308800000L,
        2028153600000L,
        2058393600000L,
        2091657600000L,
        2122502400000L,
        2155766400000L,
        2186006400000L,
        2216851200000L,
        2250115200000L,
        2280355200000L,
        2311200000000L,
        2344464000000L,
        2375308800000L,
        2405548800000L,
        2438812800000L,
        2469657600000L,
        2502316800000L,
        2533161600000L,
        2564006400000L,
        2597270400000L,
        2627510400000L,
        2658355200000L,
        2691619200000L,
        2721859200000L,
        2755123200000L,
        2785968000000L,
        2816208000000L,
        2849472000000L,
        2880316800000L,
        2910556800000L,
        2943820800000L,
        2974665600000L,
        3005510400000L,
        3038169600000L,
        3069014400000L,
        3102278400000L,
        3133123200000L,
        3163363200000L,
        3196627200000L,
        3227472000000L,
        3257712000000L,
        3290976000000L,
        3321820800000L,
        3354480000000L,
        3385324800000L,
        3416169600000L,
        3449433600000L,
        3479673600000L,
        3510518400000L,
        3543782400000L,
        3574022400000L,
        3604867200000L,
        3638131200000L,
        3668371200000L,
        3701635200000L,
        3732480000000L,
        3763324800000L,
        3795984000000L,
        3826828800000L,
        3857673600000L,
        3890332800000L,
        3921177600000L,
        3954441600000L,
        3985286400000L,
        4015526400000L,
        4048790400000L,
        4079635200000L
    };

    @Test
    public void easterFunctionTest() {
        System.out.println("Testing easter calculation from 1980-2099");
        Calendar cal = new GregorianCalendar(Locale.UK, TimeZone.getTimeZone("GMT"));
        
        for(int i=0; i<easterTimes.length; i++) {
            Date date = cal.getEaster(i + 1980);
            
            assertEquals("Easter " + (i+1980) + " not calculated correctly", easterTimes[i], date.getTime());
        }
        System.out.println("Passed");
    }
}
