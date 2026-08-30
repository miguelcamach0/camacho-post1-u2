# camacho-post1-u2
Post-contenido — Exportación de reportes académicos con patrones creacionales justificados

# Post-contenido — Unidad 2: Patrones Creacionales

## Descripción
Repositorio del post-contenido de la Unidad 2 de Patrones de Diseño
de Software — Sexto Semestre. Un único proyecto Maven
(exportador-reportes/) que resuelve la exportación de reportes
académicos en múltiples formatos (Parte 1) y se extiende con
configuración compleja y evaluación de Singleton (Parte 2).

## Cómo ejecutar
$ cd exportador-reportes
$ mvn compile
$ mvn exec:java -Dexec.mainClass="com.patrones.u2.Main"

## Decisiones de diseño

### Decisión 1 — Factory Method vs. Abstract Factory (Parte 1)
Patrón elegido: Abstract Factory
Justificación: 

1. ¿El sistema crea un único tipo de producto que varía por formato, o varios productos relacionados que deben mantenerse coherentes entre sí dentro del mismo formato?

R// Para este caso, el sistema crea varios productos relacionados. Según el enunciado, cada acta de calificación está compuesta como mínimo, por dos partes: el cuerpo, que contiene la tabla de calificaciones; y el encabezado y pie de página, que contiene elementos institucionales como membrete, numeración, marca de agua o firma.

Estas dos partes en particular dependen del formato utilizado, ejemplo:

PDF → CuerpoPDF + EncabezadoPiePDF
Excel → CuerpoExcel + EncabezadoPieExcel
HTML → CuerpoHTML + EncabezadoPieHTML

Por lo tanto, no bastaría con crear solamente un producto que cambie de formato. Se necesitaría crear una familia de productos relacionados que permanezcan coherentes entre sí.

2. Al agregar el futuro formato CSV, ¿basta con agregar una nueva implementación de un único producto, o hay que agregar una familia completa de piezas relacionadas?

R// Habría que agregar una familia completa de piezas relacionadas, al incorporar CSV no sería suficiente crear solamente un nuevo tipo de cuerpo, se tendría que proporcionar sus piezas correspondientes a su formato, es decir:

CSV → CuerpoCSV + EncabezadoPieCSV

Quiere decir que CSV representa una nueva familia de productos relacionados. La solución debería permitir incorporar una CsvActaFactory que sea responsable de crear tanto el cuerpo como el encabezado/pie correspondientes a CSV.

3. ¿El riesgo real del problema es "se instancia la clase equivocada" o es "se mezclan piezas de familias distintas y el documento queda inconsistente"?

R// El riesgo real del problema es que se mezclen piezas de familias distintas y el documento final quede inconsistente. Esto se debe a que el sistema no solamente debe seleccionar un formato de salida, sino que también debe garantizar que tanto el cuerpo como el encabezado y pie de página pertenezcan al mismo formato. Por ejemplo, un cuerpo de excel debe utilizar su correspondiente encabezado y pie en Excel, y no con uno diseñado para PDF. 

Como riesgo principal, una implementación que no controle esta relación podría permitir combinaciones inválidas como cuerpo Excel + encabezado PDF, generando un acta incoherente. Por esta razón, el problema se relaciona con la necesidad de mantener familias de productos compatibles, característica que favorece la utilización de Abstract Factory.

¿Por qué descarté la otra alternativa?

R// Factory method lo descarté como alternativa principal porque su abstracción está orientada a la creación de un único producto. En esta situación cada formato representa una familia de productos relacionados (cuerpo y encabezado/pie) que deben mantenerse compatibles ente sí. Si utilizaramos este patrón para este caso, cada componente requeriría coordinar varias fábricas y podría permitir combinaciones inconsistentes. Factory Method puede resolver partes del problema, pero Abstract Factory representa mejor la estructura completa del problema.

### Decisión 2 — Mecanismo de extensibilidad de formatos (Parte 1)
Opción elegida: Registro dinámico mediante "Map<String, Supplier<ReportFormatFactory>>"
Justificación: Se descartó utilizar un switch o una cadena de if-else para seleccionar la fábrica, ya que este enfoque en específico obligaría a modificar el código existente cada vez que se agregue un nuevo formato. Por ejemplo, traigamos el hecho de incorporar CSV sería necesario agregar una condición para "CsvReportFactory", generando un punto central de modificación y dificultaría el cumplimiento del principio OCP.

Ahora bien, al implementar un registro dinámico como "Map<String, Supplier<ReportFormatFactory>>", cada indentificador de formato (PDF, Excel, HTML) se asocia a un Supplier capaz de crear su fábrica concreta. Debido a que elegí el patrón Abstract Factory, cada formato de salida representa una familia de productos relacionados compuesta por un cuerpo y un encabezado/pie de página. De esta manera, el Map + Supplier nos permite incorporar nuevos formatos mediante su registro, mientras que Abstract Factory garantiza que los productos creados pertenezcan a la misma familia y sean compatibles entre sí.

### Decisión 3 — Builder vs. constructor telescópico vs. setters (Parte 2)
#### 1. Evaluación y elección entre las tres opciones planteadas.
Opción elegida: **Clase mutable con setters sueltos (JavaBeans)**.
Justificación: De las tres opciones evaluadas, es la única que no obliga al cliente a recordar el orden exacto de 9 argumentos ni genera una explosión combinatoria de constructores sobrecargados. Permite instanciar con el campo obligatorio (`format`) y modificar solo las propiedades opcionales requeridas.
¿Por qué descarté las demás opciones?
  1.Constructor con 9 parámetros: Lo descarté ya que el cliente debe recordar el orden exacto de múltiples argumentos, varios de ellos del mismo tipo (String, boolean). Esto facilita que los valores sean intercambiados accidentalmente sin que el compilador pueda detectar el error, lo que puede producir una configuración incorrecta del objeto.
  2.Constructores sobrecargados (Telescópicos): Descartado porque los 8 parámetros opcionales generan una gran cantidad de combinaciones posibles (hasta 256), lo que llevaría a crear numerosos constructores casi idénticos. Esto aumentaría la complejidad del código y haría que la clase fuera difícil de mantener.

#### 2. Patrón creacional que resuelve las limitaciones.
R// El patrón Builder resuelve el problem sin las limitaciones de las alternativas anteriores, esto se debe a que permite construir ExportConfig de manera progresiva y legible. El parámetro obligatorio (format) se establece al iniciar el Builder, mientras que los parámetros opcionales pueden configurarse mediante métodos específicos sin depender de un orden determinado. Finalmente, build() permite realizar la validación necesaria y crear el objeto completamente configurado.

### Decisión 4 — ¿ReportFactoryRegistry necesita ser Singleton? (Parte 2)
Conclusión: NO
Justificación: No se considera necesario convertir ReportFactoryRegistry en singleton por que el registro actual utiliza el Map estático de ReportFactoryRegistry, este ya mantiene un único registro compartido de las fábricas dentro de la JVM. Por lo tanto, agregar un Singleton no aportaría una garantía adicional de unicidad sobre el registro. Por el lado de la inicialización costosa, la creación del registro no requiere operaciones costosas, ya que únicamente consiste en asociar los formatos con sus respectivas fábricas mediante el bloque static. En cuanto a la identidad de objeto, actualmente no existe la necesidad de pasar ReportFactoryRegistry como un objeto, inyectarlo por constructor, implementar una interfaz con él o sustituirlo por un mock en pruebas. El funcionamiento mediante métodos y estado estático es suficiente para las necesidades actuales.

## Herramientas utilizadas
- Java 17, Apache Maven, VS Code, Git, GitHub

## Conclusiones
Destaco primeramente la elección de un patrón de diseño, y es que debemos partir de un problema concreto y sus necesidades de variabilidad, y no de aplicar patrones por costumbre. También, esta actividad me permitió comprender mucho mejor Abstract Factory y la idea de mantener coherencia entre familias de productos, mientras que el uso de Map y Supplier permite extender los formatos sin modificar la lógica de resolución existente. Esta actividad me mostró que Singleton no debe aplicarse automáticamente a todos los componentes centrales, sino únicamente cuando exista una necesidad real de unicidad.



