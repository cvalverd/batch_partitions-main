package com.duoc.advanced;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import com.duoc.business.InformeVenta;
import com.duoc.business.Venta;
import com.duoc.items.ErrorItemWriter;

import java.util.List;

@Component
public class VentaSkipListener implements SkipListener<Venta, InformeVenta> {

    private static final Logger logger = LoggerFactory.getLogger(VentaSkipListener.class);
    private ErrorItemWriter errorItemWriter;

    public void setErrorItemWriter(ErrorItemWriter errorItemWriter) {
        this.errorItemWriter = errorItemWriter;
    }
    
    @Override
    public void onSkipInProcess(Venta item, Throwable t) {
        logger.info("Tipo de excepción en SkipListener: {}", t.getClass().getName());
        try {
            errorItemWriter.open(new ExecutionContext());  // Abre el writer antes de escribir
            errorItemWriter.setAppendAllowed(true);
            errorItemWriter.write(new Chunk<>(List.of(item)));
            errorItemWriter.close();  // Cierra el writer después de escribir
        } catch (Exception e) {
            logger.error("Error al escribir registro omitido en archivo de errores: ", e);
        }
    }

    @Override
    public void onSkipInRead(Throwable t) {
        logger.warn("Línea omitida debido a un error en la lectura", t);
    }

    @Override
    public void onSkipInWrite(InformeVenta item, Throwable t) {
        logger.error("Error al escribir registro", t);
    }
    
}
