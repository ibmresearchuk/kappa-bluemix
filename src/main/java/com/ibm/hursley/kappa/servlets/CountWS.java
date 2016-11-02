package com.ibm.hursley.kappa.servlets;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.ibm.hursley.kappa.kafka.KappaListenerInterface;
import com.ibm.hursley.kappa.kafka.KappaQueries;
import com.ibm.hursley.kappa.kafka.KappaQuery;



@ServerEndpoint(value = "/ws/count/{queryhash}")
public class CountWS {
	
	
	private KappaQueries kappaQueries = null;
	
	public CountWS() {
		kappaQueries = new KappaQueries();
	}
	
	@OnOpen
	public void open(final Session session, @PathParam("queryhash") String queryHash) {
		System.out.println("ws open:" + queryHash);
		
		KappaQuery kappaQuery = kappaQueries.getQuery(queryHash);
		
		if(kappaQuery != null){
			kappaQuery.addListener(new KappaListenerInterface() {
				@Override
				public void updateResult(String data) {
					System.out.println("Sending data to session");
					try {
						if(session != null && session.isOpen()){
							session.getBasicRemote().sendText(data);
						}
					} 
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			},true);
		}
		else{
		
		}
		
	}
	
	@OnMessage
	public String message(String message, Session session, @PathParam("queryhash") String queryHash) {
		System.out.println("ws: message: " + queryHash);
		
		KappaQuery kappaQuery = kappaQueries.getQuery(queryHash);
		if(kappaQuery != null){
			return kappaQuery.getResult();
		}
		else{
			return "0";
		}
		
		
		
	}
	
	@OnClose
	public void close(Session session, CloseReason reason) {
		System.out.println("ws: close");
	}
	
	@OnError
	public void error(Throwable t) {
		t.printStackTrace();
	}

}




