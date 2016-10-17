package r3qu13m.pml;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.RelaunchClassLoader;
import net.minecraft.client.Minecraft;
import r3qu13m.mc.SidedLogger;
import r3qu13m.pml.python.PyMod;

@Mod(name = "PythonModLoader", version = "0.0.1", modid = "python_modloader")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class PythonModLoader {
	@Mod.Instance("python_modloader")
	public static PythonModLoader instance;
	public static File pyModsDir;
	public static PythonInterpreter pyi;
	public static Map<String, PythonModContainer> loaded_pymods = new HashMap<>();

	static {
		RelaunchClassLoader loader = (RelaunchClassLoader) Thread.currentThread().getContextClassLoader();
		loader.addURL(PythonModLoader.class.getResource("jython.jar"));
		pyi = new PythonInterpreter();
		pyModsDir = new File(Minecraft.getMinecraftDir(), "pymods");
		if (!pyModsDir.exists()) {
			pyModsDir.mkdir();
		}
	}

	public PythonModLoader() {
		if (instance != null) {
			throw new RuntimeException("Already Instantiated");
		}
		instance = this;
		SidedLogger.setDebug();
		SidedLogger.debug("%s", PythonModLoader.class.getResource("jython.jar"));

		List<ModContainer> mods = null;
		List<ModContainer> amods = null;
		try {
			mods = ReflectionHelper.getPrivateValue(Loader.class, Loader.instance(), "mods");
			LinkedList<ModContainer> list = new LinkedList<>();
			list.addAll(mods);
			mods = list;

			LoadController lc = ReflectionHelper.getPrivateValue(Loader.class, Loader.instance(), "modController");
			EventBus bus = ReflectionHelper.getPrivateValue(LoadController.class, lc, "masterChannel");
			for (File f : pyModsDir.listFiles()) {
				if (f.isDirectory()) {
					File descriptionFile = new File(f, "__mod__.py");
					if (descriptionFile.exists()) {
						pyi.cleanup();
						pyi.execfile(descriptionFile.getAbsolutePath());

						PyObject pymodname = pyi.get("__name__");
						PyObject pymodid = pyi.get("__modid__");
						PyObject pymodver = pyi.get("__version__");
						PyObject pymodmain = pyi.get("__main__");
						String modname = pymodname.toString();
						String modid = pymodid.toString();
						String modver = pymodver.toString();
						String modmain = pymodmain.toString();
						File mainFile = new File(f, modmain);
						pyi.cleanup();
						pyi.execfile(mainFile.getAbsolutePath());
						PyObject pymodobj = pyi.get("instance");
						PyMod mod = (PyMod) pymodobj.__tojava__(PyMod.class);
						PythonModContainer pmc = new PythonModContainer(modname, modver, modname, mainFile, mod);
						mods.add(pmc);
						pmc.registerBus(bus, lc);
						lc.getActiveModList().add(pmc);
						SidedLogger.log("Found PyMod: %s", modid);
					}
				}
			}
			ReflectionHelper.setPrivateValue(Loader.class, Loader.instance(), mods, "mods");
		} catch (IllegalArgumentException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
}
