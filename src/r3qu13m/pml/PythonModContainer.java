package r3qu13m.pml;

import java.io.File;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import cpw.mods.fml.common.versioning.VersionRange;
import r3qu13m.pml.python.PyMod;

public class PythonModContainer implements ModContainer {
	private ModMetadata meta = new ModMetadata();
	private File mainFile;
	private boolean isEnable;
	private EventBus eventBus;
	private LoadController controller;
	private PyMod modobj;

	public PythonModContainer(String par1Name, String par2Version, String par3Id, File par4Main, PyMod par5Mod) {
		meta.name = par1Name;
		meta.version = par2Version;
		meta.modId = par3Id;
		mainFile = par4Main;
		modobj = par5Mod;
		meta.description = "";
	}

	@Override
	public VersionRange acceptableMinecraftVersionRange() {
		try {
			return VersionRange.createFromVersionSpec("1.4.7");
		} catch (InvalidVersionSpecificationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void bindMetadata(MetadataCollection mc) {

	}

	@Override
	public List<ArtifactVersion> getDependants() {
		return new ArrayList<ArtifactVersion>();
	}

	@Override
	public List<ArtifactVersion> getDependencies() {
		return new ArrayList<ArtifactVersion>();
	}

	@Override
	public String getDisplayVersion() {
		return meta.version;
	}

	@Override
	public ModMetadata getMetadata() {
		return meta;
	}

	@Override
	public Object getMod() {
		return modobj;
	}

	@Override
	public String getModId() {
		return meta.modId;
	}

	@Override
	public String getName() {
		return meta.name;
	}

	@Override
	public ArtifactVersion getProcessedVersion() {
		return null;
	}

	@Override
	public Set<ArtifactVersion> getRequirements() {
		return null;
	}

	@Override
	public Certificate getSigningCertificate() {
		return null;
	}

	@Override
	public String getSortingRules() {
		return "";
	}

	@Override
	public File getSource() {
		return mainFile;
	}

	@Override
	public String getVersion() {
		return meta.version;
	}

	@Subscribe
	public void handleModStateEvent(FMLEvent event) {
		if (event.getClass() == FMLInitializationEvent.class) {
			modobj.init((FMLInitializationEvent) event);
		} else if (event.getClass() == FMLPreInitializationEvent.class) {
			modobj.preinit((FMLPreInitializationEvent) event);
		} else if (event.getClass() == FMLPostInitializationEvent.class) {
			modobj.postinit((FMLPostInitializationEvent) event);
		}
	}

	@Override
	public boolean isImmutable() {
		return false;
	}

	@Override
	public boolean isNetworkMod() {
		return false;
	}

	@Override
	public boolean matches(Object mod) {
		return mod == modobj;
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		this.eventBus = bus;
		this.controller = controller;
		eventBus.register(this);
		return true;
	}

	@Override
	public void setEnabledState(boolean enabled) {
		isEnable = enabled;
	}

}
