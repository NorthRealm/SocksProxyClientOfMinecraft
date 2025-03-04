package crimsonedgehope.minecraft.fabric.socksproxyclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import crimsonedgehope.minecraft.fabric.socksproxyclient.Constants;
import crimsonedgehope.minecraft.fabric.socksproxyclient.SocksProxyClient;
import crimsonedgehope.minecraft.fabric.socksproxyclient.config.entry.SocksProxyClientConfigEntry;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public abstract class SocksProxyClientConfig {

    public static final Logger LOGGER = SocksProxyClient.getLogger("Config");

    protected static Path configPathDir() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(Constants.MOD_ID);
        File file = path.toFile();
        if (!path.toFile().exists()) {
            file.mkdirs();
        }
        return path;
    }

    @Getter
    private final File configFile;

    protected SocksProxyClientConfig(String filename) {
        this(configPathDir().resolve(filename).toFile());
    }

    protected SocksProxyClientConfig(File configFile) {
        this.configFile = configFile;
    }

    public abstract JsonObject defaultEntries();
    public abstract JsonObject toJsonObject();
    public abstract void fromJsonObject(JsonObject object);

    public void load() {
        LOGGER.info("Reading config file {}", this.configFile.getName());
        if (!this.configFile.exists()) {
            writeConfigFile(defaultEntries());
        }
        try {
            readConfigFile();
        } catch (Exception e) {
            LOGGER.error("Error reading config file " + this.configFile.getName(), e);
        }
    }

    public void save() {
        writeConfigFile(toJsonObject());
    }

    private FileReader readFile(File file) throws IOException {
        return readFile(file, StandardCharsets.UTF_8);
    }

    private FileReader readFile(File file, Charset charset) throws IOException {
        return new FileReader(file, charset);
    }

    private FileWriter writeFile(File file, boolean append) throws IOException {
        return new FileWriter(file, append);
    }

    private FileWriter writeFile(File file, String content) throws IOException {
        return writeFile(file, content, false);
    }

    private FileWriter writeFile(File file, String content, boolean append) throws IOException {
        FileWriter writer = writeFile(file, append);
        writer.write(content);
        return writer;
    }

    private void readConfigFile() throws IOException {
        FileReader reader = readFile(this.configFile);
        Gson gson = new Gson();
        JsonObject object = gson.fromJson(new JsonReader(reader), JsonObject.class);
        parseConfigJson(object);
    }

    private void parseConfigJson(JsonObject object) {
        final JsonObject defaults = defaultEntries();
        if (object == null || object.size() == 0) {
            writeConfigFile(defaults);
            load();
            return;
        }
        LOGGER.info("Parsing config json {}", this.configFile.getName());
        boolean reload = false;
        try {
            for (Map.Entry<String, JsonElement> entry : defaults.entrySet()) {
                if (!object.has(entry.getKey())) {
                    object.add(entry.getKey(), entry.getValue());
                    reload = true;
                }
            }
            if (reload) {
                writeConfigFile(object);
                load();
                return;
            }
        } catch (Exception e) {
            LOGGER.error("Error reading config json " + this.configFile.getName(), e);
        }
        fromJsonObject(object);
    }

    private void writeConfigFile(JsonObject entries) {
        try (FileWriter writer = writeFile(this.configFile, false)) {
            LOGGER.info("Writing config to file {}", this.configFile.getName());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(entries));
        } catch (Exception e) {
            LOGGER.error("Error writing config to file " + this.configFile.getName(), e);
        }
    }

    protected Predicate<Field> entryVariablesListFilter =
            field -> SocksProxyClientConfigEntry.class.isAssignableFrom(field.getType());

    public List<SocksProxyClientConfigEntry<?>> entryFields(final Predicate<Field> listFilter) throws Exception {
        List<SocksProxyClientConfigEntry<?>> entries = new ArrayList<>();
        List<Field> fields = Arrays.stream(this.getClass().getDeclaredFields()).filter(listFilter).toList();
        for (Field field : fields) {
            SocksProxyClientConfigEntry<?> entry = (SocksProxyClientConfigEntry<?>) field.get(null);
            Class<?> clazz = entry.getDefaultValue().getClass();
            if (Integer.class.isAssignableFrom(clazz)
                    || Boolean.class.isAssignableFrom(clazz)
                    || String.class.isAssignableFrom(clazz)
                    || Enum.class.isAssignableFrom(clazz)
                    || List.class.isAssignableFrom(clazz)) {
                entries.add(entry);
            } else {
                throw new UnsupportedOperationException("Not using \"" + clazz.getName() + "\"!");
            }
        }
        return entries;
    }

    public SocksProxyClientConfigEntry<?> getEntryField(final String fieldName) throws Exception {
        return entryFields(field -> entryVariablesListFilter.test(field) && field.getName().equals(fieldName)).get(0);
    }

    public <T> SocksProxyClientConfigEntry<T> getEntryField(final String fieldName, final Class<T> valueType) throws Exception {
        return (SocksProxyClientConfigEntry<T>) entryFields(field -> {
                    try {
                        field.setAccessible(true);
                        return entryVariablesListFilter.test(field)
                            && field.getName().equals(fieldName)
                            && valueType.isAssignableFrom(((SocksProxyClientConfigEntry<?>)field.get(null)).getDefaultValue().getClass());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
        ).get(0);
    }
}
