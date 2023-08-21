package pti.sb_squash_mvc.dto;

public class PasswordChangeResponse {
	
	private boolean success;
	private String responseMessage;
	
	
	
	public PasswordChangeResponse(boolean success, String responseMessage) {
		this.success = success;
		this.responseMessage = responseMessage;
	}



	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	
	
	
	
	
}
