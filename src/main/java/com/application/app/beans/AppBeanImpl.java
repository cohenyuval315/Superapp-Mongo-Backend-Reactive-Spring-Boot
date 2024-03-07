package com.application.app.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.application.app.daos.ObjectCrud;
import com.application.app.daos.UserCrud;
import com.application.app.entities.ObjectEntity;
import com.application.app.entities.UserEntity;
import com.application.app.enums.Role;

import jakarta.annotation.PostConstruct;

@Component
public class AppBeanImpl implements AppBean {
	
	
	private Log logger = LogFactory.getLog(AppBeanImpl.class);
	
	@Value("${spring.application.name}")
	private String superapp;
	
	@Value("${spring.application.delimiter}")
	private String delimiter;
	
	@Value("${initial.app.message:Default Message}")
	private String initMessage;
	
	@Value("${server.port}")
	private String port;
	
	private String hostString = "127.0.0.1";
	
	@Value("${logging.level.org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping}")
	private String webReactiveLogLevel;
	
	@Value("${logging.level.org.springframework.data.mongodb.core.annotation.Collation}")
	private String springMongoLogLevel;
	
	@Value("${logging.level.org.springframework}")
	private String springLogLevel;
	
	@Value("${logging.level.org.springframework.data.mongodb.core.MongoTemplate}")
	private String mongoLogLevel;
	
	@Value("${logging.level.com.application.app.services.CommandServiceImpl}")
	private String commandServiceLogLevel;
	
	@Value("${logging.level.com.application.app.services.UserServiceImpl}")
	private String userServiceLogLevel;
	
	@Value("${logging.level.com.application.app.services.ObjectServiceImpl}")
	private String objectServiceLogLevel;
	
	@Value("${logging.level.com.application.app.beans.AppBeanImpl}")
	private String appBeanLogLevel;
	
	@Value("${logging.level.com.application.app.exceptions.InternalServerException}")
	private String internalErrorLogLevel;
	
	@Value("${spring.docker.compose.start.log-level}")
	private String dockerComposeLogLevel;
	
	
	@Value("${logging.level.org.mongodb.driver}")
	private String mongoDriver;
	

    @Autowired
    private UserCrud userCrud; 

    @Autowired
    private ObjectCrud objectCrud; 
     
    
	@PostConstruct
	public void init() {
		String message =
				"\n\n** Application Name: '" + this.superapp + "'\n" +
				"** Host: ''" + this.hostString  + "\n" +
				"** Port: '" + this.port + "'\n" +
				"** Initial Message: " +  this.initMessage + "\n" +
				"** The Delimter Used: '" + this.delimiter + "'\n" +
				"** LOGGINGS LEVELS: \n" +
                "---------------- \n" +
                "Application Bean: " + appBeanLogLevel + "\n" +
                "Web Reactive Handler Mapping: " + webReactiveLogLevel + "\n" +
                "Spring Data MongoDB Collation: " + springMongoLogLevel + "\n" +
                "Spring Framework: " + springLogLevel + "\n" +
                "MongoDB Core: " + mongoLogLevel + "\n" +
                "Command Service: " + commandServiceLogLevel + "\n" +
                "User Service: " + userServiceLogLevel + "\n" +
                "Object Service: " + objectServiceLogLevel + "\n" +
                "Internal Server Exception: " + internalErrorLogLevel + "\n" +
                "Docker Compose Start Log Level: " + dockerComposeLogLevel + "\n" +
                "Mongo Driver Log Level: " + mongoDriver + "\n" +
                "----------------\n\n";
		logger.debug(message);
		
		this.seedDatabase();
		
	}
	
	public void seedDatabase() {
		logger.info("seeding mongo...");
		try {
			
			// Users
			String adminEmail = "admin@admin.com";
			String adminId = this.superapp + this.delimiter + adminEmail;
			UserEntity admin = new UserEntity(adminId,"username", adminEmail, this.superapp, Role.ADMIN.name(), "avatar");
			
			String superappUserEmail = "super@super.com";
			String superappUserId = this.superapp + this.delimiter + superappUserEmail;
			UserEntity superappUser = new UserEntity(superappUserId,"username", superappUserEmail, this.superapp, Role.SUPERAPP_USER.name(), "avatar");
			
			String miniappUserEmail = "mini@mini.com";
			String miniappUserId = this.superapp + this.delimiter + miniappUserEmail;
			UserEntity miniappUser = new UserEntity(miniappUserId,"username", miniappUserEmail, this.superapp, Role.MINIAPP_USER.name(), "avatar");
						
			
			
			List<UserEntity> users = Arrays.asList(
					admin,
					superappUser,
					miniappUser
			);
			
			// Objects
			String objId = this.generateObjectId();
			String userSuperapp = this.superapp;
			
	        Map<String, Object> product1SupersalRishon = new HashMap<>();
	        product1SupersalRishon.put("id", "PRODUCT_1");
	        product1SupersalRishon.put("title", "product1");
	        product1SupersalRishon.put("price", 10.99);
	        
	        Map<String, Object> product1SupersalNessZiona = new HashMap<>();
	        product1SupersalNessZiona.put("id", "PRODUCT_1");
	        product1SupersalNessZiona.put("title", "product1");
	        product1SupersalNessZiona.put("price", 9.99);
	        
	        Map<String, Object> product1OsherAdRishon= new HashMap<>();
	        product1OsherAdRishon.put("id", "PRODUCT_1");
	        product1OsherAdRishon.put("title", "product1");
	        product1OsherAdRishon.put("price", 8.99);
	        
	        
	        
	        Map<String, Object> product2SupersalRishon = new HashMap<>();
	        product2SupersalRishon.put("id", "PRODUCT_2");
	        product2SupersalRishon.put("title", "product2");
	        product2SupersalRishon.put("price", 99.99);
	        
	        Map<String, Object> product2SupersalNessZiona = new HashMap<>();
	        product2SupersalNessZiona.put("id", "PRODUCT_2");
	        product2SupersalNessZiona.put("title", "product2");
	        product2SupersalNessZiona.put("price", 101.99);
	        
	        Map<String, Object> product2OsherAdRishon= new HashMap<>();
	        product2OsherAdRishon.put("id", "PRODUCT_2");
	        product2OsherAdRishon.put("title", "product2");
	        product2OsherAdRishon.put("price", 102.99);
	        
	        
	        
	        Map<String, Object> product3SupersalRishon = new HashMap<>();
	        product3SupersalRishon.put("id", "PRODUCT_3");
	        product3SupersalRishon.put("title", "product3");
	        product3SupersalRishon.put("price", 7.99);
	        
	        Map<String, Object> product3SupersalNessZiona = new HashMap<>();
	        product3SupersalNessZiona.put("id", "PRODUCT_3");
	        product3SupersalNessZiona.put("title", "product3");
	        product3SupersalNessZiona.put("price", 10.99);
	        
	        Map<String, Object> product3OsherAdRishon= new HashMap<>();
	        product3OsherAdRishon.put("id", "PRODUCT_3");
	        product3OsherAdRishon.put("title", "product3");
	        product3OsherAdRishon.put("price", 6.99);
	        
	        
	        
	        
	        Map<String, Object> product4SupersalRishon = new HashMap<>();
	        product4SupersalRishon.put("id", "PRODUCT_4");
	        product4SupersalRishon.put("title", "product4");
	        product4SupersalRishon.put("price", 10);
	        
	        Map<String, Object> product5SupersalNessZiona = new HashMap<>();
	        product5SupersalNessZiona.put("id", "PRODUCT_5");
	        product5SupersalNessZiona.put("title", "product5");
	        product5SupersalNessZiona.put("price", 10);
	        
	        Map<String, Object> product6OsherAdRishon= new HashMap<>();
	        product6OsherAdRishon.put("id", "PRODUCT_6");
	        product6OsherAdRishon.put("title", "product6");
	        product6OsherAdRishon.put("price", 5);
			
			Map<String, Object> SuperSalRishonObjectDetails = new HashMap<>();
			SuperSalRishonObjectDetails.put("id", "SuperSalRishonID");
	        SuperSalRishonObjectDetails.put("title", "Supersal Rishon");
	        SuperSalRishonObjectDetails.put("address", "rishon 3");
	        List<Map<String, Object>> SuperSalRishonProducts = new ArrayList<>();
	        SuperSalRishonProducts.add(product1SupersalRishon);
	        SuperSalRishonProducts.add(product2SupersalRishon);
	        SuperSalRishonProducts.add(product3SupersalRishon);
	        SuperSalRishonProducts.add(product4SupersalRishon);
	        SuperSalRishonObjectDetails.put("products", SuperSalRishonProducts);

			
			Map<String, Object> SuperSalNessZionaObjectDetails = new HashMap<>();
			SuperSalNessZionaObjectDetails.put("id", "SuperSalNessZionaID");
			SuperSalNessZionaObjectDetails.put("title", "Supersal Ness Ziona");
			SuperSalNessZionaObjectDetails.put("address", "ness ziona 4");
	        List<Map<String, Object>> SuperSalNessZionaProducts = new ArrayList<>();
	        SuperSalNessZionaProducts.add(product1SupersalNessZiona);
	        SuperSalNessZionaProducts.add(product2SupersalNessZiona);
	        SuperSalNessZionaProducts.add(product3SupersalNessZiona);
	        SuperSalNessZionaProducts.add(product5SupersalNessZiona);
	        SuperSalNessZionaObjectDetails.put("products", SuperSalNessZionaProducts);


			Map<String, Object> OsherAdRishonObjectDetails = new HashMap<>();
			OsherAdRishonObjectDetails.put("id", "OsherAdRishonID");
			OsherAdRishonObjectDetails.put("title", "OsherAd Rishon");
			OsherAdRishonObjectDetails.put("address", "rishon 1");
	        List<Map<String, Object>> OsherAdRishonProducts = new ArrayList<>();
	        OsherAdRishonProducts.add(product1OsherAdRishon);
	        OsherAdRishonProducts.add(product2OsherAdRishon);
	        OsherAdRishonProducts.add(product3OsherAdRishon);
	        OsherAdRishonProducts.add(product6OsherAdRishon);
	        OsherAdRishonObjectDetails.put("products", OsherAdRishonProducts);


			
			ObjectEntity SuperSalRishon = new ObjectEntity(
					objId, 
					"SupermarketBranch", 
					"SupersalRishon", 
					this.superapp,
					userSuperapp,
					true, 
					superappUserId, 
					SuperSalRishonObjectDetails);
			
			ObjectEntity SuperSalNessZiona = new ObjectEntity(
					objId, 
					"SupermarketBranch", 
					"SupersalNessZiona", 
					this.superapp,
					userSuperapp,
					true, 
					superappUserId, 
					SuperSalNessZionaObjectDetails);

			ObjectEntity OsherAdRishon = new ObjectEntity(
					objId, 
					"SupermarketBranch", 
					"OsherAdRishon", 
					this.superapp,
					userSuperapp,
					true, 
					superappUserId, 
					OsherAdRishonObjectDetails);
			
			List<ObjectEntity> objects = Arrays.asList(
					SuperSalRishon,
					SuperSalNessZiona,
					OsherAdRishon
			);
			
			
			if (userCrud.count().block() == 0) {
				logger.info("seeding users in mongo...");
				userCrud.saveAll(users).blockLast();
				logger.info("users seeding was successful...");
			}else {
				logger.info("users already seeded ...");
			}	
			
			if (objectCrud.count().block() == 0) {
				logger.info("seeding objects in mongo...");
				objectCrud.saveAll(objects).blockLast();
				logger.info("objects seeding was successful...");
			}else {
				logger.info("objects already seeded ...");	
			}
			
		}catch (Exception e) {
			logger.error("An Error occured durring the seeding");
			logger.trace("An Error occured durring the seeding: " +  e.toString());
		}
	}
	
	private String generateObjectId() {
		return this.superapp + this.delimiter + UUID.randomUUID().toString();
	}

}
