package pl.baczkowicz.mqttspy;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;

import com.sun.javafx.application.LauncherImpl;

/**
 * Standalone launcher that bootstraps the JavaFX runtime before delegating to {@link Main}.
 * Having a separate entry point avoids the JRE's early JavaFX checks that assume
 * platform modules are available on the runtime path.
 */
public final class MqttSpyLauncher
{
	private MqttSpyLauncher()
	{
		// no instances
	}

	public static void main(String[] args)
	{
		System.setProperty("javax.xml.bind.JAXBContextFactory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
		System.setProperty("javafx.verbose", "true");
		System.setProperty("javafx.launcher.debug", "true");
		System.setProperty("prism.verbose", "true");

		detectAndSetJavaFxPlatform();
		printRuntimeProbe();
		try
		{
			LauncherImpl.launchApplication(Main.class, args);
		}
		catch (Throwable t)
		{
			log("LauncherImpl failed: " + t.getClass().getName() + " - " + t.getMessage(), t);
			throw (t instanceof RuntimeException) ? (RuntimeException) t : new RuntimeException(t);
		}
	}

	private static void detectAndSetJavaFxPlatform()
	{
		final String osName = System.getProperty("os.name", "").toLowerCase();
		final String arch = System.getProperty("os.arch", "").toLowerCase();
		String platform = null;
		if (osName.contains("win"))
		{
			platform = "win";
		}
		else if (osName.contains("mac") || osName.contains("darwin"))
		{
			platform = (arch.contains("aarch64") || arch.contains("arm")) ? "mac-aarch64" : "mac";
		}
		else if (osName.contains("linux"))
		{
			platform = arch.contains("aarch64") ? "linux-aarch64" : "linux";
		}

		if (platform != null)
		{
			System.setProperty("javafx.platform", platform);
			log("Configured javafx.platform=" + platform, null);
		}
		else
		{
			log("Unable to determine javafx.platform for os='" + osName + "' arch='" + arch + "'", null);
		}
	}

	private static void printRuntimeProbe()
	{
		log("Java version=" + System.getProperty("java.version") + ", vendor=" + System.getProperty("java.vendor"), null);
		log("OS=" + System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")", null);
		probeClass("javafx.application.Application");
		probeClass("com.sun.javafx.application.LauncherImpl");
		probeClass("com.sun.glass.ui.Application");
		probeClass("com.sun.glass.ui.win.WinApplication");
		log("javafx.platform property=" + System.getProperty("javafx.platform"), null);
		log("java.library.path=" + System.getProperty("java.library.path"), null);
		log("java.class.path=" + System.getProperty("java.class.path"), null);
	}

	private static void probeClass(String name)
	{
		try
		{
			Class.forName(name);
			log("Found class " + name, null);
		}
		catch (Throwable t)
		{
			log("Missing class " + name + ": " + t, t);
		}
	}

	private static void log(String message, Throwable t)
	{
		final String fullMessage = "[mqtt-spy] " + Instant.now() + " - " + message;
		System.err.println(fullMessage);
		if (t != null)
		{
			t.printStackTrace(System.err);
		}
		try (PrintWriter writer = new PrintWriter(new FileWriter("mqttspy-launch-debug.log", true)))
		{
			writer.println(fullMessage);
			if (t != null)
			{
				t.printStackTrace(writer);
			}
		}
		catch (IOException ignored)
		{
			System.err.println("[mqtt-spy] Failed to write log file: " + ignored);
		}
	}
}
