package com.nanobnk.epayment.reporting.utils;

import java.util.HashMap;
import java.util.Map;

public class PaidNoticeAppName {

    private Map<Integer, String> reportNameMap = new HashMap();
    private static PaidNoticeAppName paidNoticeApp = null;

    private PaidNoticeAppName(){

        reportNameMap.put(101, "paid-customs-list-report");
        reportNameMap.put(102, "ontime-paid-customs-list-report");
        reportNameMap.put(103, "delay-paid-customs-list-report");
        reportNameMap.put(104, "type-paid-customs-list-report");
        reportNameMap.put(105, "participant-paid-customs-list-report");
        reportNameMap.put(106, "benef-paid-customs-list-report");
        reportNameMap.put(107, "taxpayer-paid-customs-list-report");
        reportNameMap.put(108, "taxpayer-ontime-paid-customs-list-report");
        reportNameMap.put(109, "taxpayer-delay-paid-customs-list-report");
        reportNameMap.put(110, "office-paid-customs-list-report");
        reportNameMap.put(111, "paycat-paid-customs-list-report");
        reportNameMap.put(112, "office-dectype-paid-customs-list-report");
        reportNameMap.put(113, "paymeth-paid-customs-list-report");
        reportNameMap.put(114, "cda-paid-customs-list-report");
        reportNameMap.put(115, "participant-reconciliation-report");
        reportNameMap.put(116, "payment-audit-report");
        reportNameMap.put(117, "provider-paid-customs-list-report");

    }
    
    public static PaidNoticeAppName getInstance(){
        if(paidNoticeApp==null)
            paidNoticeApp = new PaidNoticeAppName();
        return paidNoticeApp;

    }

    public Map<Integer, String> getReportNameMap() {
        return reportNameMap;
    }
}
