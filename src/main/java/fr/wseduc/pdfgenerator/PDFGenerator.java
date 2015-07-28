package fr.wseduc.pdfgenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class PDFGenerator extends BusModBase implements Handler<Message<JsonObject>> {

	private final ITextRenderer renderer = new ITextRenderer();
	private String baseUrl = "";

	/**
	 * Verticle start method.
	 */
	@Override
	public void start(){
		super.start();
		vertx.eventBus().registerHandler(config.getString("pdf-generator-address", "entcore.pdf.generator"), this);
		baseUrl = config.getString("baseUrl", "");
	}

	@Override
	public void handle(Message<JsonObject> message) {
		final String encoding = message.body().getString("encoding", "UTF-8");
		ByteArrayOutputStream binaryOutput = new ByteArrayOutputStream();

		try {
			String content = new String(message.body().getBinary("content"), encoding);

			if(content == null || content.trim().isEmpty()){
				sendError(message, "invalid.content");
				return;
			}

			//Actual pdf rendering
			renderer.setDocumentFromString(content, message.body().getString("baseUrl", baseUrl));
			renderer.layout();
			renderer.createPDF(binaryOutput);

			sendOK(message, new JsonObject().putBinary("content", binaryOutput.toByteArray()));
		} catch (UnsupportedEncodingException encexp) {
			sendError(message, "invalid.encoding", encexp);
		} catch (Exception exc) {
			sendError(message, "error.while.rendering", exc);
		} finally {
			try {
				binaryOutput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
