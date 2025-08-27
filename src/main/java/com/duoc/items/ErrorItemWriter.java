package com.duoc.items;

import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;

import com.duoc.business.Venta;


public class ErrorItemWriter extends FlatFileItemWriter<Venta> {

    public ErrorItemWriter(String partitionName) {
        // Configura el nombre del archivo de errores usando el nombre de la partición
        setResource(new FileSystemResource("errores_" + partitionName + ".csv")); // Define el archivo de salida de errores

        // Configura el delimitador y el extractor de campos
        DelimitedLineAggregator<Venta> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(","); // Usa coma como delimitador

        BeanWrapperFieldExtractor<Venta> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "producto", "cantidad", "precio"}); // Define los campos de la clase Venta

        lineAggregator.setFieldExtractor(fieldExtractor); // Configura el agregador de líneas con el extractor de campos
        setLineAggregator(lineAggregator); // Asigna el agregador de líneas al escritor
    }
}