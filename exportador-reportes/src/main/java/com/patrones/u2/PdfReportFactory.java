package com.patrones.u2;

public class PdfReportFactory implements ReportFormatFactory {
    @Override
    public ReportBody createBody() { 
        return new PdfReportBody(); 
    }

    @Override
    public ReportHeaderFooter createHeaderFooter() { 
        return new PdfHeaderFooter(); 
    }
}