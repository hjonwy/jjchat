package sancang.jjchat.androidclient;

public class ChatMessage {

	public interface IChatMessageHandler{
		public void handleChatMessage(ChatMessage msg);
	}
	
	public enum MessageType{
		Msg_Login, Msg_Chat, Msg_Logout
	}
	
	public MessageType msgType;
	public String sender;
	public String content;
	
	public ChatMessage(MessageType type){
		msgType = type;
	}
}
