# datahub-upgrade

**如何使用**

1、使用git clone命令将此上面三个服务下载到本地<br/>
2、切换到datahub-upgrade目录下执行 mvn clean install命令将此服务发布到本地仓库<br/>
3、在需要使用此插件的SpringBoot项目的pom.xml文件中添加如下相关依赖:<br/>

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.10</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<dependency>
	<groupId>com.gouuse</groupId>
	<artifactId>datahub-upgrade</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

4、在需要使用此插件的项目配置文件(application.yml或者bootstrap.yml)添加如下配置:

```yaml
#配置脚本升级
upgrade:
  enabled: true
  version: 1.0.0
  app-name: API-I18N
  upgrade-script: classpath:/upgrade/datahub_ugrade_v1.0.0.sql
```

<div style="width:770px;height:450px;margin:50px 0px">
   <img alt="upgrade-set.png" src="/images/upgrade-set.png" width="770" height="450"/>
</div>

5、 在第4步配置的sql脚本目录添加脚本文件，如下:

<div style="width:770px;height:450px;margin:50px 0px">
   <img alt="upgrade-sql.png" src="/images/upgrade-sql.png" width="770" height="450"/>
</div>

6、 在服务启动类中添加@EnableUpgrade注解，如下:

```java
@EnableUpgrade //开启脚本升级
@SpringBootApplication
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
```
7、 启动服务，升级脚本会自动执行
