package com.duoc.items;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ExecutionContext;
import com.duoc.business.Venta;

@Component
public class VentasItemReader implements ItemReader<Venta>, ItemStream {

    private final FlatFileItemReader<Venta> delegate; // El lector delegado
    private int start;
    private int end;
    private int currentLine;

    public VentasItemReader() {
        // Configura el FlatFileItemReader interno
        this.delegate = new FlatFileItemReaderBuilder<Venta>()
                .name("ventasItemReader") // Define el nombre del lector
                .resource(new ClassPathResource("consolidacion_diaria_ventas.csv")) // Ubicación del archivo CSV de entrada
                .linesToSkip(1) // Omite la primera línea (encabezados)
                .delimited() // Define el formato de los datos como delimitado
                .names("id", "producto", "cantidad", "precio") // Define los nombres de los campos
                .targetType(Venta.class) // Clase objetivo para el mapeo de los datos
                .build();
        this.delegate.setStrict(false); // Permite continuar en caso de error en el archivo
    }

    @Override
    public Venta read() throws Exception {
        if (currentLine < start) {
            // Salta las líneas hasta llegar al inicio del rango
            while (currentLine < start && delegate.read() != null) {
                currentLine++;
            }
        }

        // Solo lee dentro del rango [start, end]
        if (currentLine <= end) {
            currentLine++;
            return delegate.read();
        } else {
            return null; // Finaliza la lectura al superar el rango
        }
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        // Obtiene los valores de inicio y fin del ExecutionContext
        this.start = executionContext.getInt("start", 0);
        this.end = executionContext.getInt("end", Integer.MAX_VALUE);
        this.currentLine = 0; // Resetea el contador de líneas
        delegate.open(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);
    }
}
