package io.opensaber.registry.util;

import com.fasterxml.jackson.databind.JsonNode;
import io.opensaber.registry.middleware.util.DateUtil;
import io.opensaber.registry.middleware.util.OSSystemFields;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OSSystemFieldsHelper {

    private static Logger logger = LoggerFactory.getLogger(OSSystemFieldsHelper.class);

    @Autowired
    private DefinitionsManager definitionsManager;

    /**
     * ensure the system fields(createdAt, createdBy) at time of adding a fresh record/node
     * 
     * @param title
     * @param node
     * @param userId
     */
    public void ensureCreateAuditFields(String title, JsonNode node, String userId) {
        List<String> systemFields = getSystemFields(title);
        String timeStamp = DateUtil.instantTimeStamp();
        for (String field : systemFields) {
            addSystemProperty(field, node, userId, timeStamp);
        }
    }

    /**
     * ensure the system fields(updatedAt, updatedBy) at time of updating a record/node
     * 
     * @param title
     * @param node
     * @param userId
     */
    public void ensureUpdateAuditFields(String title, JsonNode node, String userId) {
        List<String> systemFields = getSystemFields(title);
        String timeStamp = DateUtil.instantTimeStamp();
        for (String field : systemFields) {
            addSystemProperty(field, node, userId, timeStamp);

        }
    }

    /**
     * adds a system property to given node
     * 
     * @param field       propertyName
     * @param node
     * @param userId
     * @param timeStamp 
     */
    public void addSystemProperty(String field, JsonNode node, String userId, String timeStamp) {
        try {
            switch (OSSystemFields.getByValue(field)) {
                case _osCreatedAt:
                    OSSystemFields._osCreatedAt.createdAt(node, timeStamp);
                    break;
                case _osCreatedBy:
                    OSSystemFields._osCreatedBy.createdBy(node, userId);
                    break;
                case _osUpdatedAt:
                    OSSystemFields._osUpdatedAt.updatedAt(node, timeStamp);
                    break;
                case _osUpdatedBy:
                    OSSystemFields._osUpdatedBy.updatedBy(node, userId);
                    break;
            }
        } catch (Exception e) {
            logger.error("Audit field - {} not valid!", field);
        }
    }

    public List<String> getSystemFields(String definitionName) {
        Definition def = definitionsManager.getDefinition(definitionName);
        return def != null ? def.getOsSchemaConfiguration().getSystemFields() : new ArrayList<>();

    }

}
