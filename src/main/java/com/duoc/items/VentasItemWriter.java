package com.duoc.items;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.duoc.business.InformeVenta;

import java.io.IOException;
import java.io.Writer;

@Component
@StepScope
public class VentasItemWriter extends FlatFileItemWriter<InformeVenta> {

    public VentasItemWriter(@Value("#{stepExecutionContext['partitionName']}") String partitionName) {
        setResource(new FileSystemResource("output-" + partitionName + ".csv")); // Archivo de salida por partición
        setAppendAllowed(false);

        // Configura el encabezado del archivo
        setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("Producto,Cantidad Total,Total Ventas"); // Escribe el encabezado en el archivo de salida
            }
        });

        // Configura el agregador de líneas delimitadas y el extractor de campos
        DelimitedLineAggregator<InformeVenta> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(","); // Define el delimitador como coma

        BeanWrapperFieldExtractor<InformeVenta> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"producto", "cantidadTotal", "totalVentas"}); // Define los nombres de los campos

        lineAggregator.setFieldExtractor(fieldExtractor); // Configura el agregador de líneas con el extractor
        setLineAggregator(lineAggregator); // Asigna el agregador de líneas al escritor
    }
}
