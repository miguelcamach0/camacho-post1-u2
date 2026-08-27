package com.patrones.u2;

public class ExcelReportFactory implements ReportFormatFactory {
    @Override
    public ReportBody createBody() { 
        return new ExcelReportBody(); 
    }

    @Override
    public ReportHeaderFooter createHeaderFooter() { 
        return new ExcelHeaderFooter(); 
    }
}
