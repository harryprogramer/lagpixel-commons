package lgpxl.servercommons.events.config.parser;

import lgpxl.servercommons.events.config.PartyConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractEventParser implements EventConfigParser {
    @Override
    public PartyConfig parseConfig(Path file) throws IOException {
        if(file == null){
            throw new IOException("file is null");
        }

        String content = Files.readString(file);
        return parseConfig(content);
    }

    @Override
    public PartyConfig parseConfig(File file) throws IOException {
        if(file == null){
            throw new IOException("file is null");
        }

        return parseConfig(Path.of(file.getPath()));
    }

    @Override
    public String getVendor() {
        return "StandardConfigParser";
    }
}
