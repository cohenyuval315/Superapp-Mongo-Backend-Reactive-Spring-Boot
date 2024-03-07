package com.application.app.miniApps.cartSal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.application.app.boundaries.ObjectBoundary;
import com.application.app.boundaries.UnknownCommandResponseBoundary;
import com.application.app.daos.ObjectCrud;
import com.application.app.daos.UserCrud;
import com.application.app.entities.ObjectEntity;
import com.application.app.enums.SupportedMiniApp;
import com.application.app.exceptions.InternalServerException;
import com.application.app.exceptions.InvalidRequestException;

import reactor.core.publisher.Flux;

import com.application.app.miniApps.MiniApp;

/**
 * SuperMarket Branch Object:
 * id 
 * title
 * products: [
 * 		id:
 * 		title:
 * 		price:
 * ]
 * 
 * **/


public class CartSalMiniApp implements MiniApp {
	
	private ObjectCrud objectCrud;
	
	public CartSalMiniApp(ObjectCrud objectCrud) {
		this.objectCrud = objectCrud;
	}	
	
	
	
	@Override
	public Flux<Object> invokeCommand(String command, String userId, ObjectEntity object,
			Map<String, Object> commandAttributes) {
		try {
			CartSalCommand cartSalCommand = CartSalCommand.valueOf(command);
	        switch (cartSalCommand) {
				case GET_SUPERMARKETS_PRICES_BY_CART: 
					return this.getAllValidSupermarketPricesByCart(commandAttributes);
				default:
					return Flux.error(() -> new InternalServerException("Could Not Get Mini App Command (Should not be here)"));
			} 			
		} catch (IllegalArgumentException e) {
			UnknownCommandResponseBoundary crb = new UnknownCommandResponseBoundary();
			crb.setCommandName(command);
			crb.setMiniAppName(SupportedMiniApp.CartSal.name());
			crb.setCommandAttributes(commandAttributes);
	    	return Flux.just(crb); 
	    }	
	}

	
	public Flux<Object> getAllValidSupermarketPricesByCart(Map<String, Object> commandAttributes){
		String branchType = "SupermarketBranch";
		String attr = "cart";
		if (!commandAttributes.containsKey(attr) || !(commandAttributes.get(attr) instanceof List)) {
			return Flux.error(() -> new InvalidRequestException("invalid command attributes: {" + attr + "}"));
		}	
	    List<Object> cartObject = (List<Object>) commandAttributes.get(attr);
        for (Object item : cartObject) {
            if (!(item instanceof String)) {
            	return Flux.error(() -> new InvalidRequestException("not all cart items are strings"));
            }
        }
        List<String> cart = new ArrayList<>();
        for (Object item : cartObject) {
            cart.add((String) item);
        }
        return this.objectCrud.findAllByType(branchType)
        		.flatMap(object -> {
        			try {
            			Map<String, Object> branch = object.getObjectDetails();
            			List<Map<String, Object>> branchProducts = (List<Map<String, Object>>) branch.get("products");
            			Map<String, Object> branchData = new HashMap<>();
            			double totalPrice = 0.0;
            			
            			List<Map<String, Object>> validProducts = new ArrayList<>();
            			List<Map<String, Object>> invalidProducts = new ArrayList<>();
            			Map<String, Boolean> found = new HashMap<>();
            			for (String cartProductId : cart) {
            				found.put(cartProductId, false);
            			}
                        for (Map<String, Object> product : branchProducts) {
                        	if(!product.containsKey("id") || !(product.get("id") instanceof String)) {
                        		return Flux.error(() -> new InternalServerException("product id is missing error "));
                        	}
                        	String productId = product.get("id").toString();
                            if (cart.contains(productId)) {
                            	if(!product.containsKey("price")) {
                            		return Flux.error(() -> new InternalServerException("product price field is missing  error "));
                            	}
                                validProducts.add(product);
                                found.put(productId, true);
                                totalPrice += Double.parseDouble(product.get("price").toString());
                            }
                        }
                        for (Map.Entry<String, Boolean> entry : found.entrySet()) {
                            String key = entry.getKey();
                            Boolean value = entry.getValue();
                            if (value == false) {
                            	Map<String, Object> invalidProduct = new HashMap<>();
                            	invalidProduct.put("id",key);
                            	invalidProducts.add(invalidProduct);
                            }
                        
                        }
                        branchData.put("validProducts", validProducts);
                        branchData.put("invalidProducts", invalidProducts);
                        branchData.put("totalPrice", totalPrice);
                        branchData.put("id", branch.get("id"));
                        branchData.put("title", branch.get("title"));
                        branchData.put("address", branch.get("address"));
                        return Flux.just(branchData);
        			}catch (Exception e) {
						return Flux.error(() -> new InternalServerException("server error in parsing branch"));
					}
        		})
        		.cast(Object.class)
        		.log();
	}
	
	

	 
	
	
	
	

	
}
