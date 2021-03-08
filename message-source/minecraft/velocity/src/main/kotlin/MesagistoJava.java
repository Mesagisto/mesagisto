import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.logging.Logger;

@Plugin(id = "mesagisto", name = "Mesagisto", authors = {"Itsusinn"})
public class MesagistoJava {
	private final ProxyServer server;
	private final Logger logger;

	@Inject
	public MesagistoJava(ProxyServer server, Logger logger) {
		this.server = server;
		this.logger = logger;

		logger.info("Hello there, it's a test plugin I made!");
	}
}