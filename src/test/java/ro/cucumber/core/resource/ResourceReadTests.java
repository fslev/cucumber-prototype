package ro.cucumber.core.resource;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import ro.cucumber.core.engineering.utils.JsonUtils;
import ro.cucumber.core.engineering.utils.ResourceUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResourceReadTests {

    @Test
    public void testStringReadFromFile() throws IOException {
        assertEquals("some content 1\nsome content 2\n", ResourceUtils.read("foobar/file1.txt"));
    }

    @Test
    public void testJsonReadingFromFile() throws IOException {
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.putObject("menu").put("show", true);
        assertEquals(json, JsonUtils.toJson(ResourceUtils.read("foobar/file1.json")));
    }

    @Test
    public void testPropertiesReadFromFile() {
        assertEquals("some values with white spaces and new lines \n ",
                ResourceUtils.readProps("foobar/foo.properties").get("IAmAProperty"));
    }

    @Test
    public void testInDepthReadFromDirectory() throws IOException, URISyntaxException {
        Map<String, String> actualData = ResourceUtils.readDirectory("foobar/dir");
        assertEquals(5, actualData.size());
        assertTrue(actualData.get("foobar/dir/foobar1.json").equals("1"));
        assertTrue(actualData.get("foobar/dir/foo/foo1.json").equals("2"));
        assertTrue(actualData.get("foobar/dir/foo/foo2.json").equals("3"));
        assertTrue(actualData.get("foobar/dir/foo/bar/bar1.json").equals("4"));
        assertTrue(actualData.get("foobar/dir/foo/bar/bar2.json").equals("5"));
    }

    @Test(expected = IOException.class)
    public void testInDepthReadFromNonExistentDirectory() throws IOException, URISyntaxException {
        ResourceUtils.readDirectory("non_existent");
    }
}
