/*
 * Copyright © WebServices pour l'Éducation, 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
		final String address = config.getString("address", "entcore.pdf.generator");
		logger.info("Starting PdfGenerator - address : " + address);
		vertx.eventBus().registerHandler(address, this);
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
				logger.error(e.getMessage(), e);
			}
		}
	}

}
