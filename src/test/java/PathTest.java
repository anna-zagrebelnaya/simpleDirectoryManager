import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PathTest {

    @Test
    public void cd_root() {
        Path path = new Path("/a/b/c/d");
        path.cd("/");

        assertEquals("/", path.getPath());
    }

    @Test
    public void cd_relational() {
        Path path = new Path("/a/b/c/d");
        path.cd("../../x");

        assertEquals("/a/b/x", path.getPath());
    }

    @Test
    public void cd_absolute() {
        Path path = new Path("/a/b/c/d");
        path.cd("/a/b/c");

        assertEquals("/a/b/c", path.getPath());
    }

    @Test
    public void cd_absoluteAndRelational() {
        Path path = new Path("/a/b/c/d");
        path.cd("/a/b/../x");

        assertEquals("/a/x", path.getPath());
    }
}