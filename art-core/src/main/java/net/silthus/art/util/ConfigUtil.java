package net.silthus.art.util;

import net.silthus.art.api.annotations.Description;
import net.silthus.art.api.annotations.Ignore;
import net.silthus.art.api.annotations.Position;
import net.silthus.art.api.annotations.Required;
import net.silthus.art.api.config.ArtConfigException;
import net.silthus.art.api.config.ConfigFieldInformation;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public final class ConfigUtil {

    public static Map<String, ConfigFieldInformation> getConfigFields(Class<?> configClass) throws ArtConfigException {

        try {
            Constructor<?> constructor = configClass.getConstructor();
            constructor.setAccessible(true);
            return getConfigFields("", configClass, constructor.newInstance());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ArtConfigException(e);
        }
    }

    private static Map<String, ConfigFieldInformation> getConfigFields(String basePath, Class<?> configClass, Object configInstance) throws ArtConfigException {
        Map<String, ConfigFieldInformation> fields = new HashMap<>();

        try {
            Field[] allFields = FieldUtils.getAllFields(configClass);
            for (int i = 0; i < allFields.length; i++) {
                Field field = allFields[i];
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (field.isAnnotationPresent(Ignore.class)) continue;

                String identifier = basePath + field.getName();
                ConfigFieldInformation configInformation = new ConfigFieldInformation(identifier, field.getName(), field.getType());

                if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
                    if (field.isAnnotationPresent(Description.class)) {
                        configInformation.setDescription(field.getAnnotation(Description.class).value());
                    }
                    if (field.isAnnotationPresent(Required.class)) {
                        configInformation.setRequired(true);
                    }
                    if (field.isAnnotationPresent(Position.class)) {
                        configInformation.setPosition(field.getAnnotation(Position.class).value());
                    }

                    field.setAccessible(true);
                    configInformation.setDefaultValue(field.get(configInstance));

                    fields.put(identifier, configInformation);
                } else {
                    fields.putAll(getConfigFields(identifier + ".", field.getType(), field.getType().getConstructor().newInstance()));
                }
            }

            List<ConfigFieldInformation> sameFieldPosition = fields.values().stream().filter(field1 -> fields.values().stream().anyMatch(
                    field2 -> field1 != field2
                            && field1.getPosition() > -1
                            && field2.getPosition() > -1
                            && field1.getPosition() == field2.getPosition()
            )).collect(Collectors.toList());
            if (!sameFieldPosition.isEmpty()) {
                throw new ArtConfigException("found same position " + sameFieldPosition.get(0).getPosition() + " on the following fields: "
                        + sameFieldPosition.stream().map(ConfigFieldInformation::getIdentifier).collect(Collectors.joining(",")));
            }

        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            throw new ArtConfigException(e);
        }

        return fields;
    }

    /**
     * Tries to find the config file containing the given id.
     *
     * @param id id of the ART config
     * @return null if no config file containing the id was found.
     *          the absolute path to the config file if found.
     */
    public static Optional<String> getFileName(String id) {

        try {
            return Files.walk(Path.of(""))
                    .filter(Files::isRegularFile)
                    .filter(file -> containsString(file.toFile(), id))
                    .map(Path::toFile)
                    .map(File::getAbsolutePath)
                    .findFirst();
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static boolean containsString(File file, String string) {
        try {
            Scanner scanner = new Scanner(file);

            //now read the file line by line...
            int lineNum = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNum++;
                if(line.contains(string)) {
                    return true;
                }
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
