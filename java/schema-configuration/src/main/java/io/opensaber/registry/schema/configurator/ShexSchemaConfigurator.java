package io.opensaber.registry.schema.configurator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ext.com.google.common.io.ByteStreams;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.opensaber.registry.middleware.util.Constants;
import io.opensaber.registry.middleware.util.Constants.JsonldConstants;
import io.opensaber.registry.middleware.util.JSONUtil;

public class ShexSchemaConfigurator implements ISchemaConfigurator {

	private final static String PREFIX = "teacher:";
	public static final String OPENSABER_PRIVACY_PROPERTY = "opensaber:privateProperties";

	private final ObjectMapper mapper = new ObjectMapper();
	private ObjectNode schemaConfigurationNode;
	private String schemaContent;

	private List<String> foundProperties = new ArrayList<>();

	// TODO: schemaFile location to be passed.
	public ShexSchemaConfigurator(String schemaFile) throws IOException {
		schemaConfigurationNode = loadSchemaConfig(schemaFile);

	}

	@Override
	public boolean isPrivate(String propertyName) {
		foundProperties = getPrivateProperties();
		if (foundProperties.contains(propertyName))
			return true;

		return false;
	}

	@Override
	public boolean isEncrypted(String tailPropertyKey) {
		if (tailPropertyKey != null) {
			return tailPropertyKey.substring(0, Math.min(tailPropertyKey.length(), 9)).equalsIgnoreCase("encrypted");
		} else
			return false;
	}

	@Override
	public String getSchemaContent() {
		return schemaContent;
	}

	private ObjectNode loadSchemaConfig(String schemaFile) throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(schemaFile);
		if (is == null) {
			throw new IOException(Constants.SCHEMA_CONFIGURATION_MISSING);
		}
		schemaContent = new String(ByteStreams.toByteArray(is));
		return (ObjectNode) mapper.readTree(schemaContent);
	}

	private List<String> getPrivateProperties() {
		if (foundProperties.isEmpty()) {
			JSONUtil.trimPrefix(schemaConfigurationNode, PREFIX);
			ArrayNode arrayNode = (ArrayNode) schemaConfigurationNode.get(OPENSABER_PRIVACY_PROPERTY);
			for (int i = 0; i < arrayNode.size(); i++) {
				if (arrayNode.get(i).isObject())
					foundProperties.addAll(arrayNode.get(i).findValuesAsText(JsonldConstants.ID, foundProperties));
			}
		}
		return foundProperties;
	}

}
