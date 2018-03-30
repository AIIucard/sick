package main.htw.handler;

public interface IHandler {
	public void handleConnection();
	
	public void handleRequest();
	
	public void handleReply();
}
