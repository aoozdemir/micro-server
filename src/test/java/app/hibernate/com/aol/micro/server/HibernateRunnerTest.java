package app.hibernate.com.aol.micro.server;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aol.micro.server.Config;
import com.aol.micro.server.MicroServerStartup;
import com.aol.micro.server.rest.client.nio.RestClient;
import com.aol.micro.server.spring.annotations.Microserver;
import com.aol.micro.server.testing.RestAgent;
import com.google.common.collect.ImmutableMap;

@Microserver
public class HibernateRunnerTest {

  	private final RestClient<List<HibernateEntity>> listClient = new RestClient(1000,1000).withGenericResponse(List.class, HibernateEntity.class);

	RestAgent rest = new RestAgent();
	
	MicroServerStartup server;
	
	@Before
	public void startServer(){
		
		
		server = new MicroServerStartup( Config.builder().dataSourcePackages(ImmutableMap.of("db",Arrays.asList("app.hibernate.com.aol.micro.server"))).build()
												.withDAOClasses(HibernateRunnerTest.class)
												.withProperties(
																			ImmutableMap.of("db.connection.driver","org.hsqldb.jdbcDriver",
																						    "db.connection.url","jdbc:hsqldb:mem:aname",
																						    "db.connection.username", "sa",
																						    "db.connection.dialect","org.hibernate.dialect.HSQLDialect",
																						    "db.connection.ddl.auto","create"))
																				,()->"hibernate-app");
		server.start();

	}
	
	@After
	public void stopServer(){
		server.stop();
	}
	
	@Test
	public void runAppAndBasicTest() throws InterruptedException, ExecutionException{
		
		
		
		assertThat(rest.get("http://localhost:8080/hibernate-app/persistence/create"),is("ok"));
		assertThat(listClient.get("http://localhost:8080/hibernate-app/persistence/get").get().get(0),is(HibernateEntity.class));
		
		
	}
	
	
	
}
