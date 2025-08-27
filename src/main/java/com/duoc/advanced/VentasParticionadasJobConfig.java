package com.duoc.advanced;

import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.duoc.business.InformeVenta;
import com.duoc.business.Venta;
import com.duoc.items.ErrorItemWriter;
import com.duoc.items.VentasItemProcessor;
import com.duoc.items.VentasItemReader;
import com.duoc.jobs.CustomDecider;
//import com.duoc.advanced.VentaSkipListener;

@Configuration
@EnableBatchProcessing
@Import(DataSourceConfiguration.class)
@ComponentScans({
    @ComponentScan(basePackages = "com.duoc.items"),
    @ComponentScan(basePackages = "com.duoc.jobs")
})
public class VentasParticionadasJobConfig {

    @Bean
    public Job ventasJob(JobRepository jobRepository, @Qualifier("partitionStep") Step partitionStep, CustomDecider decider, JobCompletionListener listener) {
        return new JobBuilder("partitionedJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(partitionStep)
                //.next(consolidateErrorFilesStep()) // Paso adicional para consolidar archivos de error
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler partitionHandler(Step minionStep, TaskExecutor taskExecutor) {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setStep(minionStep);
        handler.setTaskExecutor(taskExecutor);
        handler.setGridSize(3); // Número de particiones
        return handler;
    }

    @Bean
    public Step partitionStep(JobRepository jobRepository, 
                              TaskExecutorPartitionHandler partitionHandler,
                              Partitioner partitioner) {
        return new StepBuilder("partitionStep", jobRepository)
                .partitioner("minionStep", partitioner)
                .partitionHandler(partitionHandler)
                .build();
    }

    @Bean
    public Step minionStep(JobRepository jobRepository, 
                           JdbcTransactionManager transactionManager,
                           VentasItemReader itemReader, 
                           VentasItemProcessor itemProcessor,
                           FlatFileItemWriter<InformeVenta> itemWriter,
                           FileVerificationSkipper fileVerificationSkipper,
                           VentaSkipListener skipListener) {
        return new StepBuilder("minionStep", jobRepository)
                .<Venta, InformeVenta>chunk(new SimpleCompletionPolicy(2), transactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .faultTolerant()
                .skipPolicy(fileVerificationSkipper)
                .listener(skipListener)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        String partitionName = stepExecution.getExecutionContext().getString("partitionName", "defaultPartition");

                        // Configura ErrorItemWriter para cada partición
                        ErrorItemWriter errorItemWriter = new ErrorItemWriter(partitionName);
                        skipListener.setErrorItemWriter(errorItemWriter); // Enlaza el escritor de errores a skipListener
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        return stepExecution.getExitStatus();
                    }
                })
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(10);
        executor.initialize();
        return executor;
    }    

    @Bean
    public Partitioner partitioner() {
        return gridSize -> {
            Map<String, ExecutionContext> partitions = new HashMap<>();
            int totalData = 10;  // Cambia esto al número real de datos
            int partitionSize = (int) Math.ceil((double) totalData / gridSize);

            int start = 0;
            for (int i = 0; i < gridSize; i++) {
                ExecutionContext context = new ExecutionContext();
                int end = Math.min(start + partitionSize - 1, totalData - 1);

                context.putInt("start", start);
                context.putInt("end", end);
                context.putString("partitionName", "partition" + i);
                partitions.put("partition" + i, context);

                start += partitionSize;
                if (start >= totalData) {
                    break;
                }
            }
            return partitions;
        };
    }

   
}