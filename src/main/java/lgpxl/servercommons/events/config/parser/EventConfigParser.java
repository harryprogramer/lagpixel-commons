package lgpxl.servercommons.events.config.parser;

import lgpxl.servercommons.events.config.PartyConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface EventConfigParser {
    PartyConfig parseConfig(String text) throws IOException;

    PartyConfig parseConfig(File file) throws IOException;

    PartyConfig parseConfig(Path file) throws IOException;

    String getVendor();
}
