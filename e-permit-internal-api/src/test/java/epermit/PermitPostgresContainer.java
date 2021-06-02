package epermit;
import org.testcontainers.containers.PostgreSQLContainer;

public class PermitPostgresContainer extends PostgreSQLContainer<PermitPostgresContainer> {
    private static final String IMAGE_VERSION = "postgres:alpine";
    private static PermitPostgresContainer container;
   
    private PermitPostgresContainer() {
      super(IMAGE_VERSION);
    }
   
    public static PermitPostgresContainer getInstance() {
      if (container == null) {
        container = new PermitPostgresContainer();
      }
      return container;
    }
   
    @Override
    public void start() {
      super.start();
      System.setProperty("SPRING_DATASOURCE_URL", container.getJdbcUrl());
      System.setProperty("SPRING_DATASOURCE_USERNAME", container.getUsername());
      System.setProperty("SPRING_DATASOURCE_PASSWORD", container.getPassword());
    }
   
    @Override
    public void stop() {
    }
  }
