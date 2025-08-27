# Proyecto Spring Batch - Particiones en Spring batch
Este proyecto es una solución de procesamiento de datos con Spring Batch que utiliza particiones para procesar archivos CSV de ventas de manera eficiente. Cada partición lee y transforma un subconjunto de datos de ventas, generando un informe consolidado con totales por producto y un archivo de errores para los registros que no cumplen los criterios establecidos. La configuración de particiones permite distribuir el procesamiento de datos en paralelo, optimizando el rendimiento y asegurando una ejecución precisa, en la que se reportan errores de manera clara para cada partición. Esta arquitectura es ideal para manejar grandes volúmenes de datos con eficiencia y precisión.

## Requisitos previos
- **Java 21**: Asegúrate de tener instalado JDK 21.
- **Maven 3.x** o superior: Para compilar y ejecutar el proyecto.

## Tecnologías utilizadas
- **Java 21**
- **Spring Batch**
- **Spring Boot**
- **Maven**

# Como utilizar
Para poder utilizar este repositorio, deberas abrir tu terminal (bash/PowerShell) e ir al directorio del proyecto.

1. Clonar el repositorio

```bash
git clone https://github.com/KariVillagran/batch_partitions.git
cd batch_partitions
```

2. Compila y ejecuta

```bash
mvn clean install
mvn spring-boot:run
```

Alternativamente, también puedes ejecutar el proyecto directamente usando el comando:

```bash
java -jar target/batch-partitions-configurations-0.0.1-SNAPSHOT.jar
```