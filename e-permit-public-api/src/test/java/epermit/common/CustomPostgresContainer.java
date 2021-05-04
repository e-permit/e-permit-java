package epermit.common;


import org.slf4j.*;
import org.testcontainers.containers.PostgreSQLContainer;

public class CustomPostgresContainer extends PostgreSQLContainer<CustomPostgresContainer> {
 
    private static final Logger logger = LoggerFactory.getLogger(CustomPostgresContainer.class);
    private static final String IMAGE_VERSION = "postgres:alpine";
    private static CustomPostgresContainer container;
   
    private CustomPostgresContainer() {
      super(IMAGE_VERSION);
    }
   
    public static CustomPostgresContainer getInstance() {
      if (container == null) {
        container = new CustomPostgresContainer();
      }
      return container;
    }
   
    @Override
    public void start() {
      super.start();
      logger.debug("POSTGRES INFO");
      logger.debug("SPRING_DATASOURCE_URL: " + container.getJdbcUrl());
      logger.debug("SPRING_DATASOURCE_USERNAME: " + container.getUsername());
      logger.debug("SPRING_DATASOURCE_PASSWORD: " + container.getPassword());
      System.setProperty("SPRING_DATASOURCE_URL", container.getJdbcUrl());
      System.setProperty("SPRING_DATASOURCE_USERNAME", container.getUsername());
      System.setProperty("SPRING_DATASOURCE_PASSWORD", container.getPassword());
    }
   
    @Override
    public void stop() {
      //do nothing, JVM handles shut down
    }
  }
