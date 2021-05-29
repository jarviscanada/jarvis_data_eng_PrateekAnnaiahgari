package ca.jrvs.apps.twitter.Util;

import ca.jrvs.apps.twitter.example.JsonParser;
import ca.jrvs.apps.twitter.example.dto.Company;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.Arrays;

public class JsonUtil {

  /**
   * Convert a java object to JSON string
   *
   * @param object input object
   * @return JSON String
   * @throws JsonProcessingException
   */
  public static String toJson(Object object, boolean prettyJson, boolean includeNullValues)
      throws JsonProcessingException {
    ObjectMapper m = new ObjectMapper();
    if (!includeNullValues) {
      m.setSerializationInclusion(Include.NON_NULL);
    }
    if (prettyJson) {
      m.enable(SerializationFeature.INDENT_OUTPUT);
    }
    return m.writeValueAsString(object);
  }

  /**
   * Parse JSON string to a object
   *
   * @param json  JSON str
   * @param clazz object class
   * @param <T>   Type
   * @return Object
   * @throws IOException
   */
  public static <T> T toObjectFromJson(String json, Class clazz) throws IOException {
    ObjectMapper m = new ObjectMapper();
    return (T) m.readValue(json, clazz);
  }

  public static <T> T retainFields(Object object, String[] fields) throws JsonProcessingException {
    ObjectMapper m = new ObjectMapper();
    ObjectNode node = m.valueToTree(object);
    node.retain(Arrays.asList(fields));
    return (T) m.treeToValue(node, object.getClass());
  }
}
