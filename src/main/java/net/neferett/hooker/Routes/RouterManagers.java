package net.neferett.hooker.Routes;


import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import net.neferett.hooker.Hooker;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@Data
public class RouterManagers {

    @Delegate
    List<RoutingProperties> routes = new ArrayList<>();

    private List<Class<? extends RoutingProperties>> clazz;

    @SneakyThrows
    private void instantiate(Class<? extends RoutingProperties> clazz) {
        Constructor constructor = clazz.getConstructors()[0];

        RoutingProperties properties = (RoutingProperties) constructor.newInstance();

        properties.setName(clazz.getAnnotation(Route.class).name());
        this.add(properties);
    }

    private void createInstance() {
        this.clazz
                .stream()
                .filter(e -> e.isAnnotationPresent(Route.class) && e.getAnnotation(Route.class).activated())
                .forEach(this::instantiate);
    }

    @SneakyThrows
    private Class<? extends RoutingProperties> buildClass(String name) {
        return (Class<? extends RoutingProperties>) Class.forName("net.neferett.hooker.Routes.Routes." + name);
    }

    public void buildRoutes() {
        this.clazz = new ArrayList<>();
        Hooker.getInstance().getFile().getRoutes().forEach(e -> this.clazz.add(this.buildClass(e)));

        this.createInstance();
    }

}

