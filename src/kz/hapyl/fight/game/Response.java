package kz.hapyl.fight.game;

import javax.annotation.Nullable;

public class Response {

	public static final Response OK = new Response(null, Type.OK);
	public static final Response ERROR = new Response(null, Type.ERROR);
	public static final Response AWAIT = new Response(null, Type.AWAIT);
	public static final Response ERROR_DEFAULT = error("Talent is not complete!");

	private final String reason;
	private final Type type;

	public Response(String reason, Type type) {
		this.reason = reason;
		this.type = type;
	}

	public static Response error(String reason) {
		return new Response(reason, Type.ERROR);
	}

	@Nullable
	public String getReason() {
		return reason;
	}

	public boolean isOk() {
		return this.type == Type.OK;
	}

	public boolean isError() {
		return this.type == Type.ERROR;
	}

	public boolean isAwait() {
		return this.type == Type.AWAIT;
	}

	public enum Type {
		ERROR,
		OK,
		AWAIT // basically ok but does not start cooldown
	}

}
