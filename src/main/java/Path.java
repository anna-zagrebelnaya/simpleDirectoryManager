import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Path {
    private static final String ROOT_PATH = "/";
    private static final String PATH_SEPARATOR = "/";
    private static final String PARENT_DIRECTIVE = "..";
    private static final String PATH_REGEX = "((\\/?\\.\\.\\/?)?(\\/?[A-z])?)";

    private String path;

    public Path(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void cd(String newPath) {
        try {
            List<String> newPathDirectives = parsePath(newPath);
            List<String> updatedPath = defineRoot(newPathDirectives);
            for (String pathDirective: newPathDirectives) {
                if (PARENT_DIRECTIVE.equals(pathDirective)) {
                    validatePathIsNotRoot(updatedPath);
                    updatedPath.remove(updatedPath.size()-1);
                } else {
                    updatedPath.add(pathDirective);
                }
            }
            path = buildPath(updatedPath);
        } catch (InvalidPathException ex) {
            System.out.println("Can not change directory: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        Path path = new Path("/a/b/c/d");
        path.cd("a/b");
        System.out.println(path.getPath());
    }

    private class InvalidPathException extends Exception {
        public InvalidPathException(String message) {
            super(message);
        }
    }

    private List<String> parsePath(String path) throws InvalidPathException {
        if (ROOT_PATH.equals(path)) {
            return new ArrayList<>();
        }
        Pattern pattern = Pattern.compile(PATH_REGEX);
        Matcher matcher = pattern.matcher(path);
        List<String> directives = new ArrayList<>();
        while (matcher.find()) {
            String parentGroup = matcher.group(2);
            if (parentGroup != null) {
                directives.add(parentGroup.replace(PATH_SEPARATOR, ""));
            }
            String directoryGroup = matcher.group(3);
            if (directoryGroup != null) {
                directives.add(directoryGroup.replace(PATH_SEPARATOR, ""));
            }
        }
        if (directives.isEmpty()) {
            throw new InvalidPathException("New path is incorrect");
        }
        return directives;
    }

    private List<String> defineRoot(List<String> newPathDirectives) {
        try {
            return !isRootPath(newPathDirectives) && isRelative(newPathDirectives)
                    ? parsePath(path)
                    : new ArrayList<>();
        } catch (InvalidPathException e) {
            throw new RuntimeException("Incorrect state");
        }
    }

    private boolean isRootPath(List<String> path) {
        return path.isEmpty();
    }

    private boolean isRelative(List<String> path) {
        return PARENT_DIRECTIVE.equals(path.get(0));
    }

    private void validatePathIsNotRoot(List<String> path) throws InvalidPathException {
        if (isRootPath(path)) {
            throw new InvalidPathException("Path is a root");
        }
    }

    private String buildPath(List<String> pathParts) {
        return pathParts.stream()
                .collect(Collectors.joining(PATH_SEPARATOR, ROOT_PATH, ""));
    }
}
