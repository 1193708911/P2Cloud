package movi.net.request;

import movi.base.Response;

public interface RequestCallback {

	Class<? extends Response> getResultType();
	void  onSuccess(Response response);
	void   onFailure(Response response);

}
