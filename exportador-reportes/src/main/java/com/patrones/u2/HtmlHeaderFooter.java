package com.patrones.u2;

public class HtmlHeaderFooter implements ReportHeaderFooter {
    @Override
    public String renderHeader(String institutionName) {
        return "[HTML:encabezado] <header>" + institutionName + " — Portal de Estudiantes</header>";
    }

    @Override
    public String renderFooter(int pageNumber) {
        return "[HTML:pie] <footer>Vista " + pageNumber + " — generado dinamicamente</footer>";
    }
}
