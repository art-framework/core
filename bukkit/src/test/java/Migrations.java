import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;

import java.io.IOException;

public class Migrations {

    public static void main(String[] args) throws IOException {

        DbMigration migration = DbMigration.create();

        // location of the migration changeSet and where ddl is generated to
        migration.setPathToResources("bukkit/src/main/resources");

        // add a series of database platforms to generate the ddl for ...
        migration.addPlatform(Platform.POSTGRES, "postgres");
        migration.addPlatform(Platform.MYSQL, "mysql");
        migration.addPlatform(Platform.MARIADB, "mariadb");
        migration.addPlatform(Platform.H2, "h2");
        migration.addPlatform(Platform.SQLITE, "sqlite");

//        migration.setGeneratePendingDrop("1.24");

        migration.generateMigration();
    }
}