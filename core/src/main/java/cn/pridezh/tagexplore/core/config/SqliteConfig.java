package cn.pridezh.tagexplore.core.config;

import cn.pridezh.tagexplore.core.config.properties.AppProperties;
import cn.pridezh.tagexplore.core.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author PrideZH
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class SqliteConfig {

    private final AppProperties appProperties;

    /**
     * 数据源
     */
    @Bean(name = "sqliteDataSource")
    public DataSource sqliteDataSource() throws IOException {
        // Create file of sqlite
        boolean isInitialize = false;
        String url = appProperties.getRepository() + appProperties.getDatabase();
        File file = new File(url);
        if(!file.exists()){
            isInitialize = true;
            if (!file.createNewFile()) {
                throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "数据库创建失败");
            }
        }

        // 创建数据源
        DataSource dataSource  = DataSourceBuilder.create()
                .driverClassName("org.sqlite.JDBC")
                .url("jdbc:sqlite:" + url)
                .type(SQLiteDataSource.class)
                .build();

        // Create table
        if (isInitialize) {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);

                List<String> sqlList = new ArrayList<>();
                sqlList.add("""
                        CREATE TABLE [resource] (
                          [id] INT PRIMARY KEY NOT NULL,
                          [name] VARCHAR(255) NOT NULL,
                          [type] VARCHAR(32) NOT NULL DEFAULT '',
                          [cover] BLOB
                        );
                        """);
                sqlList.add("""
                        CREATE TABLE [cover] (
                          [id] INT PRIMARY KEY NOT NULL,
                          [resource_id] INT NOT NULL,
                          [data] BLOB NOT NULL
                        );
                        """);
                sqlList.add("""
                        CREATE TABLE [tag](
                          [id] INT PRIMARY KEY NOT NULL,
                          [name] VARCHAR(32) NOT NULL,
                          [group] INT(4) NOT NULL DEFAULT 0
                        );
                        """);
                sqlList.add("""
                        CREATE TABLE [resource__tag](
                          [id] INT NOT NULL,
                          [resource_id] INT NOT NULL,
                          [tag_id] INT NOT NULL
                        );
                        """);
                for (String sql : sqlList) {
                    connection.prepareStatement(sql).execute();
                }

                connection.commit();
            } catch (Exception e) {
                file.delete();
                log.error("数据库创建失败");
            }
        }

        return dataSource;
    }

}
