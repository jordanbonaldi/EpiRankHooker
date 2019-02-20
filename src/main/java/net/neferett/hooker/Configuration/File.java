package net.neferett.hooker.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.neferett.coreengine.Processors.Config.Config;
import org.graalvm.compiler.api.replacements.Snippet;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class File implements Config {

    @NonNull
    private String channel;

    @NonNull
    private int port;

    @NonNull
    private List<String> routes;

    @NonNull
    private String ip;

    @NonNull
    private String password;

    @NonNull
    private int redisPort;

    @NonNull
    private boolean https;
}
